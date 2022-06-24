package com.example.studentcoursebookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.Locale;
import java.util.Map;

public class EditCourseInstructor extends AppCompatActivity {

    private Button applyChangeBtn, deleteBtn, returnHomeBtn;
    private TextView courseName, courseId, courseDesc, courseCapacity;
    private EditText newDesc, newCap;
    private Button startTimeBtn;
    private Button endTimeBtn;
    private int hour, min;
    private Spinner dayOfWeekSpinner, dayOfWeekSpinner2;
    private ArrayAdapter<String> spinnerAdapter;

    private Course course;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course_instructor);

        Intent intent = getIntent();
        course = intent.getParcelableExtra("course");

        Log.d("courseReceived", course.getName());

        courseName = findViewById(R.id.courseName_inst);
        courseId = findViewById(R.id.courseId_inst);
        courseDesc = findViewById(R.id.courseDescription_inst);
        courseCapacity = findViewById(R.id.courseCapacity_inst);
        startTimeBtn = findViewById(R.id.startTime);
        endTimeBtn = findViewById(R.id.endTime);
        dayOfWeekSpinner = findViewById(R.id.spinnerDayOfWeek);
        dayOfWeekSpinner2 = findViewById(R.id.spinnerDayOfWeek2);

        spinnerAdapter = new ArrayAdapter<>(EditCourseInstructor.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.dayOfWeek));
        // use android default spinner dropdown
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayOfWeekSpinner.setAdapter(spinnerAdapter);
        dayOfWeekSpinner2.setAdapter(spinnerAdapter);

        db = FirebaseFirestore.getInstance();

        // find view in layout
        newDesc = findViewById(R.id.new_course_desc_inst);
        newCap = findViewById(R.id.new_course_capacity_inst);

        setCourseData();

        // buttons
        applyChangeBtn = findViewById(R.id.applyChangeInstBtn);
        returnHomeBtn = findViewById(R.id.returnHomeInstBtn);

        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePicker(startTimeBtn);
            }
        });

        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePicker(endTimeBtn);
            }
        });

        applyChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateData(
                        newDesc.getText().toString(),
                        newCap.getText().toString());
                newDesc.setText("");
                newCap.setText("");
            }
        });

        returnHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToHomeActivity();
            }
        });
    }

    private void popTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectHour, int selectMin) {
                hour = selectHour;
                min = selectMin;
                Button timeBtn = (Button) view;
                timeBtn.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, min));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, min, true);
        timePickerDialog.setTitle("Start Time");
        timePickerDialog.show();
    }

    private void setCourseData() {
        // set the edit text default text with the course details
        courseName.setText(course.getName());
        courseId.setText("Course id: " + course.getCourseId());
        courseDesc.setText("Description: " + course.getCourseDescription());
        courseCapacity.setText("Course capacity: " + course.getStudentCapacity());

        newDesc.setText(course.getCourseDescription());
        newCap.setText(course.getStudentCapacity());
    }

    private void UpdateData(String desc, String cap) {

        Map<String,Object> courseDetails = new HashMap<>();
        courseDetails.put(CourseField.name.toString(), courseName);
        courseDetails.put(CourseField.courseId.toString(), courseId);
        courseDetails.put(CourseField.courseDescription.toString(), desc);
        courseDetails.put(CourseField.studentCapacity.toString(), cap);

        db.collection("courses").whereEqualTo("name", course.getName())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentID = documentSnapshot.getId();
                            db.collection("courses")
                                    .document(documentID)
                                    .update(courseDetails)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(EditCourseInstructor.this, "successfully updated data", Toast.LENGTH_SHORT).show();
                                            course.setCourseDescription(desc);
                                            course.setStudentCapacity(cap);
                                            setCourseData();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(EditCourseInstructor.this, "error updated data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
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

    public void deleteCourse() {
        db.collection("courses").whereEqualTo("name", course.getName()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    db.collection("courses").document(documentSnapshot.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EditCourseInstructor.this, "Successfully deleted!", Toast.LENGTH_SHORT).show();
                            goBackToHomeActivity();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditCourseInstructor.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(EditCourseInstructor.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}