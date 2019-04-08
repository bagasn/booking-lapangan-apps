package com.example.fauzifahrizal.lapangan;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fauzifahrizal.lapangan.adapter.ListJadwalAdapter;
import com.example.fauzifahrizal.lapangan.core.AppHelper;
import com.example.fauzifahrizal.lapangan.connection.RetrofitApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JadwalActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "JadwalActivity";

    private TextView textTanggal;

    private ListView listJadwal;

    private Calendar mCalendar;

    private ProgressDialog dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal);

        textTanggal = (TextView) findViewById(R.id.text_tanggal);
        textTanggal.setOnClickListener(this);

        listJadwal = (ListView) findViewById(R.id.list_jadwal);

        dialogLoading = new ProgressDialog(this);
        dialogLoading.setMessage("Loading...");
        dialogLoading.setCancelable(false);

        mCalendar = Calendar.getInstance();

        setValueTextTanggal();

        getJadwalLapangan();

    }

    @Override
    public void onClick(View view) {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                mCalendar.set(i, i1, i2);

                setValueTextTanggal();

                getJadwalLapangan();
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void getJadwalLapangan() {

        DateFormat formater = new SimpleDateFormat("yyyy-M-d", Locale.US);

        String tanggal = formater.format(mCalendar.getTime());

        dialogLoading.show();

        RetrofitApi.open().getJadwal(tanggal)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            try {

                                String stringBody = response.body().string();

                                JSONObject json = new JSONObject(stringBody);

                                String code = json.getString("code");

                                if (code.equals("0000")) {

                                    JSONArray array = json.getJSONArray("jadwal");

                                    addToList(array);

                                } else {
                                    AppHelper.toast(getApplicationContext(), json.getString("message"));
                                }

                            } catch (IOException | JSONException e) {
                                Log.e(TAG, "onResponse: ", e);
                            }

                        } else {
                            AppHelper.toast(getApplicationContext(), response);
                        }

                        dialogLoading.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppHelper.toast(getApplicationContext(), t);

                        dialogLoading.dismiss();
                    }
                });


    }

    private void setValueTextTanggal() {
        DateFormat to = new SimpleDateFormat("d MMM yyyy", Locale.US);

        String tanggal = to.format(mCalendar.getTime());

        textTanggal.setText(tanggal);
    }

    private void addToList(JSONArray array) throws JSONException {
        List<JSONObject> jsons = new ArrayList<>();

        if (array.length() > 0) {

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                jsons.add(object);
            }

        }

        ListJadwalAdapter adapter = new ListJadwalAdapter(this, jsons);
        listJadwal.setAdapter(adapter);
    }

}
