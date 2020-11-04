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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uc.saa1.Glovar;
import com.uc.saa1.R;
import com.uc.saa1.RegisterActivity;
import com.uc.saa1.StudentData;
import com.uc.saa1.model.Student;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.CardViewViewHolder>{

    private Context context;
    private ArrayList<Student> listStudent;
    private ArrayList<Student> getListStudent() {
        return listStudent;
    }
    public void setListStudent(ArrayList<Student> listStudent) {
        this.listStudent = listStudent;
    }
    public StudentAdapter(Context context) {
        this.context = context;
    }
    Dialog dialog;
    AlphaAnimation klik = new AlphaAnimation(1F, 0.6F);
    private DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @NonNull
    @Override
    public StudentAdapter.CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_adapter, parent, false);
        return new StudentAdapter.CardViewViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final StudentAdapter.CardViewViewHolder holder, int position) {
        final Student student = getListStudent().get(position);
        mDatabase = FirebaseDatabase.getInstance().getReference("student");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String gender_age = student.getGender() + "/" + student.getAge();
        holder.lbl_name.setText(student.getName());
        holder.lbl_nim.setText(student.getNim());
        holder.lbl_email.setText(student.getEmail());
        holder.lbl_gender_age.setText(gender_age);
        holder.lbl_address.setText(student.getAddress());
        holder.btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(klik);
                new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setIcon(R.drawable.ic_android_goldtrans_24dp)
                        .setMessage("Are you sure to delete "+student.getName()+" data?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                dialog.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.cancel();
                                        mAuth.signInWithEmailAndPassword(student.getEmail(), student.getPass()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                mAuth.getCurrentUser().delete();
                                                mDatabase.child(student.getUid()).removeValue(new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                        Intent in = new Intent(context, StudentData.class);
                                                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        Toast.makeText(context, "Student Deleted successfully!", Toast.LENGTH_SHORT).show();
                                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context);
                                                        context.startActivity(in, options.toBundle());
//                                                          finish();
                                                        dialogInterface.cancel();
                                                    }
                                                });
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
                Intent in = new Intent(context, RegisterActivity.class);
                in.putExtra("action", "edit");
                in.putExtra("edit_data_stu", student);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context);
                context.startActivity(in, options.toBundle());
//                finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return getListStudent().size();
    }

    class CardViewViewHolder extends RecyclerView.ViewHolder{
        TextView lbl_name, lbl_nim, lbl_email, lbl_gender_age, lbl_address;
        ImageView btn_del, btn_edit;

        CardViewViewHolder(View itemView) {
            super(itemView);
            lbl_name = itemView.findViewById(R.id.lbl_name_stu_adp);
            lbl_nim = itemView.findViewById(R.id.lbl_nim_stu_adp);
            lbl_email = itemView.findViewById(R.id.lbl_email_stu_adp);
            lbl_gender_age = itemView.findViewById(R.id.lbl_gender_stu_adp);
            lbl_address = itemView.findViewById(R.id.lbl_address_stu_adp);
            btn_del = itemView.findViewById(R.id.img_del_stu);
            btn_edit = itemView.findViewById(R.id.img_edit_stu);
            dialog = Glovar.loadingDialog(context);
        }
    }
}
