package com.example.algamar.lapangan;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.algamar.lapangan.adapter.SpinnerJamAdapter;
import com.example.algamar.lapangan.adapter.SpinnerLapanganAdapter;
import com.example.algamar.lapangan.core.AppHelper;
import com.example.algamar.lapangan.connection.RetrofitApi;
import com.example.algamar.lapangan.core.Config;

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

public class BookingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BookingActivity";

    private TextView textTanggal;

    private Spinner spinnerLapangan;
    private Spinner spinnerJam;

    private Button btnBooking;

    private ProgressDialog dialogLoading;

    private Calendar mCalendar;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        sp = getSharedPreferences(Config.SP_MASTER, Context.MODE_PRIVATE);

        textTanggal = (TextView) findViewById(R.id.text_tanggal);
        spinnerJam = (Spinner) findViewById(R.id.spinner_jam);
        spinnerLapangan = (Spinner) findViewById(R.id.spinner_lapangan);
        btnBooking = (Button) findViewById(R.id.btn_Booking);

        dialogLoading = new ProgressDialog(this);
        dialogLoading.setMessage("Loading...");
        dialogLoading.setCancelable(false);

        textTanggal.setOnClickListener(this);
        btnBooking.setOnClickListener(this);

        mCalendar = Calendar.getInstance();

        setValueTextTanggal();

        spinnerLapangan.setOnItemSelectedListener(onSelectedListener);

        getLapangan();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Booking:
                onCooking();
                break;
            case R.id.text_tanggal:
                showDialogPicker();
                break;
        }
    }

    private void onCooking() {
        String tanggal = "";
        int lapangan = -1;
        int jam = -1;

        DateFormat formater = new SimpleDateFormat("yyyy-M-d", Locale.US);

        if (mCalendar == null) {
            AppHelper.toast(getApplicationContext(), "tanggal belum dipilih");
            return;
        }

        tanggal = formater.format(mCalendar.getTime());

        try {
            JSONObject json = ((JSONObject) spinnerLapangan.getSelectedItem());

            lapangan = json.getInt("id");
        } catch (JSONException e) {

        }

        if (lapangan == -1) {
            AppHelper.toast(getApplicationContext(), "lapangan belum dipilih");
            return;
        }

        try {
            JSONObject json = ((JSONObject) spinnerJam.getSelectedItem());

            jam = json.getInt("id");
        } catch (JSONException e) {

        }

        if (jam == -1) {
            AppHelper.toast(getApplicationContext(), "jam belum dipilih");
            return;
        }

        Integer userid = sp.getInt(Config.SP_USER_ID, 0);

        RetrofitApi.open().putBooking(userid, tanggal, lapangan, jam)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            try {

                                String string = response.body().string();

                                JSONObject json = new JSONObject(string);

                                String code = json.getString("code");

                                if (code.equals("0000")) {
                                    onBoookingSucces();
                                }
                                AppHelper.toast(getApplicationContext(), json.getString("message"));

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

    private void onBoookingSucces() {
        setResult(RESULT_OK);
        finish();
    }

    private void getJamLapangan(int id) {

        if (id == -1) {
            JSONObject object = ((JSONObject) spinnerLapangan.getSelectedItem());

            if (object != null) {
                try {
                    id = object.getInt("id");
                } catch (JSONException e) {
                    AppHelper.toast(getApplicationContext(), e);
                }
            }
        }

        if (id == -1) {
            AppHelper.toast(getApplicationContext(), "Jam tidak valid");
            return;
        }

        String tanggal = "";

        DateFormat to = new SimpleDateFormat("yyyy-M-d", Locale.US);

        if (mCalendar == null)
            return;

        tanggal = to.format(mCalendar.getTime());

        RetrofitApi.open().getJamBooking(tanggal, id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            try {

                                String bodai = response.body().string();

                                JSONObject json = new JSONObject(bodai);

                                String code = json.getString("code");

                                if (code.equals("0000")) {

                                    JSONArray array = json.getJSONArray("jams");

                                    addToJam(array);

                                } else {
                                    AppHelper.toast(getApplicationContext(), json.getString("message"));
                                }

                            } catch (IOException | JSONException | NullPointerException e) {
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

    private void showDialogPicker() {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                mCalendar.set(i, i1, i2);

                setValueTextTanggal();

                try {
                    JSONObject json = ((JSONObject) spinnerLapangan.getSelectedItem());

                    int id = json.getInt("id");

                    if (id != -1) {
                        getJamLapangan(id);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "onDateSet: ", e);
                }
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void getLapangan() {

        dialogLoading.show();

        RetrofitApi.open().getLapangan()
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {

                                String stringBody = response.body().string();

                                JSONObject json = new JSONObject(stringBody);

                                String code = json.getString("code");

                                if (code.equals("0000")) {

                                    JSONArray array = json.getJSONArray("lapangans");

                                    addLapangan(array);

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
        DateFormat to = new SimpleDateFormat("EEEE',' d MMMM yyyy", Locale.US);

        String tanggal = to.format(mCalendar.getTime());

        textTanggal.setText(tanggal);
    }

    private void addToJam(JSONArray array) throws JSONException {
        List<JSONObject> jsons = new ArrayList<>();

        JSONObject first = new JSONObject();
        first.put("waktu", "- Pilih Jam -");
        first.put("id", -1);

        jsons.add(first);

        if (array.length() > 0) {

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                jsons.add(object);
            }

        }

        SpinnerJamAdapter adapter = new SpinnerJamAdapter(this, jsons);
        spinnerJam.setAdapter(adapter);
    }

    private void addLapangan(JSONArray array) throws JSONException {
        List<JSONObject> jsons = new ArrayList<>();

        if (array.length() > 0) {

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                jsons.add(object);
            }

        }

        SpinnerLapanganAdapter adapter = new SpinnerLapanganAdapter(this, jsons);
        spinnerLapangan.setAdapter(adapter);

    }

    private AdapterView.OnItemSelectedListener onSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            JSONObject object = ((JSONObject) adapterView.getSelectedItem());

            if (object != null) {
                int id;
                try {
                    id = object.getInt("id");
                    getJamLapangan(id);
                } catch (JSONException e) {
                    AppHelper.toast(getApplicationContext(), e);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

}
