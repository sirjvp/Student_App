package com.uc.saa1.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uc.saa1.R;
import com.uc.saa1.adapter.CourseFragmentAdapter;
import com.uc.saa1.adapter.ScheduleFragmentAdapter;
import com.uc.saa1.model.Course;

import java.util.ArrayList;

public class ScheduleFragment extends Fragment {

    DatabaseReference dbSchedule;
    ArrayList<Course> listSchedule = new ArrayList<>();
    RecyclerView rv_schedule;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        dbSchedule = FirebaseDatabase.getInstance().getReference("student").child(mUser.getUid()).child("course");
        rv_schedule = view.findViewById(R.id.rv_my_schedule);

        fetchCourseData();
    }

    public void fetchCourseData(){
        dbSchedule.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listSchedule.clear();
                rv_schedule.setAdapter(null);
                for(DataSnapshot childSnapshot : snapshot.getChildren()){
                    Course course = childSnapshot.getValue(Course.class);
                    listSchedule.add(course);
                }
                showCourseData(listSchedule);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showCourseData(final ArrayList<Course> list){
        rv_schedule.setLayoutManager(new LinearLayoutManager(getActivity()));
        ScheduleFragmentAdapter scheduleFragmentAdapter = new ScheduleFragmentAdapter(getActivity());
        scheduleFragmentAdapter.setListCourse(list);
        rv_schedule.setAdapter(scheduleFragmentAdapter);

    }
}