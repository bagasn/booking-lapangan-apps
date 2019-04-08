package com.example.fauzifahrizal.lapangan;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fauzifahrizal.lapangan.connection.RetrofitApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrasiActivity extends AppCompatActivity {

    private static final String TAG = "RegistrasiActivity";

    private EditText textName;
    private EditText textEmail;
    private EditText textPassword;
    private EditText texTlp;

    private Button btnRegist;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        textName = (EditText) findViewById(R.id.text_name);
        textEmail = (EditText) findViewById(R.id.text_email);
        textPassword = (EditText) findViewById(R.id.text_password);
        texTlp = (EditText) findViewById(R.id.text_tlp);

        btnRegist = (Button) findViewById(R.id.btn_Reg);
        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRegist();
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
    }

    private void submitRegist() {

        String nama = textName.getText().toString();
        if (nama.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Nama Belum Di Isi", Toast.LENGTH_LONG).show();
            return;
        }
        String email = textEmail.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Email Belum Di Isi", Toast.LENGTH_LONG).show();
            return;
        }
        String password = textPassword.getText().toString();
        if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Password Belum Di Isi", Toast.LENGTH_LONG).show();
            return;
        }
        String telphone = texTlp.getText().toString();
        if (telphone.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No.Telphone Belum Di Isi", Toast.LENGTH_LONG).show();
            return;
        }

        dialog.show();

        RequestBody reqNama = RequestBody.create(MediaType.parse("text/plain"), nama);
        RequestBody reqEmail = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody reqTelepon = RequestBody.create(MediaType.parse("text/plain"), telphone);
        RequestBody reqPassword = RequestBody.create(MediaType.parse("text/plain"), password);

        RetrofitApi.open().regis(reqNama, reqEmail, reqTelepon, reqPassword)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            try {

                                String stringResponse = response.body().string();

                                JSONObject object = new JSONObject(stringResponse);

                                String code = object.getString("code");
                                String message = object.getString("message");

                                if (code.equals("0000")) {
                                    finish();
                                }

                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                                        .show();

                            } catch (JSONException | IOException e) {
                                Log.e(TAG, "onResponse: ", e);
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Error [" + response.code() + "]", Toast.LENGTH_SHORT)
                                    .show();
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT)
                                .show();

                        dialog.dismiss();
                    }
                });

    }
}
