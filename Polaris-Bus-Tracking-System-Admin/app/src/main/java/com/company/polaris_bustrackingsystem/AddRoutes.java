package com.company.polaris_bustrackingsystem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.company.polaris_bustrackingsystem.Adapters.StopAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class AddRoutes extends AppCompatActivity {

    private EditText etRouteNo, etRouteName;
    private Button btnAddStop, btnSubmitRoute;
    FirebaseDatabase database;
    DatabaseReference routeRef, stopRef;
    StopAdapter stopAdapter;
    RecyclerView rvStop;
    String routeKey, stopName;
    LatLng latLng;
    boolean checkSubmit = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!checkSubmit){
            routeRef.child(routeKey).setValue(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_routes);

        etRouteNo = findViewById(R.id.routeNoAdd);
        etRouteName = findViewById(R.id.routeNameAdd);
        btnAddStop = findViewById(R.id.btnAddStop);
        btnSubmitRoute = findViewById(R.id.btnSubmitRoute);
        rvStop  =findViewById(R.id.rvStop);
        rvStop.setLayoutManager(new LinearLayoutManager(this));
        database = FirebaseDatabase.getInstance();
        routeRef = database.getReference().child("Route");
        routeKey = routeRef.push().getKey();
        stopRef = routeRef.child(Objects.requireNonNull(routeKey)).child("Stops");
        stopAdapter = new StopAdapter(stopRef);
        rvStop.setAdapter(stopAdapter);

        btnAddStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertInput();
            }
        });

        btnSubmitRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String routeNo = etRouteNo.getText().toString();
                String routeName = etRouteName.getText().toString();

                if(routeNo.isEmpty()){
                    etRouteNo.setError("This field cannot be empty");
                    etRouteNo.requestFocus();
                }else if(routeName.isEmpty()){
                    etRouteName.setError("This field cannot be empty");
                    etRouteName.requestFocus();
                }else{
                    routeRef.child(routeKey).child("routeName").setValue(routeName);
                    routeRef.child(routeKey).child("routeNo").setValue(routeNo);
                    checkSubmit = true;
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        stopAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            latLng = (LatLng) data.getParcelableExtra("updatedLatLng");
            String stopKey = routeRef.child(routeKey).child("Stops").push().getKey();
            if(latLng != null) {
                routeRef.child(routeKey).child("Stops").child(stopKey).setValue(latLng);
                routeRef.child(routeKey).child("Stops").child(stopKey).child("stopName").setValue(stopName);
                stopAdapter.notifyDataSetChanged();
            }else{
                Toast.makeText(this, "Location not selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void alertInput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.input_dialog_layout,null);
        builder.setView(dialogView);

        EditText etStopName = dialogView.findViewById(R.id.etStopNameAdd);

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                stopName = etStopName.getText().toString();
                if(stopName.isEmpty()){
                    etStopName.setError("This field cannot be empty");
                    etStopName.requestFocus();
                    return;
                }
                    Intent intent = new Intent(AddRoutes.this, SetStop.class);
                    startActivityForResult(intent, 1);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}