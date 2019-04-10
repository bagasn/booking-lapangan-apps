package com.example.algamar.lapangan.connection;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApi {

//    private static final String BASE_URL = "http://192.168.0.2:8080/badminton/api/";
    private static final String BASE_URL = "https://siakad-skripsi.000webhostapp.com/vertova/api/";

    private static Retrofit getRetrofit() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(220, TimeUnit.SECONDS);
        builder.readTimeout(220, TimeUnit.SECONDS);
        builder.writeTimeout(220, TimeUnit.SECONDS);

        builder.addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(builder.build())
                .build();

        return retrofit;
    }

    public static RetrofitService open() {
        return getRetrofit().create(RetrofitService.class);
    }

}
