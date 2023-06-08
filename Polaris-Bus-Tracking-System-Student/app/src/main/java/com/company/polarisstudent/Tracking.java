package com.company.polarisstudent;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.company.polarisstudent.databinding.ActivityTrackingBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Tracking extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityTrackingBinding binding;

    private final float DEFAULT_ZOOM = 20f;
    private boolean cameraMovedByUser = false;

    private DatabaseReference driverRef;
    private Marker busMarker;
    private DatabaseReference busReference, ref;
    private FirebaseDatabase database;
    private String busID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        busID = getIntent().getStringExtra("busID");

        database = FirebaseDatabase.getInstance();
        busReference = database.getReference().child("Bus").child(busID);
        driverRef = database.getReference().child("Driver");
        ref = database.getReference();

        viewBusLocation();
        displayStops();

    }

    private void displayStops() {
        busReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BusModel bus = snapshot.getValue(BusModel.class);
                if(bus != null){
                    String routeKey = bus.routeKey;
                    ref.child("Route").child(routeKey).child("Stops").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            // Setting up the HTTP client and request builder for GraphHopper API
                            OkHttpClient client = new OkHttpClient.Builder().build();
                            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://graphhopper.com/api/1/route").newBuilder();
                            urlBuilder.addQueryParameter("key", "e0ed0224-7f2e-4d7b-8533-bc25d3616f44");
                            urlBuilder.addQueryParameter("vehicle", "car");
                            urlBuilder.addQueryParameter("points_encoded", "false");

                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                // get the latitude and longitude values for the current stop
                                StopModel stop = dataSnapshot.getValue(StopModel.class);
                                if(stop != null){
                                    double latitude = stop.getLatitude();
                                    double longitude = stop.getLongitude();
                                    String stopName = stop.getStopName();

                                    urlBuilder.addQueryParameter("point", latitude + "," + longitude);

                                    LatLng stopLatLng = new LatLng(latitude, longitude);
                                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.stop_icon_map);
                                    MarkerOptions markerOptions = new MarkerOptions().position(stopLatLng).title(stopName).icon(icon);
                                    mMap.addMarker(markerOptions);
                                }
                            }

                            // HTTP request
                            Request request = new Request.Builder()
                                    .url(urlBuilder.build().toString())
                                    .get()
                                    .build();

                            // Executing the request asynchronously
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.e(TAG, "GraphHopper API request failed", e);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if (!response.isSuccessful()) {
                                        Log.e(TAG, "GraphHopper API request failed with code " + response.code());
                                        return;
                                    }

                                    // Parse the response JSON and extract the route geometry
                                    JSONObject json = null;
                                    try {
                                        json = new JSONObject(response.body().string());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    // Getting coordinates array from the json object
                                    JSONArray coordinatesArray = null;
                                    try {
                                        coordinatesArray = json.getJSONArray("paths").getJSONObject(0).getJSONObject("points").getJSONArray("coordinates");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    try {

                                        // List of LatLng present in coordinate array
                                        List<LatLng> coordinates = new ArrayList<>();
                                        for (int i = 0; i < coordinatesArray.length(); i++) {
                                            // fetching latlng from coordinatesArray
                                            JSONArray point = coordinatesArray.getJSONArray(i);
                                            LatLng latLng = new LatLng(point.getDouble(1), point.getDouble(0));
                                            coordinates.add(latLng);
                                        }

                                        // Draw the polyline on the map
                                        runOnUiThread(() -> {
                                            mMap.addPolyline(new PolylineOptions().addAll(coordinates));
                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void viewBusLocation() {
        busReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BusModel busModel = snapshot.getValue(BusModel.class);

                String driverid = busModel.driverid;

                driverRef.child(driverid).child("location").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            try {
                                LocationModel location = snapshot.getValue(LocationModel.class);
                                if (location != null) {
                                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                                    busMarker.setPosition(latLng);
                                    if (!cameraMovedByUser) {
                                        // Only move the camera if the user has not moved it manually
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                                    }
                                }
                            }catch (Exception e){
                                Toast.makeText(Tracking.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

        // Add a marker in Sydney and move the camera
        LatLng indore = new LatLng(22.7072229, 75.8559028);
       busMarker = mMap.addMarker(new MarkerOptions().position(indore)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.busmarker_map)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(indore));
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        moveCamera(new LatLng(22.7072229, 75.8559028),DEFAULT_ZOOM);

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                cameraMovedByUser = true;
            }
        });
    }

    public void moveCamera(LatLng latLng , float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

}