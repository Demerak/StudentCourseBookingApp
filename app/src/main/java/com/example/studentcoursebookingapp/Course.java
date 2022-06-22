package com.example.studentcoursebookingapp;

public class Course {
    private String name, courseId;
    // TODO add course days, course hours, course description, student capacity

    public Course(String name, String courseId) {
        this.name = name;
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public String getCourseId() { return courseId; }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourseId(String courseId) { this.courseId = courseId; }

}
