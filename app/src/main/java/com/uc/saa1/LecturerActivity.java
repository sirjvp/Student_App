package com.uc.saa1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uc.saa1.model.Lecturer;

import java.util.HashMap;
import java.util.Map;

public class LecturerActivity extends AppCompatActivity implements TextWatcher {
    TextInputLayout input_fname, input_expertise;
    String fname, expertise, gender="Male", action;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button course;
    Toolbar toolbar;
    private DatabaseReference mDatabase;
    Dialog dialog;
    Lecturer lecturer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer);

        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = Glovar.loadingDialog(LecturerActivity.this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        course = findViewById(R.id.button3);
        input_fname = findViewById(R.id.input_subject);
        input_expertise = findViewById(R.id.expertise);

        input_fname.getEditText().addTextChangedListener(this);
        input_expertise.getEditText().addTextChangedListener(this);

        radioGroup = findViewById(R.id.rg_student);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = findViewById(checkedId);
                gender = radioButton.getText().toString();

            }
        });

        Intent intent = getIntent();
        action = intent.getStringExtra("action");
        if(action.equals("add")){
            getSupportActionBar().setTitle(R.string.addlecturer);
            course.setText(R.string.addlecturer);
            course.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fname = input_fname.getEditText().getText().toString().trim();
                    expertise = input_expertise.getEditText().getText().toString().trim();
                    addLecturer(fname, gender, expertise);
                }
            });
        }
        else{ //saat activity dari lecturer detail & mau mengupdate data
            getSupportActionBar().setTitle(R.string.editlecturer);
            lecturer = intent.getParcelableExtra("edit_data_lect");
            input_fname.getEditText().setText(lecturer.getName());
            input_expertise.getEditText().setText(lecturer.getExpertise());
            String gender_temp = lecturer.getGender();
            if(gender_temp.equalsIgnoreCase("male")){
                radioGroup.check(R.id.rb_student_m);
            }else{
                radioGroup.check(R.id.rb_student_f);
            }
            course.setText(R.string.editlecturer);
            course.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                    fname = input_fname.getEditText().getText().toString().trim();
                    expertise = input_expertise.getEditText().getText().toString().trim();
                    Map<String,Object> params = new HashMap<>();
                    params.put("name", fname);
                    params.put("expertise", expertise);
                    params.put("gender", gender);
                    mDatabase.child("lecturer").child(lecturer.getId()).updateChildren(params).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.cancel();
                            Intent intent;
                            intent = new Intent(LecturerActivity.this, LecturerData.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LecturerActivity.this);
                            startActivity(intent, options.toBundle());
                            finish();
                        }
                    });
                }
            });
        }
    }

    public void addLecturer(String mnama, String mgender, String mexpertise){
        dialog.show();
        String mid = mDatabase.child("lecturer").push().getKey();
        Lecturer lecturer = new Lecturer(mid, mnama, mgender, mexpertise);
        mDatabase.child("lecturer").child(mid).setValue(lecturer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.cancel();
                Toast.makeText(LecturerActivity.this, "Add Lecturer Succesfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.cancel();
                Toast.makeText(LecturerActivity.this, "Add Lecturer Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        fname = input_fname.getEditText().getText().toString().trim();
        expertise = input_expertise.getEditText().getText().toString().trim();

        if (!fname.isEmpty() && !expertise.isEmpty()) {
            course.setEnabled(true);
        }
        else{
            course.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lecturer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            Intent intent;
            intent = new Intent(LecturerActivity.this, StarterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
            return true;
        }else if(id == R.id.lecturer_list){
            Intent intent;
            intent = new Intent(LecturerActivity.this, LecturerData.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(LecturerActivity.this, StarterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}