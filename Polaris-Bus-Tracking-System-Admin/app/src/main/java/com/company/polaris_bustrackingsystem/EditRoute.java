package com.company.polaris_bustrackingsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.company.polaris_bustrackingsystem.Adapters.StopAdapter;
import com.company.polaris_bustrackingsystem.Models.RouteModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditRoute extends AppCompatActivity {
    private EditText routeNoEdit, routeNameEdit;
    private Button btnAddStopEdit, btnSubmitRouteEdit;
    private RecyclerView rvStopEdit;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private StopAdapter stopAdapter;
    private String stopName;
    private LatLng latLng;
    private String routeKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_route);

        routeNameEdit = findViewById(R.id.routeNameEdit);
        routeNoEdit = findViewById(R.id.routeNoEdit);
        btnAddStopEdit = findViewById(R.id.btnAddStopEdit);
        btnSubmitRouteEdit = findViewById(R.id.btnSubmitRouteEdit);
        rvStopEdit = findViewById(R.id.rvStopEdit);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("Route");

        routeKey = getIntent().getStringExtra("routeKey");
        stopName = "";
        latLng = null;

        setValues(routeKey);

        setStops(routeKey);

        btnAddStopEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertInput();
            }
        });

        btnSubmitRouteEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String routeNo = routeNoEdit.getText().toString();
                String routeName = routeNameEdit.getText().toString();

                if(routeNo.isEmpty()){
                    routeNoEdit.setError("This field cannot be empty");
                    routeNoEdit.requestFocus();
                }else if(routeName.isEmpty()){
                    routeNameEdit.setError("This field cannot be empty");
                    routeNameEdit.requestFocus();
                }else{
                    ref.child(routeKey).child("routeName").setValue(routeName);
                    ref.child(routeKey).child("routeNo").setValue(routeNo);
                    finish();
                }
            }
        });
    }

    private void setStops(String routeKey) {
        DatabaseReference stopRef = ref.child(routeKey).child("Stops");
        stopAdapter = new StopAdapter(stopRef);
        rvStopEdit.setLayoutManager(new LinearLayoutManager(this));
        rvStopEdit.setAdapter(stopAdapter);
    }

    private void setValues(String routeKey) {
        ref.child(routeKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RouteModel model = snapshot.getValue(RouteModel.class);
                if(model != null){
                    routeNoEdit.setText(model.getRouteNo());
                    routeNameEdit.setText(model.getRouteName());
                }else{
                    Toast.makeText(EditRoute.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                Intent intent = new Intent(EditRoute.this, SetStop.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            latLng = (LatLng) data.getParcelableExtra("updatedLatLng");
            String stopKey = ref.child(routeKey).child("Stops").push().getKey();
            if(latLng != null) {
                ref.child(routeKey).child("Stops").child(stopKey).setValue(latLng);
                ref.child(routeKey).child("Stops").child(stopKey).child("stopName").setValue(stopName);
                stopAdapter.notifyDataSetChanged();
            }else{
                Toast.makeText(this, "Location not selected", Toast.LENGTH_SHORT).show();
            }
        }
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
}