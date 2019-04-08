package com.example.fauzifahrizal.lapangan;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fauzifahrizal.lapangan.connection.RetrofitApi;
import com.example.fauzifahrizal.lapangan.core.AppHelper;
import com.example.fauzifahrizal.lapangan.core.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fauzi fahrizal on 9/16/2018.
 */

public class ListBookingActivity extends AppCompatActivity {

    private static final String TAG = "ListBooking";

    private View layoutLoading;
    private ListView listView;

    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listbooking);

        sp = getSharedPreferences(Config.SP_MASTER, Context.MODE_PRIVATE);

        listView = (ListView) findViewById(R.id.list_content);
        layoutLoading = findViewById(R.id.layout_loading);

        getDataListBooking();

    }

    private void getDataListBooking() {

        int userid = sp.getInt(Config.SP_USER_ID, -1);

        if (userid == -1) {
            AppHelper.toast(getApplicationContext(), "Required data not found");
            return;
        }

        RetrofitApi.open().getListBooking(userid)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            try {

                                String body = response.body().string();

                                JSONObject json = new JSONObject(body);

                                String code = json.getString("code");

                                if (code.equalsIgnoreCase("0000")) {

                                    setValuesBooking(json.getJSONArray("data"));

                                } else {

                                    AppHelper.toast(getApplicationContext(), response);

                                }

                            } catch (IOException | JSONException e) {
                                Log.e(TAG, "onResponse: ", e);
                            }

                        } else {
                            AppHelper.toast(getApplicationContext(), response);
                        }

                        showLoading(false);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppHelper.toast(getApplicationContext(), t);

                        showLoading(false);
                    }
                });

    }

    private void setValuesBooking(JSONArray array) throws JSONException {

        if (array.length() > 0) {

            List<JSONObject> listJson = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {

                JSONObject object = array.getJSONObject(i);

                listJson.add(object);
            }

            ListBookingAdapter adapter = new ListBookingAdapter(this, listJson);
            listView.setAdapter(adapter);

        }

    }

    private void showLoading(boolean show) {
        try {

            if (show) {
                layoutLoading.setVisibility(View.VISIBLE);
            } else {
                layoutLoading.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.e(TAG, "showLoading: ", e);
        }

    }

    class ListBookingAdapter extends ArrayAdapter<JSONObject> {

        public ListBookingAdapter(@NonNull Context context, @NonNull List<JSONObject> objects) {
            super(context, R.layout.row_list_content_booking, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.row_list_content_booking, parent, false);

            TextView textLapangan = (TextView) view.findViewById(R.id.text_lapangan);
            TextView textTanggal = (TextView) view.findViewById(R.id.text_tanggal);
            TextView textJam = (TextView) view.findViewById(R.id.text_jam);

            TextView textStatus = (TextView) view.findViewById(R.id.text_status);

            try {

                JSONObject json = getItem(position);

                String lapangan = json.getString("lapangan");
                if (lapangan != null)
                    textLapangan.setText(lapangan);

                try {

                    String tanggal = json.getString("tanggal");

                    Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(tanggal);

                    tanggal = new SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(date);

                    textTanggal.setText(tanggal);

                } catch (ParseException | JSONException e) {
                    Log.e(TAG, "getView: ", e);
                }

                String jam = json.getString("waktu");
                if (jam != null)
                    textJam.setText(jam);

                String status = json.getString("status");
                switch (status) {
                    case "1":
                        textStatus.setText("Booking");
                        textStatus.setTextColor(Color.parseColor("#1565c0"));
                        break;
                    case "2":
                        textStatus.setText("Selesai");
                        textStatus.setTextColor(Color.parseColor("#43a047"));
                        break;
                    case "3":
                        textStatus.setText("Batal");
                        textStatus.setTextColor(Color.parseColor("#d50000"));
                        break;
                    default:
                        textStatus.setText("Unknown");
                        textStatus.setTextColor(Color.parseColor("#424242"));
                        break;
                }


            } catch (JSONException e) {
                Log.e(TAG, "getView: ", e);
            }

            return view;
        }
    }
}
