package com.example.studentcoursebookingapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.LocalTime;
import java.time.format.DateTimeFormatter; // Import the DateTimeFormatter class


public class TimeSlot implements Parcelable {

    private LocalTime startingTime;
    private LocalTime endTime;
    private CourseType courseType;
    // TODO add what weekday

    public TimeSlot(LocalTime startingTime, LocalTime endTime, CourseType courseType) {
        this.startingTime = startingTime;
        this.endTime = endTime;
        this.courseType = courseType;
    }

    protected TimeSlot(Parcel in) {

    }

    public static final Creator<TimeSlot> CREATOR = new Creator<TimeSlot>() {
        @Override
        public TimeSlot createFromParcel(Parcel in) {
            return new TimeSlot(in);
        }

        @Override
        public TimeSlot[] newArray(int size) {
            return new TimeSlot[size];
        }
    };

    public void setStartingTime(LocalTime startingTime) {
        this.startingTime = startingTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(startingTime.toString());
        parcel.writeString(endTime.toString());
        parcel.writeString(courseType.toString());
    }
}
