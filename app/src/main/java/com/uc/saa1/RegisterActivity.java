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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uc.saa1.model.Student;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements TextWatcher {
    TextInputLayout input_email, input_pass, input_nim, input_name, input_age, input_address;
    String email, pass, nim, name, age, address, gender="Male", uid, action;
    Button btn_register;
    Toolbar toolbar;
    Dialog dialog;
    RadioGroup rg_gender;
    RadioButton radioButton;
    Student student;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = Glovar.loadingDialog(RegisterActivity.this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        input_email = findViewById(R.id.email);
        input_pass= findViewById(R.id.pass);
        input_name = findViewById(R.id.input_subject);
        input_nim = findViewById(R.id.nim);
        input_age = findViewById(R.id.age);
        input_address = findViewById(R.id.address);

        input_email.getEditText().addTextChangedListener(this);
        input_pass.getEditText().addTextChangedListener(this);
        input_name.getEditText().addTextChangedListener(this);
        input_nim.getEditText().addTextChangedListener(this);
        input_age.getEditText().addTextChangedListener(this);
        input_address.getEditText().addTextChangedListener(this);

        btn_register = findViewById(R.id.button);
        rg_gender = findViewById(R.id.rg_student);
        rg_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButton = findViewById(i);
                gender = radioButton.getText().toString();
            }
        });

        Intent intent = getIntent();
        action = intent.getStringExtra("action");
        if(action.equals("add")){
            getSupportActionBar().setTitle(R.string.regstudent);
            btn_register.setText(R.string.regstudent);
            btn_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addStudent();
                }
            });
        }
        else{ //saat activity dari lecturer detail & mau mengupdate data
            getSupportActionBar().setTitle(R.string.editstudent);
            student = intent.getParcelableExtra("edit_data_stu");
            input_email.getEditText().setText(student.getEmail());
            input_email.setEnabled(false);
            input_pass.getEditText().setText(student.getPass());
            input_pass.setEnabled(false);
            input_name.getEditText().setText(student.getName());
            input_nim.getEditText().setText(student.getNim());
            input_age.getEditText().setText(student.getAge());
            input_address.getEditText().setText(student.getAddress());
            if(student.getGender().equalsIgnoreCase("male")){
                rg_gender.check(R.id.rb_student_m);
            }else{
                rg_gender.check(R.id.rb_student_f);
            }
            btn_register.setText(R.string.editstudent);
            btn_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                    getFormValue();
                    Map<String,Object> params = new HashMap<>();
                    params.put("email", email);
                    params.put("pass", pass);
                    params.put("name", name);
                    params.put("nim", nim);
                    params.put("gender", gender);
                    params.put("age", age);
                    params.put("address", address);
                    mDatabase.child("student").child(student.getUid()).updateChildren(params).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.cancel();
                            Intent intent;
                            intent = new Intent(RegisterActivity.this, StudentData.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this);
                            startActivity(intent, options.toBundle());
                            finish();
                        }
                    });
                }
            });
        }
    }

    public void getFormValue(){
        email = input_email.getEditText().getText().toString().trim();
        pass = input_pass.getEditText().getText().toString().trim();
        name = input_name.getEditText().getText().toString().trim();
        nim = input_nim.getEditText().getText().toString().trim();
        age = input_age.getEditText().getText().toString().trim();
        address = input_address.getEditText().getText().toString().trim();
    }

    public void addStudent(){
        getFormValue();
        dialog.show();
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    dialog.cancel();
                    uid = mAuth.getCurrentUser().getUid();
                    Student student = new Student(uid, email, pass, name, nim, gender, age, address);
                    mDatabase.child("student").child(uid).setValue(student).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterActivity.this,"Student registered successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                    mAuth.signOut();
                }else{
                    try {
                        throw task.getException();
                    }catch(FirebaseAuthInvalidCredentialsException malFormed){
                        Toast.makeText(RegisterActivity.this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
                    }catch(FirebaseAuthUserCollisionException existEmail){
                        Toast.makeText(RegisterActivity.this, "Email already registered!", Toast.LENGTH_SHORT).show();
                    }catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Register failed!", Toast.LENGTH_SHORT).show();
                    }
                    dialog.cancel();
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        getFormValue();

        if (!name.isEmpty() && !email.isEmpty() && !pass.isEmpty() && !age.isEmpty() && !address.isEmpty() && !nim.isEmpty() ){
            btn_register.setEnabled(true);
        }else{
            btn_register.setEnabled(false);
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
            intent = new Intent(RegisterActivity.this, StarterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
            return true;
        }else if(id == R.id.lecturer_list){
            Intent intent;
            intent = new Intent(RegisterActivity.this, StudentData.class);
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
        intent = new Intent(RegisterActivity.this, StarterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}