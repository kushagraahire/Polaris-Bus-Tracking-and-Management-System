package com.company.polarisdriver;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.company.polarisdriver.databinding.ActivityBusTrackingBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class BusTracking extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityBusTrackingBinding binding;

    private FirebaseUser user;
    private String userID;

    private FirebaseDatabase database;
    private DatabaseReference reference,ref;
    private LocationManager manager;

    private  final int MIN_TIME = 1500;
    private final int MIN_DISTANCE = 1;
    private final float DEFAULT_ZOOM = 20f;
    private boolean cameraMovedByUser = false;

    private Marker myMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Driver").child(userID).child("location");
        ref = database.getReference();

        binding = ActivityBusTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocationUpdates();

        readChanges();

        displayStops();
    }

    private void displayStops() {
        ref.child("Driver").child(userID).child("busId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String busId = snapshot.getValue(String.class);
                ref.child("Bus").child(busId).child("routeKey").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String routeKey = snapshot.getValue(String.class);
                        ref.child("Route").child(routeKey).child("Stops").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                // Setting up the HTTP client and request builder for GraphHopper API
                                OkHttpClient client = new OkHttpClient.Builder().build();
                                HttpUrl.Builder urlBuilder = HttpUrl.parse("https://graphhopper.com/api/1/route").newBuilder();
                                urlBuilder.addQueryParameter("key", "e0ed0224-7f2e-4d7b-8533-bc25d3616f44");
                                urlBuilder.addQueryParameter("vehicle", "car");
                                urlBuilder.addQueryParameter("points_encoded", "false");

                                for (DataSnapshot stopSnapshot : snapshot.getChildren()) {
                                    // get the latitude and longitude values for the current stop
                                    StopModel stop = stopSnapshot.getValue(StopModel.class);
                                    if(stop != null){
                                        double latitude = stop.getLatitude();
                                        double longitude = stop.getLongitude();
                                        String stopName = stop.getStopName();

                                        urlBuilder.addQueryParameter("point", latitude + "," + longitude);

                                        // marker for the current stop and adding it to the map
                                        LatLng stopLatLng = new LatLng(latitude, longitude);
                                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.stop_icon);
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

    private void readChanges() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    try {
                        LocationModel location = snapshot.getValue(LocationModel.class);
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                           myMarker.setPosition(latLng);
                            if (!cameraMovedByUser) {
                                // Only move the camera if the user has not moved it manually
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                            }
                        }
                    }catch (Exception e){
                        //Toast.makeText(BusTracking.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(BusTracking.this, DriverLogin.class));
        finish();
    }

    private void getLocationUpdates() {

        if(manager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                } else {
                    Toast.makeText(this, "No Provider Enabled", Toast.LENGTH_SHORT).show();
                }
        }else{
               ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocationUpdates();
            }else{
                Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
            }
        }
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

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.busmarker);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(22.7072229, 75.8559028))
                .title("Marker").icon(icon);
        myMarker = googleMap.addMarker(markerOptions);
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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location != null){
            saveLocation(location);
        }else{
            Toast.makeText(this, "Error in sending Location", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLocation(Location location) {

        reference.setValue(location);
    }

    public void moveCamera(LatLng latLng , float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }
}