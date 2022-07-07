package com.example.studentcoursebookingapp;

import java.util.List;

public class CoursePeriod {

    private CourseTime startTime, endTime;
    private String day;

    public CoursePeriod() {
    }

    class CourseTime{
        private int hour, minute;

        public CourseTime(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }

        public String getTime() {
            return hour + ":" + minute;
        }
    }
    public void setStartTime(int hour,int minute){
        startTime = new CourseTime(hour, minute);
    }
    public void setEndTime(int hour,int minute){
        endTime = new CourseTime(hour, minute);
    }

    public String getStartTime() {
        return startTime.getTime();
    }

    public String getEndTime() {
        return endTime.getTime();
    }




    public int getStartTimeHour (){

        return startTime.hour;
    }
    public int getStartTimeMinute(){

        return startTime.minute;
    }
    public int getEndTimeHour (){

        return endTime.hour;
    }
    public int getEndTimeMinute(){
        return endTime.minute;
    }

    public String getDay(){
        return day;
    }

    public void setDay(String day) {
        this.day=day;
    }
}