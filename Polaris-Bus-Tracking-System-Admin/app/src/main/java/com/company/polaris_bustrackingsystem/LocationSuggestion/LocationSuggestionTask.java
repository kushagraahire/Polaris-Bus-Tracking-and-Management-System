package com.company.polaris_bustrackingsystem.LocationSuggestion;

import android.content.Context;
import android.database.MatrixCursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.widget.SearchView;

import java.io.IOException;
import java.util.List;

public class LocationSuggestionTask extends AsyncTask<String,Void, List<Address>> {
    private Context mContext;
    private SearchView mSearchView;

    public LocationSuggestionTask(Context context, SearchView searchView) {
        mContext = context;
        mSearchView = searchView;
    }

    @Override
    protected List<Address> doInBackground(String... strings) {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(mContext);
        try {
            addresses = geocoder.getFromLocationName(strings[0], 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    @Override
    protected void onPostExecute(List<Address> addresses) {
        if (addresses != null && !addresses.isEmpty()) {
            MatrixCursor cursor = new MatrixCursor(new String[] { BaseColumns._ID, "location_name" });
            int i = 0;
            for (Address address : addresses) {
                cursor.addRow(new Object[] { i, address.getAddressLine(0) });
                i++;
            }
            LocationSuggestionAdapter adapter = new LocationSuggestionAdapter(mContext,
                    android.R.layout.simple_dropdown_item_1line, cursor);
            mSearchView.setSuggestionsAdapter(adapter);
        }
    }
}
