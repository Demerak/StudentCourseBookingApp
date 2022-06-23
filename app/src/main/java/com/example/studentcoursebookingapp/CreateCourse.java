package com.example.studentcoursebookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateCourse extends AppCompatActivity implements View.OnClickListener {

    private EditText editCourseName;
    private EditText editCourseNumber;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button returnHomeActivityBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        editCourseName = findViewById(R.id.course_name_field);
        editCourseNumber = findViewById(R.id.course_number_field);
        findViewById(R.id.submit_entry_btn).setOnClickListener(this);
        returnHomeActivityBtn = findViewById(R.id.go_back_btn_create_course);

        returnHomeActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go back Course Activity
                Intent intent = new Intent(CreateCourse.this, CoursesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        String name = editCourseName.getText().toString().trim();
        String number = editCourseNumber.getText().toString().trim();

        CollectionReference dbCourse = db.collection("courses");
        Course course = new Course (name,number);

        dbCourse.add(course).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(CreateCourse.this, "Course Created!", Toast.LENGTH_LONG).show();
            }
        }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateCourse.this, e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}