package com.example.studentcoursebookingapp;

import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.format.DateTimeFormatter; // Import the DateTimeFormatter class


public class TimeSlot {

    private LocalDateTime startingTime;
    private LocalDateTime endTime;
    private CourseType courseType;

    public TimeSlot(LocalDateTime startingTime, LocalDateTime endTime, CourseType courseType) {
        this.startingTime = startingTime;
        this.endTime = endTime;
        this.courseType = courseType;
    }

    public void setStartingTime(LocalDateTime startingTime) {
        this.startingTime = startingTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }
}
