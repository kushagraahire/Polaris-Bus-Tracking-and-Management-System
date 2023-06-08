package com.company.polarisdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverHomepage extends AppCompatActivity {

    TextView tvName,tvEmail,tvPhone,tvRoute,tvBusNo;
    ImageView iv;
    Button btnLogout,btnTrack;

    private FirebaseUser user;
    private DatabaseReference reference,ref;

    private String userID;

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to Log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(DriverHomepage.this,DriverLogin.class));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_homepage);

        getWindow().setStatusBarColor(ContextCompat.getColor(DriverHomepage.this,R.color.orange));
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvBusNo = findViewById(R.id.tvBusNo);
        tvRoute = findViewById(R.id.tvBusRoute);

        iv = findViewById(R.id.iv);
        btnLogout = findViewById(R.id.btnLogout);
        btnTrack = findViewById(R.id.btnTrack);

        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(DriverHomepage.this,DriverLogin.class));
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        });

        btnTrack.setOnClickListener(view -> {

            startActivity(new Intent(DriverHomepage.this,BusTracking.class));
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference("Driver");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                driverModel driverModel = snapshot.getValue(driverModel.class);

                if(driverModel != null){
                    String name = driverModel.name;
                    String email = driverModel.email;
                    String phone = driverModel.phone;
                    String imgurl = driverModel.imgurl;
                    String busId = driverModel.busId;

                    tvEmail.setText(email);
                    tvName.setText(name);
                    tvPhone.setText(phone);

                    Glide.with(DriverHomepage.this).load(imgurl).placeholder(R.drawable.ic_baseline_person_outline_24).into(iv);

                    // getting Bus No. and Bus Route
                    ref.child("Bus").child(busId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            BusModel bus = snapshot.getValue(BusModel.class);
                            String busNo = bus.busno;
                            String busRoute = bus.busroute;

                            tvBusNo.setText("Bus No. "+busNo);
                            tvRoute.setText(busRoute);
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