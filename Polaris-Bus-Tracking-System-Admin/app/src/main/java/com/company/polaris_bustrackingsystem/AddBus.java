package com.company.polaris_bustrackingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.company.polaris_bustrackingsystem.Models.BusModel;
import com.company.polaris_bustrackingsystem.Models.DriverModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddBus extends AppCompatActivity {


    EditText busno;
    TextView tvAddBus;
    Button busAdd,busback;
    Spinner spinnerSelectDriverAdd, spinnerSelectRouteAdd;
    FirebaseDatabase database;
    DatabaseReference ref;
    String driverId;
    String busId, busKey;
    String routeKey, routeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_bus);

        tvAddBus = findViewById(R.id.tvAddBus);
        busno = (EditText) findViewById(R.id.addBusNo);
        busAdd = (Button) findViewById(R.id.btnBusAdd);
        busback = (Button) findViewById(R.id.btnBusBack);
        spinnerSelectDriverAdd = findViewById(R.id.spinnerSelectDriverAdd);
        spinnerSelectRouteAdd = findViewById(R.id.spinnerSelectRouteAdd);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        driverId = "";
        busId = "";
        busKey = "";
        routeKey = "";
        routeName = "";

        setSpinnerDriver();
        setSpinnerRoute();

        Intent intent = getIntent();
        busKey = intent.getStringExtra("busKey");
        if(busKey != null){
            busAdd.setText(getString(R.string.updateBus));
            tvAddBus.setText("Update Bus");
        }

        busAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
                addBusToDriver();
                startActivity(new Intent(AddBus.this,ManageBus.class));
                finish();
            }
        });

        busback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setSpinnerRoute() {
        ref.child("Route").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> routeNames = new ArrayList<>();
                final List<String> routeIds = new ArrayList<>();
                for(DataSnapshot routeSnapshot : snapshot.getChildren()){
                    String routeName = routeSnapshot.child("routeName").getValue(String.class);
                    String routeId = routeSnapshot.getKey();
                    routeNames.add(routeName);
                    routeIds.add(routeId);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,routeNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSelectRouteAdd.setAdapter(adapter);

                if(busKey != null){
                    ref.child("Bus").child(busKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            BusModel busModel =  snapshot.getValue(BusModel.class);
                            if(busModel != null){
                                String busNo = busModel.getBusno();
                                Log.e("Bus No ",busNo);
                                String route = busModel.getBusroute();
                                busno.setText(busNo);
                                int spinnerPosition = 0;
                                for (int i = 0; i < routeNames.size(); i++) {
                                    if (routeNames.get(i).equals(route)) {
                                        spinnerPosition = i;
                                        break;
                                    }
                                }
                                spinnerSelectRouteAdd.setSelection(spinnerPosition);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                spinnerSelectRouteAdd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        routeKey = routeIds.get(position);
                        routeName = routeNames.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setSpinnerDriver() {
        ref.child("Driver").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> driverNames = new ArrayList<>();
                final List<String> driverIds = new ArrayList<>(); // create a list to store the driver IDs
                for (DataSnapshot driverSnapshot : snapshot.getChildren()) {
                    String driverName = driverSnapshot.child("name").getValue(String.class);
                    String driverId = driverSnapshot.getKey(); // get the ID of the driver
                    driverNames.add(driverName);
                    driverIds.add(driverId); // add the driver ID to the list
                }
                Log.e("Driver : ",driverNames.toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, driverNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSelectDriverAdd.setAdapter(adapter);

                if(busKey != null){
                    ref.child("Bus").child(busKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            BusModel busModel =  snapshot.getValue(BusModel.class);
                            if(busModel != null){
                                String busNo = busModel.getBusno();
                                Log.e("Bus No ",busNo);
                                String driverKey = busModel.getDriverid();
                                busno.setText(busNo);
                                int spinnerPosition = 0;
                                for (int i = 0; i < driverIds.size(); i++) {
                                    if (driverIds.get(i).equals(driverKey)) {
                                        spinnerPosition = i;
                                        break;
                                    }
                                }
                                spinnerSelectDriverAdd.setSelection(spinnerPosition);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                spinnerSelectDriverAdd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinnerSelectDriverAdd.setSelection(position);
                        driverId = driverIds.get(position); // get the ID of the selected driver
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addBusToDriver() {
        ref.child("Driver").child(driverId).child("busId").setValue(busId);
    }

    public void insertData()
    {
        busId = ref.child("Bus").push().getKey();
        if(busKey != null) {
            busId = busKey;
        }

        String busNumber = busno.getText().toString();
        Log.e("Route Key",routeKey);
        BusModel bus = new BusModel(busNumber,routeName,driverId,"",routeKey);
        ref.child("Bus").child(busId).setValue(bus)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if(busKey != null){
                            Toast.makeText(AddBus.this, "Data Updated", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(AddBus.this,"Data added",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        insertStops(busId);
    }

    private void insertStops(String busId) {
        StringBuilder sb = new StringBuilder();
        ref.child("Route").child(routeKey).child("Stops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot stopsSnapshot : snapshot.getChildren()){
                    String stopName = stopsSnapshot.child("stopName").getValue(String.class);
                    sb.append(stopName).append(", ");
                }
                if(sb.length() != 0){
                    sb.delete(sb.length()-2,sb.length()-1);
                    String stopName = sb.toString();
                    ref.child("Bus").child(busId).child("stops").setValue(stopName);
                }else{
                    ref.child("Bus").child(busId).child("stops").setValue("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}