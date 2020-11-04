package com.uc.saa1.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uc.saa1.CourseData;
import com.uc.saa1.Glovar;
import com.uc.saa1.HomeActivity;
import com.uc.saa1.R;
import com.uc.saa1.fragment.ScheduleFragment;
import com.uc.saa1.model.Course;

import java.util.ArrayList;

public class ScheduleFragmentAdapter extends RecyclerView.Adapter<ScheduleFragmentAdapter.CardViewViewHolder>{

    private Context context;
    private ArrayList<Course> listCourse;
    private ArrayList<Course> getListCourse() {
        return listCourse;
    }
    public void setListCourse(ArrayList<Course> listCourse) {
        this.listCourse = listCourse;
    }
    public ScheduleFragmentAdapter(Context context) {
        this.context = context;
    }
    Dialog dialog;
    AlphaAnimation klik = new AlphaAnimation(1F, 0.6F);
    private DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String cid;


    @NonNull
    @Override
    public ScheduleFragmentAdapter.CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_fragment_adapter, parent, false);
        return new ScheduleFragmentAdapter.CardViewViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ScheduleFragmentAdapter.CardViewViewHolder holder, int position) {
        final Course course = getListCourse().get(position);
        holder.lbl_subject.setText(course.getSubject());
        holder.lbl_lecturer.setText(course.getLecturer());
        holder.lbl_day.setText(course.getDay());
        holder.lbl_start.setText(course.getStart());
        holder.lbl_end.setText(course.getEnd());
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("student").child(mUser.getUid());
//        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cid = course.getId();
//                dialog.show();
//                Course course = new Course(cid, subject, day, start, end, lecturer);
//                mDatabase.child("course").child(cid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        dialog.cancel();
//                        Toast.makeText(context, "Course Deleted Successully", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        dialog.cancel();
//                        Toast.makeText(context, "Course Delete Failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(klik);
                new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setIcon(R.drawable.ic_android_goldtrans_24dp)
                        .setMessage("Are you sure to delete "+course.getSubject()+" data?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                dialog.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        cid = course.getId();
                                        mDatabase.child("course").child(cid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dialog.cancel();
                                                Intent in = new Intent(context, HomeActivity.class);
                                                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                Toast.makeText(context, "Course Deleted Successully!", Toast.LENGTH_SHORT).show();
                                                context.startActivity(in);
//                                                finish();
                                                dialogInterface.cancel();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.cancel();
                                                Toast.makeText(context, "Course Delete Failed", Toast.LENGTH_SHORT).show();
                                                dialogInterface.cancel();
                                            }
                                        });
                                    }
                                }, 1000);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return getListCourse().size();
    }

    class CardViewViewHolder extends RecyclerView.ViewHolder{
        TextView lbl_subject, lbl_lecturer, lbl_day, lbl_start, lbl_end;
        ImageView btn_remove;


        CardViewViewHolder(View itemView) {
            super(itemView);
            lbl_subject = itemView.findViewById(R.id.lbl_course_c);
            lbl_lecturer = itemView.findViewById(R.id.lbl_lecturer_c);
            lbl_day = itemView.findViewById(R.id.lbl_day_c);
            lbl_start = itemView.findViewById(R.id.lbl_start_c);
            lbl_end = itemView.findViewById(R.id.lbl_end_c);
            btn_remove = itemView.findViewById(R.id.img_remove_c);
            dialog = Glovar.loadingDialog(context);

        }
    }
}
