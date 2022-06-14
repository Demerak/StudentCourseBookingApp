package com.example.studentcoursebookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class CreateCourse extends AppCompatActivity implements View.OnClickListener {

    private EditText editCourseName;
    private EditText editCourseNumber;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button deleteBtn;





    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        editCourseName = findViewById(R.id.course_name_field);
        editCourseNumber = findViewById(R.id.course_number_field);
        findViewById(R.id.submit_entry_btn).setOnClickListener(this);
        deleteBtn = findViewById(R.id.delete_course_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String coursename = editCourseName.getText().toString();
                editCourseName.setText("");
                String coursecode = editCourseNumber.getText().toString();
                editCourseNumber.setText("");
                DeleteData(coursename);
            }
        });

    }

    private void DeleteData(String coursename) {
        db.collection("courses").whereEqualTo("name", coursename).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    String documentID = documentSnapshot.getId();
                    db.collection("courses").document(documentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(CreateCourse.this, "Successfully deleted!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateCourse.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(CreateCourse.this, "Failed", Toast.LENGTH_SHORT).show();
                }
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