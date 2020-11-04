package com.uc.saa1.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uc.saa1.Glovar;
import com.uc.saa1.HomeActivity;
import com.uc.saa1.R;
import com.uc.saa1.model.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CourseFragmentAdapter extends RecyclerView.Adapter<CourseFragmentAdapter.CardViewViewHolder>{

    private Context context;
    private ArrayList<Course> listCourse;
    private ArrayList<Course> getListCourse() {
        return listCourse;
    }
    public void setListCourse(ArrayList<Course> listCourse) {
        this.listCourse = listCourse;
    }
    public CourseFragmentAdapter(Context context) {
        this.context = context;
    }
    Dialog dialog;
    private DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String subject, day, start, end, lecturer, cid;
    Boolean timeConflict;
    Boolean courseConflict;


    @NonNull
    @Override
    public CourseFragmentAdapter.CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_fragment_adapter, parent, false);
        return new CourseFragmentAdapter.CardViewViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final CourseFragmentAdapter.CardViewViewHolder holder, int position) {
        final Course course = getListCourse().get(position);
        holder.lbl_subject.setText(course.getSubject());
        holder.lbl_lecturer.setText(course.getLecturer());
        holder.lbl_day.setText(course.getDay());
        holder.lbl_start.setText(course.getStart());
        holder.lbl_end.setText(course.getEnd());
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("student").child(mUser.getUid());
        holder.btn_enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                ConflictCheck(course);
//                cid = course.getId();
//                subject = course.getSubject();
//                day = course.getDay();
//                start = course.getStart();
//                end = course.getEnd();
//                lecturer = course.getLecturer();
//                dialog.show();
//                Course course = new Course(cid, subject, day, start, end, lecturer);
//

//                for(int i=0;i<3;i++) {
//                    if (list.get(i) != course) {
//                        mDatabase.child("course").child(cid).setValue(course).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                dialog.cancel();
//                                Toast.makeText(context, "Added Course Succesfully", Toast.LENGTH_SHORT).show();
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                dialog.cancel();
//                                Toast.makeText(context, "Add Course Failed", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                    else{
//                        dialog.cancel();
//                        Toast.makeText(context, "Already been registered", Toast.LENGTH_SHORT).show();
//                    }
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return getListCourse().size();
    }

    class CardViewViewHolder extends RecyclerView.ViewHolder{
        TextView lbl_subject, lbl_lecturer, lbl_day, lbl_start, lbl_end;
        ImageView btn_enroll;


        CardViewViewHolder(View itemView) {
            super(itemView);
            lbl_subject = itemView.findViewById(R.id.lbl_course_c);
            lbl_lecturer = itemView.findViewById(R.id.lbl_lecturer_c);
            lbl_day = itemView.findViewById(R.id.lbl_day_c);
            lbl_start = itemView.findViewById(R.id.lbl_start_c);
            lbl_end = itemView.findViewById(R.id.lbl_end_c);
            btn_enroll = itemView.findViewById(R.id.img_enroll_c);
            dialog = Glovar.loadingDialog(context);

        }
    }

    MutableLiveData<Course> addCourse = new MutableLiveData<>();

    public MutableLiveData<Course> getAddCourse() {
        return addCourse;
    }

    public void ConflictCheck(final Course course_temp) {

        final int course_temp_time_start = Integer.parseInt(course_temp.getStart().replace(":", ""));
        final int course_temp_time_end = Integer.parseInt(course_temp.getEnd().replace(":", ""));
        final String course_temp_day = course_temp.getDay();
//        final String course_temp_id = course_temp.getId();

        mDatabase.child("course").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timeConflict = false;
                courseConflict = false;

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Course course = childSnapshot.getValue(Course.class);

                    String course_day = course.getDay();
//                    String course_id = course.getId();

                    int course_time_start = Integer.parseInt(course.getStart().replace(":", ""));
                    int course_time_end = Integer.parseInt(course.getEnd().replace(":", ""));

                    //ngecek kalau subject sudah diambil
                    if (course_temp_time_start == course_time_start && course_temp_time_end == course_time_end) {
                        courseConflict = true;
                        break;
                    }else{
                        //ngecek kalau jadwal berada di hari yang sama
                        if (course_day.equalsIgnoreCase(course_temp_day)) {

                            //ngecek kalau jam mulai berada dalam range waktu yang sudah diambil
                            if (course_temp_time_start > course_time_start && course_temp_time_start < course_time_end) {
                                timeConflict = true;
                                break;
                            }

                            //ngecek kalau jam selesai berada dalam range waktu yang sudah diambil
                            if (course_temp_time_end > course_time_start && course_temp_time_end < course_time_end) {
                                timeConflict = true;
                                break;
                            }
                        }
                    }
                }
                if(courseConflict == true) {
                    dialog.cancel();
                    new AlertDialog.Builder(context)
                            .setTitle("Warning")
                            .setIcon(R.drawable.ic_android_goldtrans_24dp)
                            .setMessage("Course Schedule is already taken")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            })
                            .create()
                            .show();
                }else {
                    if (timeConflict == true) {
                        dialog.cancel();
                        new AlertDialog.Builder(context)
                                .setTitle("Warning")
                                .setIcon(R.drawable.ic_android_goldtrans_24dp)
                                .setMessage("Course Schedule conflict with the others!")
                                .setCancelable(true)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                })
                                .create()
                                .show();
                    } else {
                        cid = course_temp.getId();
                        subject = course_temp.getSubject();
                        day = course_temp.getDay();
                        start = course_temp.getStart();
                        end = course_temp.getEnd();
                        lecturer = course_temp.getLecturer();
                        Course course = new Course(cid, subject, day, start, end, lecturer);
                        mDatabase.child("course").child(cid).setValue(course).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.cancel();
                                Toast.makeText(context, "Added Course Succesfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.cancel();
                                Toast.makeText(context, "Add Course Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
