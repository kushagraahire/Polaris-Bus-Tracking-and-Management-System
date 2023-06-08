package com.company.polarisstudent;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ProfileFragment extends Fragment {

    private ImageView ivProfile;
    private TextView tvEnroll, tvEmail, tvBusStop, tvName;
    private Button btnLogout;

    private FirebaseDatabase database;
    private FirebaseUser user;
    private DatabaseReference ref;
    private String studentID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        ivProfile = view.findViewById(R.id.ivProfile);
        tvName = view.findViewById(R.id.tvName);
        tvEnroll = view.findViewById(R.id.tvEnroll);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvBusStop = view.findViewById(R.id.tvBusStop);
        btnLogout = view.findViewById(R.id.btnLogout);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("Students");
        user = FirebaseAuth.getInstance().getCurrentUser();

        studentID = user.getUid();

        ref.child(studentID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StudentModel student = snapshot.getValue(StudentModel.class);

                if(student != null){
                    String name = student.name;
                    String enroll = student.enroll;
                    String email = student.email;
                    String imgurl = student.imgurl;
                    String busstop = student.busstop;

                    tvName.setText(name);
                    tvBusStop.setText(busstop);
                    tvEmail.setText(email);
                    tvEnroll.setText(enroll);

                    Glide.with(view.getContext()).load(imgurl).placeholder(R.drawable.profile_pic_placeholder)
                            .error(R.drawable.profile_pic_placeholder)
                            .into(ivProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(),StudentLogin.class));
                getActivity().finish();
            }
        });

        return view;
    }
}