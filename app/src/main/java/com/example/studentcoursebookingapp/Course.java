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
    private List<TimeSlot> timeSlot;

    public Course() {
        this("", "");
    }

    public Course(String name, String courseId) {
        this(name, courseId, "","0");
    }

    public Course(String name, String courseId, String courseDescription, String studentCapacity) {
        this.name = name;
        this.courseId = courseId;
        this.courseDescription = courseDescription;
        this.studentCapacity = studentCapacity;

        TimeSlot firstCourse = new TimeSlot(LocalTime.of(0,0), LocalTime.of(0,0), CourseType.Lecture);
        TimeSlot secondCourse = new TimeSlot(LocalTime.of(0,0), LocalTime.of(0,0), CourseType.Lecture);

        this.timeSlot = new ArrayList<>();
        this.timeSlot.add(firstCourse);
        this.timeSlot.add(secondCourse);
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

    public String getCourseId() { return courseId; }

    public String getCourseDescription() { return courseDescription; }

    public String getStudentCapacity() { return studentCapacity; }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourseId(String courseId) { this.courseId = courseId; }

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
        parcel.writeTypedList(this.timeSlot);
    }
}
