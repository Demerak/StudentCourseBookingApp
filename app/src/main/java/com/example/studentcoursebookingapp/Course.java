package com.example.studentcoursebookingapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Course implements Parcelable {
    private String name, courseId, courseDescription;
    private int studentCapacity;
    private List<TimeSlot> timeSlot;

    public Course() {
        this("", "");
    }

    public Course(String name, String courseId) {
        this(name, courseId, "courseDescriptionTODO", 0);
    }

    public Course(String name, String courseId, String courseDescription, int studentCapacity) {
        this.name = name;
        this.courseId = courseId;
        this.courseDescription = courseDescription;
        this.studentCapacity = studentCapacity;
    }

    protected Course(Parcel in) {
        name = in.readString();
        courseId = in.readString();
        courseDescription = in.readString();
        studentCapacity = in.readInt();
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

    public void addTimeSlot(LocalDateTime startTime, LocalDateTime endTime, CourseType type) {
        TimeSlot newTimeSlot = new TimeSlot(startTime, endTime, type);
        timeSlot.add(newTimeSlot);
    }

    public String getName() {
        return name;
    }

    public String getCourseId() { return courseId; }

    public String getCourseDescription() { return courseDescription; }

    public int getStudentCapacity() { return studentCapacity; }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourseId(String courseId) { this.courseId = courseId; }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public void setStudentCapacity(int studentCapacity) {
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
        parcel.writeInt(this.studentCapacity);
    }
}
