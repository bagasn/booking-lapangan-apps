package com.example.fauzifahrizal.lapangan;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.fauzifahrizal.lapangan.connection.RetrofitApi;
import com.example.fauzifahrizal.lapangan.core.AppHelper;
import com.example.fauzifahrizal.lapangan.core.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private TextView textNama;
    private TextView textEmail;
    private TextView textPhone;

    private View layoutLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textNama = (TextView) findViewById(R.id.text_nama);
        textEmail = (TextView) findViewById(R.id.text_email);
        textPhone = (TextView) findViewById(R.id.text_handphone);

        layoutLoading = findViewById(R.id.layout_loading);

        getDataProfil();

    }

    private void getDataProfil() {

        showLoading(true);

        SharedPreferences sp = getSharedPreferences(Config.SP_MASTER, Context.MODE_PRIVATE);

        int user = sp.getInt(Config.SP_USER_ID, -1);

        if (user == -1) {
            AppHelper.toast(getApplicationContext(), "Required data not found");
            return;
        }

        RetrofitApi.open().getProfil(user)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            try {

                                String body = response.body().string();

                                JSONObject json = new JSONObject(body);

                                String code = json.getString("code");

                                if (code.equalsIgnoreCase("0000")) {

                                    JSONObject o = json.getJSONObject("data");

                                    textNama.setText(o.getString("nama"));
                                    textEmail.setText(o.getString("email"));
                                    textPhone.setText(o.getString("telepon"));

                                } else {
                                    AppHelper.toast(getApplicationContext(), json.getString("message"));
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

}
