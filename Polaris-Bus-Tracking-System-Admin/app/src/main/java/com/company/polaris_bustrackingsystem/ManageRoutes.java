package com.company.polaris_bustrackingsystem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.company.polaris_bustrackingsystem.Adapters.RouteAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ManageRoutes extends AppCompatActivity {

    RecyclerView rvManageRoutes;
    FloatingActionButton fabManageRoutes;
    FirebaseDatabase database;
    DatabaseReference ref;
    RouteAdapter routeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_routes);

        fabManageRoutes = findViewById(R.id.fabManageRoutes);
        rvManageRoutes = findViewById(R.id.rvManageRoutes);
        rvManageRoutes.setLayoutManager(new LinearLayoutManager(this));
        database = FirebaseDatabase.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Route");

        routeAdapter = new RouteAdapter(ref);
        rvManageRoutes.setAdapter(routeAdapter);

        fabManageRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManageRoutes.this, AddRoutes.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        routeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        routeAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        routeAdapter.stopListening();
    }
}