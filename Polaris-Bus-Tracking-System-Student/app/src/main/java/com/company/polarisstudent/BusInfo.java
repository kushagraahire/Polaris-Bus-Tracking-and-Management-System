package com.company.polarisstudent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BusInfo extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private TextView tvDriverName, tvDriverPhone, tvBusNoInfo, tvBusRouteInfo, tvBusStopsInfo;
    private ImageView ivDriver, ivMap;

    private String busID;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_info);

        busID = getIntent().getStringExtra("busID");

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        tvDriverName = findViewById(R.id.tvDriverName);
        tvDriverPhone = findViewById(R.id.tvDriverPhone);
        tvBusNoInfo = findViewById(R.id.tvBusNoInfo);
        tvBusRouteInfo = findViewById(R.id.tvBusRouteInfo);
        tvBusStopsInfo = findViewById(R.id.tvBusStopsInfo);
        ivDriver = findViewById(R.id.ivDriver);
        ivMap = findViewById(R.id.ivMap);

        DisplayBusInfo();

        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusInfo.this,Tracking.class);
                intent.putExtra("busID",busID);
                startActivity(intent);
            }
        });
    }

    private void DisplayBusInfo() {
        reference.child("Bus").child(busID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BusModel bus = snapshot.getValue(BusModel.class);

                if(bus != null){
                    tvBusNoInfo.setText(bus.getBusno());
                    tvBusRouteInfo.setText(bus.getBusroute());
                    tvBusStopsInfo.setText(bus.getStops());

                    String driverId = bus.getDriverid();

                    reference.child("Driver").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DriverModel driver = snapshot.getValue(DriverModel.class);
                            if(driver != null){
                                tvDriverName.setText(driver.getName());
                                tvDriverPhone.setText(driver.getPhone());
                            }

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
}