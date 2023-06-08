package com.company.polaris_bustrackingsystem.LocationSuggestion;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class LocationSuggestionAdapter extends ResourceCursorAdapter {

    public LocationSuggestionAdapter(Context context, int layout, Cursor c) {
        super(context, layout, c, 0);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(cursor.getString(cursor.getColumnIndexOrThrow("location_name")));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setPadding(20, 20, 20, 20);
        return view;
    }
}
