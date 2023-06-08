package com.company.polarisstudent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

public class StudentLogin extends AppCompatActivity {

    TextView tvForgotPassword;
    EditText etStudentEmail,etStudentPassword;
    Button btnLogin;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private FirebaseUser user;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_login);
        getWindow().setStatusBarColor(ContextCompat.getColor(StudentLogin.this,R.color.black));
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            startActivity(new Intent(StudentLogin.this,StudentHomepage.class));
        }

        tvForgotPassword = findViewById(R.id.textViewForgotPassword);

        etStudentEmail = findViewById(R.id.editTextStudentEmail);
        etStudentPassword = findViewById(R.id.editTextStudentPassword);
        btnLogin = findViewById(R.id.buttonStudentLogin);
        progressBar = findViewById(R.id.progressBar);

        tvForgotPassword.setOnClickListener(view -> {
            startActivity(new Intent(StudentLogin.this,StudentForgotPassword.class));
        });

        btnLogin.setOnClickListener(view -> {
            userLogin();
        });
    }

    private void userLogin() {

        String email = etStudentEmail.getText().toString().trim();
        String password = etStudentPassword.getText().toString().trim();

        if(email.isEmpty()){
            etStudentEmail.setError("Email is required!");
            etStudentEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etStudentEmail.setError("Please enter a valid email");
            etStudentEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            etStudentPassword.setError("Password is required");
            etStudentPassword.requestFocus();
            return;
        }

        if(password.length() <  6){
            etStudentPassword.setError("Minimum password length is 6 characters");
            etStudentPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                startActivity(new Intent(StudentLogin.this,StudentHomepage.class));
                        }
                        else{

                            Toast.makeText(StudentLogin.this, "Login Failed", Toast.LENGTH_SHORT)
                                    .show();

                        }
                    }
                });
    }

}