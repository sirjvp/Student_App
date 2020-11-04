package com.uc.saa1.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable {
    private String uid;
    private String email;
    private String pass;
    private String name;
    private String nim;
    private String gender;
    private String age;
    private String address;

    public Student(){}

    public Student(String uid, String email, String pass, String name, String nim, String gender, String age, String address) {
        this.uid = uid;
        this.email = email;
        this.pass = pass;
        this.name = name;
        this.nim = nim;
        this.gender = gender;
        this.age = age;
        this.address = address;
    }

    protected Student(Parcel in) {
        uid = in.readString();
        email = in.readString();
        pass = in.readString();
        name = in.readString();
        nim = in.readString();
        gender = in.readString();
        age = in.readString();
        address = in.readString();
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }

    public String getName() {
        return name;
    }

    public String getNim() {
        return nim;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(email);
        dest.writeString(pass);
        dest.writeString(name);
        dest.writeString(nim);
        dest.writeString(gender);
        dest.writeString(age);
        dest.writeString(address);
    }
}
