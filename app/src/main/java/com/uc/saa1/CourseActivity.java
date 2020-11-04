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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uc.saa1.model.Course;
import com.uc.saa1.model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CourseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    TextInputLayout input_subject;
    Button btn_add;
    Toolbar toolbar;
    Dialog dialog;
    Spinner spinner_day, spinner_start, spinner_end, spinner_lecturer;
    String subject, day, start, end, lecturer, action;
    private DatabaseReference mDatabase;
    private DatabaseReference dbStudentChild;
    private DatabaseReference dbStudent;
    private ArrayList<String> listLecturer;
    Course course;
    ArrayAdapter<CharSequence> adapterend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        toolbar = findViewById(R.id.tb_course);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        input_subject = findViewById(R.id.input_subject);
        btn_add = findViewById(R.id.btn_course);
        dialog = Glovar.loadingDialog(CourseActivity.this);

        spinner_day = findViewById(R.id.s_day);
        spinner_start = findViewById(R.id.s_start);
        spinner_end = findViewById(R.id.s_end);
        spinner_lecturer = findViewById(R.id.s_lecturer);

        listLecturer = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("lecturer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                listLecturer.clear();
                for(DataSnapshot childSnapshot : snapshot.getChildren()){
                    String lecturer = childSnapshot.child("name").getValue(String.class);
                    listLecturer.add(lecturer);
                }
                ArrayAdapter<String> adapterLecturer = new ArrayAdapter<>(CourseActivity.this, android.R.layout.simple_spinner_item,listLecturer);
                adapterLecturer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_lecturer.setAdapter(adapterLecturer);
                if(action.equalsIgnoreCase("edit")){
                    int lecturerindex = adapterLecturer.getPosition(course.getLecturer());
                    spinner_lecturer.setSelection(lecturerindex);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            };
        });

        input_subject.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                subject = input_subject.getEditText().getText().toString().trim();
                if (!subject.isEmpty()){
                    btn_add.setEnabled(true);
                }else{
                    btn_add.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ArrayAdapter<CharSequence> adapterday = ArrayAdapter.createFromResource(this,R.array.day, android.R.layout.simple_spinner_item);
        adapterday.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_day.setAdapter(adapterday);

        ArrayAdapter<CharSequence> adapterstart = ArrayAdapter.createFromResource(this,R.array.start, android.R.layout.simple_spinner_item);
        adapterstart.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_start.setAdapter(adapterstart);
        spinner_start.setOnItemSelectedListener(this);

        final ArrayAdapter<CharSequence> adapterlecturer = ArrayAdapter.createFromResource(this, R.array.lecturer, android.R.layout.simple_spinner_item);
//        ArrayAdapter<String> adapterlecturer = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listLecturer);
        adapterlecturer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        adapterlecturer.notifyDataSetChanged();
        spinner_lecturer.setAdapter(adapterlecturer);

        Intent intent = getIntent();
        action = intent.getStringExtra("action");
        if(action.equals("add")){
            getSupportActionBar().setTitle(R.string.addcourse);
            btn_add.setText(R.string.addcourse);
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subject = input_subject.getEditText().getText().toString().trim();
                    day = spinner_day.getSelectedItem().toString().trim();
                    start = spinner_start.getSelectedItem().toString().trim();
                    end = spinner_end.getSelectedItem().toString().trim();
                    lecturer = spinner_lecturer.getSelectedItem().toString().trim();
                    addCourse(subject, day, start, end, lecturer);
                }
            });
        }
        else{
            getSupportActionBar().setTitle(R.string.editcourse);
            course = intent.getParcelableExtra("edit_data_course");
            input_subject.getEditText().setText(course.getSubject());

            int dayIndex = adapterday.getPosition(course.getDay());
            spinner_day.setSelection(dayIndex);

            int startIndex = adapterstart.getPosition(course.getStart());
            spinner_start.setSelection(startIndex);

            setSpinnerTimeEnd(startIndex);
            int endIndex = adapterend.getPosition(course.getEnd());
            spinner_end.setSelection(endIndex);

//            int lecturerindex = adapterlecturer.getPosition(course.getLecturer());
//            spinner_lecturer.setSelection(lecturerindex);

            btn_add.setText(R.string.editcourse);
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                    subject = input_subject.getEditText().getText().toString().trim();
                    day = spinner_day.getSelectedItem().toString().trim();
                    start = spinner_end.getSelectedItem().toString().trim();
                    end = spinner_end.getSelectedItem().toString().trim();
                    lecturer = spinner_lecturer.getSelectedItem().toString().trim();
                    Map<String,Object> params = new HashMap<>();
                    params.put("subject", subject);
                    params.put("day", day);
                    params.put("start", start);
                    params.put("end", end);
                    params.put("lecturer", lecturer);
                    mDatabase.child("course").child(course.getId()).updateChildren(params).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.cancel();
                            Toast.makeText(CourseActivity.this, "Updated Successful", Toast.LENGTH_SHORT).show();
                            Intent intent;
                            intent = new Intent(CourseActivity.this, CourseData.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(CourseActivity.this);
                            startActivity(intent, options.toBundle());
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CourseActivity.this, "Failed to Update", Toast.LENGTH_SHORT).show();
                        }
                    });
                    updateCourseStudent(course.getId());
                }
            });
        }
    }

    public void addCourse(String subject, String day, String start, String end, String idlecturer){
        dialog.show();
        String mid = mDatabase.child("course").push().getKey();
        Course course = new Course(mid, subject, day, start, end, idlecturer);
        mDatabase.child("course").child(mid).setValue(course).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.cancel();
                Toast.makeText(CourseActivity.this, "Add Course Succesfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.cancel();
                Toast.makeText(CourseActivity.this, "Add Course Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateCourseStudent(final String id){
        dbStudent = FirebaseDatabase.getInstance().getReference("student");
        dbStudent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot studID : dataSnapshot.getChildren()) {
                    dbStudentChild = mDatabase.child(studID.getValue(Student.class).getUid()).child("course");
                    dbStudentChild.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot coID : dataSnapshot.getChildren()) {

                                String coursegetid = coID.getValue(Course.class).getId();
                                if(id.equals(coursegetid)){
                                    Map<String, Object> params = new HashMap<>();
                                    params.put("subject", subject);
                                    params.put("day", day);
                                    params.put("start", start);
                                    params.put("end", end);
                                    params.put("lecturer", lecturer);
                                    dbStudentChild.child(coursegetid).updateChildren(params).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setSpinnerTimeEnd(int position) {
        if(position==0){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end0730, android.R.layout.simple_spinner_item);
        }else if(position==1){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end0800, android.R.layout.simple_spinner_item);
        }else if(position==2){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end0830, android.R.layout.simple_spinner_item);
        }else if(position==3){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end0900, android.R.layout.simple_spinner_item);
        }else if(position==4){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end0930, android.R.layout.simple_spinner_item);
        }else if(position==5){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end1000, android.R.layout.simple_spinner_item);
        }else if(position==6){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end1030, android.R.layout.simple_spinner_item);
        }else if(position==7){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end1100, android.R.layout.simple_spinner_item);
        }else if(position==8){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end1130, android.R.layout.simple_spinner_item);
        }else if(position==9){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end1200, android.R.layout.simple_spinner_item);
        }else if(position==10){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end1230, android.R.layout.simple_spinner_item);
        }else if(position==11){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end1300, android.R.layout.simple_spinner_item);
        }else if(position==12){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end1330, android.R.layout.simple_spinner_item);
        }else if(position==13){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end1400, android.R.layout.simple_spinner_item);
        }else if(position==14){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end1430, android.R.layout.simple_spinner_item);
        }else if(position==15){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end1500, android.R.layout.simple_spinner_item);
        }else if(position==16){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end1530, android.R.layout.simple_spinner_item);
        }else if(position==17){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end1600, android.R.layout.simple_spinner_item);
        }else if(position==18){
            adapterend = ArrayAdapter.createFromResource(CourseActivity.this, R.array.jam_end1630, android.R.layout.simple_spinner_item);
        }

        adapterend.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_end.setAdapter(adapterend);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setSpinnerTimeEnd(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
            intent = new Intent(CourseActivity.this, StarterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
            return true;
        }else if(id == R.id.lecturer_list){
            Intent intent;
            intent = new Intent(CourseActivity.this, CourseData.class);
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
        intent = new Intent(CourseActivity.this, StarterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}