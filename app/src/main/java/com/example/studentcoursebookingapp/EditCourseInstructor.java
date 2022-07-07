package com.example.studentcoursebookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditCourseInstructor extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Button applyChangeBtn, returnHomeBtn;
    private TextView courseName, courseId, courseDesc, courseCapacity;
    private EditText newDesc, newCap;
    private Button startTimeBtn, startTime2Btn;
    private Button endTimeBtn, endTime2Btn;
    private int hour, minute;
    private Spinner dayOfWeekSpinner, dayOfWeekSpinner2;
    private ArrayAdapter<String> spinnerAdapter,spinnerAdapter2;
    private CoursePeriod coursePeriod1, coursePeriod2;

    private Course course, tempCourse;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course_instructor);

        Intent intent = getIntent();
        course = intent.getParcelableExtra("course");

        coursePeriod1 = new CoursePeriod();
        coursePeriod2 = new CoursePeriod();

        Log.d("courseReceived", course.getName());

        courseName = findViewById(R.id.courseName_inst);
        courseId = findViewById(R.id.courseId_inst);
        courseDesc = findViewById(R.id.courseDescription_inst);
        courseCapacity = findViewById(R.id.courseCapacity_inst);

        startTimeBtn = findViewById(R.id.startTime);
        startTime2Btn = findViewById(R.id.startTime2);

        endTimeBtn = findViewById(R.id.endTime);
        endTime2Btn = findViewById(R.id.endTime2);

        dayOfWeekSpinner = findViewById(R.id.spinnerDayOfWeek);
        dayOfWeekSpinner2 = findViewById(R.id.spinnerDayOfWeek2);

        spinnerAdapter = new ArrayAdapter<>(EditCourseInstructor.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.dayOfWeek));
        spinnerAdapter2 = new ArrayAdapter<>(EditCourseInstructor.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.dayOfWeek));

        // use android default spinner dropdown
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayOfWeekSpinner.setAdapter(spinnerAdapter);
        dayOfWeekSpinner2.setAdapter(spinnerAdapter2);
        dayOfWeekSpinner.setOnItemSelectedListener(this);
        dayOfWeekSpinner2.setOnItemSelectedListener(this);

        db = FirebaseFirestore.getInstance();

        // find view in layout
        newDesc = findViewById(R.id.new_course_desc_inst);
        newCap = findViewById(R.id.new_course_capacity_inst);

        tempCourse = new Course();
        setCourseData();

        // buttons
        applyChangeBtn = findViewById(R.id.applyChangeInstBtn);
        returnHomeBtn = findViewById(R.id.returnHomeInstBtn);

        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //popTimePicker1(startTimeBtn);
                popTimePicker(startTimeBtn, 1);
            }
        });

        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //popTimePicker2(endTimeBtn);
                popTimePicker(endTimeBtn, 2);
            }
        });

        startTime2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //popTimePicker3(startTime2Btn);
                popTimePicker(startTime2Btn, 3);
            }
        });

        endTime2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //popTimePicker4(endTime2Btn);
                popTimePicker(endTime2Btn, 4);
            }
        });

        applyChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String snewDesc = newDesc.getText().toString();
                String snewCap = newCap.getText().toString();

                if ((snewDesc.matches("")) || (snewCap.matches(""))) {
                    Toast.makeText(EditCourseInstructor.this, "You have an empty input", Toast.LENGTH_SHORT).show();
                    return;

                }
                int tempCap = Integer.parseInt(newCap.getText().toString());

                if(tempCap >= 500 ) { //Max capacity is 500
                    Toast.makeText(EditCourseInstructor.this, "The maximum capacity of a course is 500", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateData();
            }
        });

        returnHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToHomeActivity();
            }
        });
    }

    private void popTimePicker(View view, int btn) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectHour, int selectMin) {
                hour= selectHour;
                minute = selectMin;
                Button timeBtn = (Button) view;
                switch (btn) {
                    case 1:
                        coursePeriod1.setStartTime(hour,minute);
                        Log.d("time", "1");
                        break;
                    case 2:
                        coursePeriod1.setEndTime(hour,minute);
                        Log.d("time", "2");
                        break;
                    case 3:
                        coursePeriod2.setStartTime(hour,minute);
                        Log.d("time", "3");
                        break;
                    case 4:
                        coursePeriod2.setEndTime(hour,minute);
                        Log.d("time", "4");
                        break;
                }


                timeBtn.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
        timePickerDialog.show();
    }

    private void setCourseData() {
        // set the edit text default text with the course details
        db.collection("courses").whereEqualTo("name", course.getName()).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot : snapshotList) {
                            Log.d("tempCourse", "here" + String.valueOf(snapshot.contains(CourseField.name.toString())));
                            // this is what we should do ... tempCourse = snapshot.toObject(Course.class);

                            // name
                            if (snapshot.contains(CourseField.name.toString())) {
                                tempCourse.setName((String) snapshot.get(CourseField.name.toString()));
                            }

                            // course id
                            if (snapshot.contains(CourseField.courseId.toString())) {
                                tempCourse.setCourseId((String) snapshot.get(CourseField.courseId.toString()));
                            }

                            // course Description
                            if (snapshot.contains(CourseField.courseDescription.toString())) {
                                tempCourse.setCourseDescription((String) snapshot.get(CourseField.courseDescription.toString()));
                            }

                            // student Capacity
                            if (snapshot.contains(CourseField.studentCapacity.toString())) {
                                tempCourse.setStudentCapacity((String) snapshot.get(CourseField.studentCapacity.toString()));
                            }

                            // course Day 1
                            if (snapshot.contains(CourseField.courseDay1.toString())) {
                                String day = (String) snapshot.get(CourseField.courseDay1.toString());
                                tempCourse.setCourseDay1(day);
                                Log.d("day", day);
                                Log.d("day", Integer.toString(dayOfTheWeekToInt(day)));
                                dayOfWeekSpinner.setSelection(dayOfTheWeekToInt(day));
                            }

                            // course Day 2
                            if (snapshot.contains(CourseField.courseDay2.toString())) {
                                String day = (String) snapshot.get(CourseField.courseDay2.toString());
                                tempCourse.setCourseDay2(day);
                                dayOfWeekSpinner2.setSelection(dayOfTheWeekToInt(day));
                            }

                            //        dayOfWeekSpinner.setSelection(course.getCourseDay1());
                            //        dayOfWeekSpinner2.setSelection(course.getCourseDay2());

                            // course start time 1
                            if (snapshot.contains(CourseField.courseStartTime1.toString())) {
                                tempCourse.setCourseStartTime1((String) snapshot.get(CourseField.courseStartTime1.toString()));
                                int[] hourAndMin = getHoursAndMin(tempCourse.getCourseStartTime1().split(":"));
                                startTimeBtn.setText(String.format(Locale.getDefault(), "%02d:%02d", hourAndMin[0], hourAndMin[1]));
                                coursePeriod1.setStartTime(hourAndMin[0], hourAndMin[1]);
                            }

                            // course start time 2
                            if (snapshot.contains(CourseField.courseStartTime2.toString())) {
                                tempCourse.setCourseStartTime2((String) snapshot.get(CourseField.courseStartTime2.toString()));
                                int[] hourAndMin = getHoursAndMin(tempCourse.getCourseStartTime2().split(":"));
                                startTime2Btn.setText(String.format(Locale.getDefault(), "%02d:%02d", hourAndMin[0], hourAndMin[1]));
                                coursePeriod2.setStartTime(hourAndMin[0], hourAndMin[1]);
                            }

                            // course end time 1
                            if (snapshot.contains(CourseField.courseEndTime1.toString())) {
                                tempCourse.setCourseEndTime1((String) snapshot.get(CourseField.courseEndTime1.toString()));
                                int[] hourAndMin = getHoursAndMin(tempCourse.getCourseEndTime1().split(":"));
                                endTimeBtn.setText(String.format(Locale.getDefault(), "%02d:%02d", hourAndMin[0], hourAndMin[1]));
                                coursePeriod1.setEndTime(hourAndMin[0], hourAndMin[1]);
                            }

                            // course end time 2
                            if (snapshot.contains(CourseField.courseEndTime2.toString())) {
                                tempCourse.setCourseEndTime2((String) snapshot.get(CourseField.courseEndTime2.toString()));
                                int[] hourAndMin = getHoursAndMin(tempCourse.getCourseEndTime2().split(":"));
                                endTime2Btn.setText(String.format(Locale.getDefault(), "%02d:%02d", hourAndMin[0], hourAndMin[1]));
                                coursePeriod2.setEndTime(hourAndMin[0], hourAndMin[1]);
                            }

                            courseName.setText(tempCourse.getName());
                            courseId.setText("Course id: " + tempCourse.getCourseId());

                            courseDesc.setText("Course description : "+ tempCourse.getCourseDescription());
                            courseCapacity.setText("Course capacity : "+ tempCourse.getStudentCapacity());

                            // not very good practice but setting these value temporarily (TODO CHANGE COURSE IMPLEMENTATION TO INCLUDE COURSEPERIOD)
//                            coursePeriod1.setStartTime(course.getCourseStartHour1(),course.getCourseStartMinute1());
//                            coursePeriod1.setEndTime(course.getCourseEndHour1(),course.getCourseEndMinute1());
//                            coursePeriod2.setStartTime(course.getCourseStartHour2(), course.getCourseStartMinute2());
//                            coursePeriod2.setEndTime(course.getCourseEndHour2(), course.getCourseEndMinute2());

                        }
                    }});

        Log.d("tempCourse", "name " + tempCourse.getName());
        Log.d("tempCourse", "id" + tempCourse.getCourseId());
        Log.d("tempCourse", "desc" + tempCourse.getCourseDescription());
        Log.d("tempCourse", "capacity" + tempCourse.getStudentCapacity());
    }

    private int dayOfTheWeekToInt(String day) {
        switch (day){
            case "Monday":
                return 0;
            case "Tuesday":
                return 1;
            case "Wednesday":
                return 2;
            case "Thursday":
                return 3;
            case "Friday":
                return 4;
            default :
                return 0;
        }
    }

    private int[] getHoursAndMin(String[] str) {
        int[] hourAndMin = new int[2];
        hourAndMin[0] = Integer.parseInt(str[0]);
        hourAndMin[1] = Integer.parseInt(str[1]);
        return hourAndMin;
    }
    private void updateData() {
        Map<String,Object> courseDetails = new HashMap<>();
        Log.d("details", String.valueOf(newCap.getText().toString().isEmpty()));

        if (newDesc.getText().toString().isEmpty()) {
            // add the current course description
            courseDetails.put(CourseField.courseDescription.toString(), tempCourse.getCourseDescription());
        } else {
            // add the new course description
            courseDetails.put(CourseField.courseDescription.toString(), newDesc.getText().toString());
        }

        if (newCap.getText().toString().isEmpty()) {
            // add the current course capacity
            courseDetails.put(CourseField.studentCapacity.toString(), tempCourse.getStudentCapacity());
        } else {
            // add the new course capacity
            courseDetails.put(CourseField.studentCapacity.toString(), newCap.getText().toString());
        }

        courseDetails.put(CourseField.courseStartTime1.toString(), coursePeriod1.getStartTime());
        courseDetails.put(CourseField.courseEndTime1.toString(), coursePeriod1.getEndTime());
        courseDetails.put(CourseField.courseDay1.toString(),dayOfWeekSpinner.getSelectedItem().toString());

        courseDetails.put(CourseField.courseStartTime2.toString(), coursePeriod2.getStartTime());
        courseDetails.put(CourseField.courseEndTime2.toString(), coursePeriod2.getEndTime());
        courseDetails.put(CourseField.courseDay2.toString(),dayOfWeekSpinner2.getSelectedItem().toString());

//        Log.d("details", "1");
//        Log.d("details", coursePeriod1.getStartTime());
//        Log.d("details", coursePeriod1.getEndTime());
//        Log.d("details", dayOfWeekSpinner.getSelectedItem().toString());
//
//        Log.d("details", coursePeriod2.getStartTime());
//        Log.d("details", coursePeriod2.getEndTime());
//        Log.d("details", dayOfWeekSpinner2.getSelectedItem().toString());
//        Log.d("details", "2");

        db.collection("courses").whereEqualTo(CourseField.courseId.toString(), course.getCourseId())
            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d("details", "here");
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    String documentID = task.getResult().getDocuments().get(0).getId();
                    db.collection("courses")
                            .document(documentID)
                            .update(courseDetails)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     Toast.makeText(EditCourseInstructor.this, "successfully updated data", Toast.LENGTH_SHORT).show();
                                     setCourseData();
                                 }
                             }
                            );
                } else {
                    Toast.makeText(EditCourseInstructor.this, "Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void goBackToHomeActivity() {
        // Go back Course Activity
        Intent intent = new Intent(EditCourseInstructor.this, HomeInstructorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}