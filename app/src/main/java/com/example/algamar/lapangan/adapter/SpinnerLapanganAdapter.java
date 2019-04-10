package com.example.algamar.lapangan.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.algamar.lapangan.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SpinnerLapanganAdapter extends ArrayAdapter<JSONObject> {

    private static final String TAG = "SpinnerLapanganAdapter";

    private static final int LAYOUT_RESOURCE = R.layout.row_spinner_item;

    public SpinnerLapanganAdapter(@NonNull Context context, @NonNull List<JSONObject> objects) {
        super(context, LAYOUT_RESOURCE, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(LAYOUT_RESOURCE, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.text1);

        try {

            JSONObject json = getItem(position);

            String title = json.getString("lapangan");

            textView.setText(title);
        } catch (JSONException e){}

        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(LAYOUT_RESOURCE, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.text1);

        try {

            JSONObject json = getItem(position);

            String title = json.getString("lapangan");

            textView.setText(title);
        } catch (JSONException e){}

        return view;
    }
}
