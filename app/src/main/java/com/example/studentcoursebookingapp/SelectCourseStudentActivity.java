package com.example.studentcoursebookingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SelectCourseStudentActivity extends AppCompatActivity {

    private Button homeBtn;
    private FirebaseFirestore db;
    private ArrayList<Course> courseListNotEnroll;
    private ArrayList<Course> courseListEnroll;
    private RecyclerView recyclerViewEnroll;
    private RecyclerView recyclerViewUnEnroll;
    private CourseAdapterEnroll courseAdapterEnroll;
    private CourseAdapterUnEnroll courseAdapterUnEnroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course_student);

        recyclerViewEnroll = findViewById(R.id.recyclerViewerStudSelectEnroll);
        recyclerViewEnroll.setHasFixedSize(true);
        recyclerViewEnroll.setLayoutManager(new LinearLayoutManager((this)));

        recyclerViewUnEnroll = findViewById(R.id.recyclerViewerStudSelectUnEnroll);
        recyclerViewUnEnroll.setHasFixedSize(true);
        recyclerViewUnEnroll.setLayoutManager(new LinearLayoutManager((this)));

        db = db.getInstance();
        courseListNotEnroll = new ArrayList<Course>();
        courseListEnroll = new ArrayList<Course>();

        courseAdapterEnroll = new CourseAdapterEnroll(SelectCourseStudentActivity.this, courseListNotEnroll);
        courseAdapterUnEnroll = new CourseAdapterUnEnroll(SelectCourseStudentActivity.this, courseListEnroll);

        recyclerViewEnroll.setAdapter(courseAdapterEnroll);

        homeBtn = findViewById(R.id.homeBtnStudSelect);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openHomePageActivity(); }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO change to look at all the course that the student isn't enroll in
        // TODO query the course that the student is enroll in and add to recyclerViewUnEnroll

        db.collection("courses").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    for (DocumentSnapshot document: value.getDocuments()) {
                        Log.d("SUCCESS", document.getId() + " => " + document.getData() + document.toObject(Course.class).getName());
                        courseListNotEnroll.add(document.toObject(Course.class));
                    }
                    courseAdapterEnroll.notifyDataSetChanged();
                }
            }
        });
    }

    public void openHomePageActivity () {
        Intent intentHomeActivity = new Intent(this, HomeStudentActivity.class);
        startActivity(intentHomeActivity);
    }
}