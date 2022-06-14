package com.example.studentcoursebookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    Button updateBtn, updateCodeBtn;
    EditText existingName, newName, existingNumber, newNumber;
    FirebaseFirestore dbup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);
        dbup = FirebaseFirestore.getInstance();
        updateBtn = findViewById(R.id.submit_edits_btn);
        updateCodeBtn = findViewById(R.id.submit_coursecode_edits_btn);
        existingName = findViewById(R.id.previous_course_name_field);
        newName = findViewById((R.id.new_course_name_field));

        existingNumber = findViewById(R.id.previous_course_code);
        newNumber = findViewById((R.id.new_course_code_field));

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courseName = existingName.getText().toString();
                String newname = newName.getText().toString();
                existingName.setText("");
                newName.setText("");

                UpdateData(courseName, newname);


            }
        });

        updateCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courseNum = existingNumber.getText().toString();
                String newnum = newNumber.getText().toString();
                existingNumber.setText("");
                newNumber.setText("");

                UpdateDataCode(courseNum, newnum);
            }
        });
    }

    private void UpdateData(String previousName,String updatedname) {

        Map<String,Object> courseDetails = new HashMap<>();
        courseDetails.put("name",updatedname);

        dbup.collection("courses").whereEqualTo("name", previousName)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    String documentID = documentSnapshot.getId();
                    dbup.collection("courses")
                            .document(documentID)
                            .update(courseDetails)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EditCourse.this, "successfully updated data", Toast.LENGTH_SHORT).show();
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

    private void UpdateDataCode(String previousNumber,String updatedNumber) {
        Map<String,Object> courseDetails = new HashMap<>();
        courseDetails.put("number",updatedNumber);

        dbup.collection("courses").whereEqualTo("number", previousNumber)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentID = documentSnapshot.getId();
                            dbup.collection("courses")
                                    .document(documentID)
                                    .update(courseDetails)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(EditCourse.this, "successfully updated data", Toast.LENGTH_SHORT).show();
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