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

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uc.saa1.CourseActivity;
import com.uc.saa1.CourseData;
import com.uc.saa1.Glovar;
import com.uc.saa1.R;
import com.uc.saa1.model.Course;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CardViewViewHolder>{

    private Context context;
    private ArrayList<Course> listCourse;
    private ArrayList<Course> getListCourse() {
        return listCourse;
    }
    public void setListCourse(ArrayList<Course> listCourse) {
        this.listCourse = listCourse;
    }
    public CourseAdapter(Context context) {
        this.context = context;
    }
    Dialog dialog;
    AlphaAnimation klik = new AlphaAnimation(1F, 0.6F);
    private DatabaseReference mDatabase;

    @NonNull
    @Override
    public CourseAdapter.CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_adapter, parent, false);
        return new CourseAdapter.CardViewViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final CourseAdapter.CardViewViewHolder holder, int position) {
        final Course course = getListCourse().get(position);
        mDatabase = FirebaseDatabase.getInstance().getReference("course");
        holder.lbl_subject.setText(course.getSubject());
        holder.lbl_lecturer.setText(course.getLecturer());
        holder.lbl_day.setText(course.getDay());
        holder.lbl_start.setText(course.getStart());
        holder.lbl_end.setText(course.getEnd());

        holder.btn_del.setOnClickListener(new View.OnClickListener() {
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
                                        dialog.cancel();
                                        mDatabase.child(course.getId()).removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                Intent in = new Intent(context, CourseData.class);
                                                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                Toast.makeText(context, "Course Deleted successfully!", Toast.LENGTH_SHORT).show();
                                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context);
                                                context.startActivity(in, options.toBundle());
//                                                finish();
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


        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(klik);
                Intent in = new Intent(context, CourseActivity.class);
                in.putExtra("action", "edit");
                in.putExtra("edit_data_course", course);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context);
                context.startActivity(in, options.toBundle());
//                finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return getListCourse().size();
    }

    class CardViewViewHolder extends RecyclerView.ViewHolder{
        TextView lbl_subject, lbl_lecturer, lbl_day, lbl_start, lbl_end;
        ImageView btn_del, btn_edit;

        CardViewViewHolder(View itemView) {
            super(itemView);
            lbl_subject = itemView.findViewById(R.id.lbl_course_c);
            lbl_lecturer = itemView.findViewById(R.id.lbl_lecturer_c);
            lbl_day = itemView.findViewById(R.id.lbl_day_c);
            lbl_start = itemView.findViewById(R.id.lbl_start_c);
            lbl_end = itemView.findViewById(R.id.lbl_end_c);
            btn_del = itemView.findViewById(R.id.img_del_c);
            btn_edit = itemView.findViewById(R.id.img_enroll_c);
            dialog = Glovar.loadingDialog(context);
        }
    }
}
