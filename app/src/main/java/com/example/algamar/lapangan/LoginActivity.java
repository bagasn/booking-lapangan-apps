package com.example.algamar.lapangan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.algamar.lapangan.core.Config;
import com.example.algamar.lapangan.connection.RetrofitApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText editTextUsername;
    private EditText editTextPassword;

    private ProgressDialog dialog;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences(Config.SP_MASTER, Context.MODE_PRIVATE);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Logging...");
        dialog.setCancelable(false);

        editTextUsername = (EditText) findViewById(R.id.txt_username);
        editTextPassword = (EditText) findViewById(R.id.txt_password);

        Button buttonLogin = (Button) findViewById(R.id.btn_Login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        Button buttonRegist = (Button) findViewById(R.id.btn_Regist);
        buttonRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRegist = new Intent(getApplicationContext(), RegistrasiActivity.class);
                startActivity(intentRegist);
            }
        });

        if (sp.getBoolean(Config.SP_LOGIN, false)) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void login() {

        String username = editTextUsername.getText().toString().trim();
        if (username.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username belum diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = editTextPassword.getText().toString().trim();
        if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Password belum diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody reqUsername = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody reqPassword = RequestBody.create(MediaType.parse("text/plain"), password);

        dialog.show();

        RetrofitApi.open().login(reqUsername, reqPassword)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            try {

                                String stringResponse = response.body().string();

                                JSONObject json = new JSONObject(stringResponse);

                                String code = json.getString("code");

                                String message;

                                if (code.equals("0000")) {
                                    JSONObject data = json.getJSONObject("data");

                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putBoolean(Config.SP_LOGIN, true);
                                    editor.putInt(Config.SP_USER_ID, data.getInt("id"));
                                    editor.putString(Config.SP_NAMA, data.getString("nama"));
                                    editor.putString(Config.SP_EMAIL, data.getString("email"));
                                    editor.putString(Config.SP_TELEPHONE, data.getString("telepon"));

                                    editor.commit();

                                    message = "Login berhasil";

                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    message = json.getString("message");
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

