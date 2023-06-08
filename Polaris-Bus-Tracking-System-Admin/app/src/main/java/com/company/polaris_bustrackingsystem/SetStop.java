package com.company.polaris_bustrackingsystem;

import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import com.company.polaris_bustrackingsystem.LocationSuggestion.LocationSuggestionTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.company.polaris_bustrackingsystem.databinding.ActivitySetStopBinding;

import java.io.IOException;
import java.util.List;

public class SetStop extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivitySetStopBinding binding;
    private SearchView svMap;
    private Button btnSubmitStop;
    private final float DEFAULT_ZOOM = 45f;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySetStopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        svMap = findViewById(R.id.svMap);
        svMap.setIconifiedByDefault(false);
        latLng = null;

        svMap.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Geocoder geocoder = new Geocoder(SetStop.this);
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocationName(s, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addresses != null && !addresses.isEmpty()) {
                    Address selectedAddress = addresses.get(0);
                    latLng = new LatLng(selectedAddress.getLatitude(), selectedAddress.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .title(selectedAddress.getAddressLine(0)).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_icon));
                    mMap.addMarker(markerOptions);
                    moveCamera(latLng,DEFAULT_ZOOM);
                    Toast.makeText(SetStop.this, "Drag the marker to desired location", Toast.LENGTH_SHORT).show();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                new LocationSuggestionTask(SetStop.this, svMap).execute(s);
                return true;
            }
        });

        binding.svMap.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                CursorAdapter cursorAdapter = svMap.getSuggestionsAdapter();
                Cursor cursor = cursorAdapter.getCursor();
                cursor.moveToPosition(position);
                String locationName = cursor.getString(cursor.getColumnIndexOrThrow("location_name"));

                Geocoder geocoder = new Geocoder(SetStop.this);
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocationName(locationName, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null && !addresses.isEmpty()) {
                    Address selectedAddress = addresses.get(0);
                    latLng = new LatLng(selectedAddress.getLatitude(), selectedAddress.getLongitude());
                    mMap.clear();
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .title(selectedAddress.getAddressLine(0)).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_icon));
                    mMap.addMarker(markerOptions);
                    moveCamera(latLng,DEFAULT_ZOOM);
                    //Toast.makeText(SetStop.this, "Drag the marker to desired location", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        btnSubmitStop = findViewById(R.id.btnSubmitLocation);
        btnSubmitStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("updatedLatLng", latLng);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }

    public void moveCamera(LatLng latLng , float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(22.7196,75.8577)));
        moveCamera(new LatLng(22.7196,75.8577),15f);

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                // Code to be executed when marker drag starts
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                // Code to be executed while marker is being dragged
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // Code to be executed when marker drag ends
                mMap.clear();
                latLng = marker.getPosition();
                moveCamera(latLng, DEFAULT_ZOOM);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_icon));
                mMap.addMarker(markerOptions);
            }
        });
    }
}