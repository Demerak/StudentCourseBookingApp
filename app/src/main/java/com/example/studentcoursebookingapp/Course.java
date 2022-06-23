package com.example.studentcoursebookingapp;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Course {
    private String name, courseId, courseDescription;
    private int studentCapacity;
    private ArrayList<TimeSlot> timeSlot;
    // TODO add course days, course hours, course description, student capacity

    public Course(String name, String courseId) {
        this(name, courseId, "courseDescriptionTODO", 0);
    }

    public Course(String name, String courseId, String courseDescription, int studentCapacity) {
        this.name = name;
        this.courseId = courseId;
        this.courseDescription = courseDescription;
        this.studentCapacity = studentCapacity;
    }

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
}
