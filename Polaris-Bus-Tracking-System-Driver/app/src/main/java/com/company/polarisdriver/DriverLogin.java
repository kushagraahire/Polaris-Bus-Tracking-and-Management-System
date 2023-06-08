package com.company.polarisdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DriverLogin extends AppCompatActivity {

    TextView tvForgotPassword;
    EditText etDriverEmail,etDriverPassword;
    Button btnLogin;

    private FirebaseAuth mAuth;

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(DriverLogin.this);
        alert.setTitle("Exit App");
        alert.setMessage("Do you want to exit app?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(DriverLogin.this,R.color.black));
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        tvForgotPassword = findViewById(R.id.textViewForgotPasswordDriver);

        etDriverEmail = findViewById(R.id.editTextDriverEmail);
        etDriverPassword = findViewById(R.id.editTextDriverPassword);
        btnLogin = findViewById(R.id.buttonDriverLogin);

        tvForgotPassword.setOnClickListener(view -> {
            startActivity(new Intent(DriverLogin.this,DriverForgotPassword.class));
        });

        btnLogin.setOnClickListener(view -> {
            userLogin();
        });

        mAuth = FirebaseAuth.getInstance();

    }



    private void userLogin() {

        String email = etDriverEmail.getText().toString().trim();
        String password = etDriverPassword.getText().toString().trim();

        if(email.isEmpty()){
            etDriverEmail.setError("Email is required!");
            etDriverEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etDriverEmail.setError("Please enter a valid email");
            etDriverEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            etDriverPassword.setError("Password is required");
            etDriverPassword.requestFocus();
            return;
        }



        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                                startActivity(new Intent(DriverLogin.this,DriverHomepage.class));
                            Toast.makeText(DriverLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(DriverLogin.this, "Login Failed", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    }