package com.example.fauzifahrizal.lapangan.core;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class AppHelper {

    private static final String TAG = "AppHelper";

    public static void toast(Context context, String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "toast: ", e);
        }
    }

    public static void toast(Context context, Response<?> response) {
        try {
            Toast.makeText(context, "Error [" + response.code() + "]", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "toast: ", e);
        }
    }

    public static void toast(Context context, Throwable throwable) {
        try {
            Toast.makeText(context, throwable.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "toast: ", e);
        }
    }

}
