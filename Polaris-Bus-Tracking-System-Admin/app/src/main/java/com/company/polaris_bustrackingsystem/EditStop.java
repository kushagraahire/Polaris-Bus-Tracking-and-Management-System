package com.company.polaris_bustrackingsystem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import com.company.polaris_bustrackingsystem.LocationSuggestion.LocationSuggestionTask;
import com.company.polaris_bustrackingsystem.Models.StopModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.company.polaris_bustrackingsystem.databinding.ActivityEditStopBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class EditStop extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityEditStopBinding binding;
    private SearchView svEditMap;
    private Button btnEditStop;
    private final float DEFAULT_ZOOM = 45f;
    private LatLng latLng;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     binding = ActivityEditStopBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.editMap);
        mapFragment.getMapAsync(this);

        String stopKey = getIntent().getStringExtra("stopKey");
        String routeKey = getIntent().getStringExtra("routeKey");
        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("Route").child(routeKey).child("Stops").child(stopKey);
        svEditMap = findViewById(R.id.svEditMap);
        svEditMap.setIconifiedByDefault(false);
        btnEditStop = findViewById(R.id.btnEditLocation);
        latLng = null;

        Log.e("Route Key : ",routeKey);
        Log.e("Stop Key : ",stopKey);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    StopModel model = snapshot.getValue(StopModel.class);
                    latLng = new LatLng(model.getLatitude(),model.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_icon));
                    mMap.addMarker(markerOptions);
                    moveCamera(latLng,DEFAULT_ZOOM);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        svEditMap.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Geocoder geocoder = new Geocoder(EditStop.this);
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocationName(s, 1);
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
                    Toast.makeText(EditStop.this, "Drag the marker to desired location", Toast.LENGTH_SHORT).show();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                new LocationSuggestionTask(EditStop.this, svEditMap).execute(s);
                return true;
            }
        });

        binding.svEditMap.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                CursorAdapter cursorAdapter = svEditMap.getSuggestionsAdapter();
                Cursor cursor = cursorAdapter.getCursor();
                cursor.moveToPosition(position);
                String locationName = cursor.getString(cursor.getColumnIndexOrThrow("location_name"));

                Geocoder geocoder = new Geocoder(EditStop.this);
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
              //      Toast.makeText(EditStop.this, "Drag the marker to desired location", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        btnEditStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(latLng == null){
                    finish();
                }else{
                    double latitude = latLng.latitude;
                    double longitude = latLng.longitude;
                    ref.child("latitude").setValue(latitude);
                    ref.child("longitude").setValue(longitude);
                    Toast.makeText(EditStop.this, "Location updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
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

    public void moveCamera(LatLng latLng , float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }
}