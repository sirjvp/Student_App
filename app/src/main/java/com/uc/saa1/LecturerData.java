package com.uc.saa1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uc.saa1.adapter.LecturerAdapter;
import com.uc.saa1.model.Lecturer;

import java.util.ArrayList;

public class LecturerData extends AppCompatActivity {
    Toolbar toolbar;
    DatabaseReference dbLecturer;
    ArrayList<Lecturer> listLecturer = new ArrayList<>();
    RecyclerView rv_lecturer;
    AlphaAnimation klik = new AlphaAnimation(1F, 0.6F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_data);
        toolbar = findViewById(R.id.tb_lecturer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dbLecturer = FirebaseDatabase.getInstance().getReference("lecturer");
        rv_lecturer = findViewById(R.id.rv_lecturer);

        fetchLecturerData();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void fetchLecturerData(){
        dbLecturer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listLecturer.clear();
                rv_lecturer.setAdapter(null);
                for(DataSnapshot childSnapshot : snapshot.getChildren()){
                    Lecturer lecturer = childSnapshot.getValue(Lecturer.class);
                    listLecturer.add(lecturer);
                }
                showLecturerData(listLecturer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showLecturerData(final ArrayList<Lecturer> list){
        rv_lecturer.setLayoutManager((new LinearLayoutManager(LecturerData.this)));
        LecturerAdapter lecturerAdapter = new LecturerAdapter(LecturerData.this);
        lecturerAdapter.setListLecturer(list);
        rv_lecturer.setAdapter(lecturerAdapter);

        ItemClickSupport.addTo(rv_lecturer).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                v.startAnimation(klik);
                Intent intent = new Intent(LecturerData.this, LecturerDetail.class);
                Lecturer lecturer = new Lecturer(list.get(position).getId(), list.get(position).getName(), list.get(position).getGender(), list.get(position).getExpertise());
                intent.putExtra("data_lecturer", lecturer);
                intent.putExtra("position", position);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LecturerData.this);
                startActivity(intent, options.toBundle());
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.lecturer_list){
            Intent intent;
            intent = new Intent(LecturerData.this, LecturerActivity.class);
            intent.putExtra("action", "add");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LecturerData.this);
            startActivity(intent, options.toBundle());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(LecturerData.this, LecturerActivity.class);
        intent.putExtra("action", "add");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}

