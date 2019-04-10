package com.example.algamar.lapangan.connection;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitService {

    @Multipart
    @POST("regis.php")
    Call<ResponseBody> regis(
            @Part("nama") RequestBody nama,
            @Part("email") RequestBody email,
            @Part("telepon") RequestBody telepon,
            @Part("password") RequestBody password
    );

    @Multipart
    @POST("login.php")
    Call<ResponseBody> login(
            @Part("email") RequestBody username,
            @Part("password") RequestBody password
    );

    @GET("getJadwal.php")
    Call<ResponseBody> getJadwal(
            @Query("tanggal") String tanggal
    );

    @GET("getLapangan.php")
    Call<ResponseBody> getLapangan();

    @GET("getJamBooking.php")
    Call<ResponseBody> getJamBooking(
            @Query("tanggal") String tanggal,
            @Query("lapangan") Integer lapangan
    );

    @POST("putBooking.php")
    Call<ResponseBody> putBooking(
            @Query("userid") Integer userid,
            @Query("tanggal") String tanggal,
            @Query("lapangan") Integer lapangan,
            @Query("jam") Integer jam
    );

    @GET("getListBooking.php")
    Call<ResponseBody> getListBooking(
            @Query("userid") Integer userid
    );

    @GET("getProfil.php")
    Call<ResponseBody> getProfil(
            @Query("userid") Integer userid
    );
}

