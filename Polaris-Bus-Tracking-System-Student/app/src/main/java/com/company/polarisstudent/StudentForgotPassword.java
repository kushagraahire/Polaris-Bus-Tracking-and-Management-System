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
import com.google.firebase.auth.FirebaseAuth;

public class StudentForgotPassword extends AppCompatActivity {

    TextView tvLoginPage;
    EditText etStudentFPEmail;
    Button btnResetPassword;
    FirebaseAuth auth;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_forgot_password);
        getWindow().setStatusBarColor(ContextCompat.getColor(StudentForgotPassword.this,R.color.black));
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        tvLoginPage = findViewById(R.id.textViewLoginPage);

        tvLoginPage.setOnClickListener(view -> {
            startActivity(new Intent(StudentForgotPassword.this,StudentLogin.class));
        });

        etStudentFPEmail = findViewById(R.id.etStudentFPEmail);
        btnResetPassword = findViewById(R.id.buttonResetPassword);

        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(view -> {
            resetPassword();
        });
    }

    private void resetPassword(){
        String email = etStudentFPEmail.getText().toString().trim();

        if(email.isEmpty()){
            etStudentFPEmail.setError("Email is required!");
            etStudentFPEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etStudentFPEmail.setError("Please enter a valid email");
            etStudentFPEmail.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isComplete()){
                            Toast.makeText(StudentForgotPassword.this, "Check your email to reset your password", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(StudentForgotPassword.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}