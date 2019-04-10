package com.example.algamar.lapangan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.support.v7.app.AppCompatActivity;

import com.example.algamar.lapangan.core.Config;

public class HomeActivity extends AppCompatActivity {

    private View layoutProfile;
    private View layoutLapangan;
    private View layoutBooking;
    private View layoutListBooking;
    private View layoutJadwal;
    private View layoutLogout;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1);

        sp = getSharedPreferences(Config.SP_MASTER, Context.MODE_PRIVATE);

        layoutProfile = findViewById(R.id.profile);

        layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intenProfile = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intenProfile);
            }
        });

        layoutLapangan = findViewById(R.id.lapangan);

        layoutLapangan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intenLapangan = new Intent(getApplicationContext(), LapanganActivity.class);
                startActivity(intenLapangan);
            }
        });

        layoutBooking = findViewById(R.id.booking);

        layoutBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intenBok = new Intent(getApplicationContext(), BookingActivity.class);
                startActivity(intenBok);
            }
        });

        layoutListBooking = findViewById(R.id.listbook);

        layoutListBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intenList = new Intent(getApplicationContext(), ListBookingActivity.class);
                startActivity(intenList);
            }
        });

        layoutJadwal = findViewById(R.id.jadwal);

        layoutJadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intenJadwal = new Intent(getApplicationContext(), JadwalActivity.class);
                startActivity(intenJadwal);
            }
        });

        layoutLogout = findViewById(R.id.logout);

        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().clear().apply();

                Intent intenLogout = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intenLogout);
                finish();
            }
        });

    }


}
