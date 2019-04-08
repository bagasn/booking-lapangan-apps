package com.example.fauzifahrizal.lapangan.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.fauzifahrizal.lapangan.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ListJadwalAdapter extends ArrayAdapter<JSONObject> {

    private static final String TAG = "ListJadwalAdapter";

    private static final int LAYOUT_RESOURCE = R.layout.row_list_jadwal;

    public ListJadwalAdapter(@NonNull Context context, @NonNull List<JSONObject> objects) {
        super(context, LAYOUT_RESOURCE, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(LAYOUT_RESOURCE, parent, false);

        TextView textLapangan = (TextView) view.findViewById(R.id.text_lapangan);
        TextView textJam = (TextView) view.findViewById(R.id.text_jam);

        try {
            JSONObject item = getItem(position);

            if (item != null) {
                textLapangan.setText(item.getString("lapangan"));
                textJam.setText(item.getString("jam"));
            }

        } catch (JSONException e) {
            Log.e(TAG, "getView: ", e);
        }

        return view;
    }

}
