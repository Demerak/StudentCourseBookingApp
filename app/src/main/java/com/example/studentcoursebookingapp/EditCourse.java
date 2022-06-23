package com.example.studentcoursebookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class EditCourse extends AppCompatActivity {

    private Button applyChangeBtn, updateCodeBtn, returnHomeBtn;
    private TextView courseName, courseId, courseDesc, courseCapacity;
    private EditText newName, newId, newDesc, newCap;
    private Course course;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course_admin);

        Intent intent = getIntent();
        course = intent.getParcelableExtra("course");

        Log.d("courseReceived", course.getName());

        courseName = findViewById(R.id.courseName);
        courseId = findViewById(R.id.courseId);
        courseDesc = findViewById(R.id.courseDescription);
        courseCapacity = findViewById(R.id.courseCapacity);

        db = FirebaseFirestore.getInstance();

        // find view in layout
        newName = findViewById(R.id.new_course_name_field);
        newId = findViewById(R.id.new_course_code_field);
        newDesc = findViewById(R.id.new_course_desc);
        newCap = findViewById(R.id.new_course_capacity);

        setCourseData();

        // buttons
        applyChangeBtn = findViewById(R.id.applyChangeBtn);
        returnHomeBtn = findViewById(R.id.returnHomeBtn);

        applyChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateData(newName.getText().toString(),
                        newId.getText().toString(),
                        newDesc.getText().toString(),
                        newCap.getText().toString());
                newName.setText("");
                newId.setText("");
                newDesc.setText("");
                newCap.setText("");
            }
        });

        returnHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go back Course Activity
                Intent intent = new Intent(EditCourse.this, CoursesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void setCourseData() {
        // set the edit text default text with the course details
        courseName.setText(course.getName());
        courseId.setText("Course id: " + course.getCourseId());
        courseDesc.setText("Description: " + course.getCourseDescription());
        courseCapacity.setText("Course capacity: " + course.getStudentCapacity());

        newName.setText(course.getName());
        newId.setText(course.getCourseId());
        newDesc.setText(course.getCourseDescription());
        newCap.setText(course.getStudentCapacity());
    }

    private void UpdateData(String name, String id, String desc, String cap) {

        Map<String,Object> courseDetails = new HashMap<>();
        courseDetails.put("name", name);
        courseDetails.put("courseId", id);
        courseDetails.put("courseDescription", desc);
        courseDetails.put("studentCapacity", cap);

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
                            Toast.makeText(EditCourse.this, "successfully updated data", Toast.LENGTH_SHORT).show();
                            course.setName(name);
                            course.setCourseId(id);
                            course.setCourseDescription(desc);
                            course.setStudentCapacity(cap);
                            setCourseData();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditCourse.this, "error updated data", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(EditCourse.this, "Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}