package com.uc.saa1.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uc.saa1.CourseData;
import com.uc.saa1.R;
import com.uc.saa1.adapter.CourseAdapter;
import com.uc.saa1.adapter.CourseFragmentAdapter;
import com.uc.saa1.model.Course;

import java.util.ArrayList;

public class CoursesFragment extends Fragment {

    DatabaseReference dbCourse;
    ArrayList<Course> listCourse = new ArrayList<>();
    RecyclerView rv_course;

    public CoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbCourse = FirebaseDatabase.getInstance().getReference("course");
        rv_course = view.findViewById(R.id.rv_my_course);

        fetchCourseData();
    }

    public void fetchCourseData(){
        dbCourse.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCourse.clear();
                rv_course.setAdapter(null);
                for(DataSnapshot childSnapshot : snapshot.getChildren()){
                    Course course = childSnapshot.getValue(Course.class);
                    listCourse.add(course);
                }
                showCourseData(listCourse);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showCourseData(final ArrayList<Course> list){
        rv_course.setLayoutManager(new LinearLayoutManager(getActivity()));
        CourseFragmentAdapter courseFragmentAdapter = new CourseFragmentAdapter(getActivity());
        courseFragmentAdapter.setListCourse(list);
        rv_course.setAdapter(courseFragmentAdapter);

    }
}