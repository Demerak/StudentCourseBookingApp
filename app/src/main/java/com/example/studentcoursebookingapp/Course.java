package com.example.studentcoursebookingapp;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Course implements Parcelable {
    private String name, courseId, courseDescription;
    private String studentCapacity; // initially a int but it cause some problems
    private String courseDay1;
    private String courseDay2;
    private String courseStartTime1;
    private String courseStartTime2;
    private String courseEndTime1;
    private String courseEndTime2;

    public Course() {
        this("", "");
    }

    public Course(String name, String courseId) {
        this(name, courseId, "", "0");
    }

    public Course(String name, String courseId, String courseDescription, String studentCapacity) {
        this.name = name;
        this.courseId = courseId;
        this.courseDescription = courseDescription;
        this.studentCapacity = studentCapacity;
        this.courseDay1 = "";
        this.courseDay2 = "";
    }

    protected Course(Parcel in) {
        name = in.readString();
        courseId = in.readString();
        courseDescription = in.readString();
        studentCapacity = in.readString();
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public String getStudentCapacity() {
        return studentCapacity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public void setStudentCapacity(String studentCapacity) {
        this.studentCapacity = studentCapacity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.courseId);
        parcel.writeString(this.courseDescription);
        parcel.writeString(this.studentCapacity);
    }

    public void setCourseDay1(String day) {
        this.courseDay1 = day;
    }

    public void setCourseDay2(String day) {
        this.courseDay2 = day;
    }

    public void setCourseStartTime2(String courseStartTime2) {
        this.courseStartTime2 = courseStartTime2;
    }

    public void setCourseEndTime1(String courseEndTime1) {
        this.courseEndTime1 = courseEndTime1;
    }

    public void setCourseEndTime2(String courseEndTime2) {
        this.courseEndTime2 = courseEndTime2;
    }

    public void setCourseStartTime1(String courseStartTime1) {
        this.courseStartTime1 = courseStartTime1;
    }

    public String getCourseStartTime1() {
        return courseStartTime1;
    }

    public String getCourseStartTime2() {
        return courseStartTime2;
    }

    public String getCourseEndTime1() {
        return courseEndTime1;
    }

    public String getCourseEndTime2() {
        return courseEndTime2;
    }

    public String getCourseDay1() { return courseDay1; }

    public String getCourseDay2() { return courseDay2; }
}