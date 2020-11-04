package com.uc.saa1.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uc.saa1.Glovar;
import com.uc.saa1.R;
import com.uc.saa1.StarterActivity;
import com.uc.saa1.model.Student;

import java.util.ArrayList;

public class AccountFragment extends Fragment {
    TextView lbl_name, lbl_nim, lbl_email, lbl_gender_age, lbl_address;
    Button logout;
    Dialog dialog;
    DatabaseReference dbStudent;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lbl_name = view.findViewById(R.id.lbl_name_stu_adp);
        lbl_nim = view.findViewById(R.id.lbl_nim_stu_adp);
        lbl_email = view.findViewById(R.id.lbl_email_stu_adp);
        lbl_gender_age = view.findViewById(R.id.lbl_gender_stu_adp);
        lbl_address = view.findViewById(R.id.lbl_address_stu_adp);
        dialog = Glovar.loadingDialog(getActivity());

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        dbStudent = FirebaseDatabase.getInstance().getReference("student").child(mUser.getUid());
        dbStudent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Student student = snapshot.getValue(Student.class);
                String gender_age = student.getGender() + "/" + student.getAge();
                lbl_name.setText(student.getName());
                lbl_nim.setText(student.getNim());
                lbl_email.setText(student.getEmail());
                lbl_gender_age.setText(gender_age);
                lbl_address.setText(student.getAddress());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logout = view.findViewById(R.id.btn_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), StarterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}