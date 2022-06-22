package com.example.studentcoursebookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class CoursesActivity extends AppCompatActivity {


    private Button openHomePageButton, openCreateCourseButton,editCourseButton;;
    FirebaseFirestore db;
    ArrayList<Course> courseList;
    RecyclerView recyclerView;
    CourseAdapter courseAdapter;




    public void openHomePageActivity () {
        Intent intentHomeActivity = new Intent(this, HomeAdminActivity.class);
        startActivity(intentHomeActivity);
    }

    public void openCreateCoursePageActivity () {
        Intent intentCreateActivity = new Intent(this, CreateCourse.class);
        startActivity(intentCreateActivity);
    }

    private void openEditActivity() {
        Intent editCourseIntent = new Intent(this, EditCourse.class);
        startActivity(editCourseIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        recyclerView = findViewById(R.id.recyclerViewer);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));

        db = db.getInstance();
        courseList = new ArrayList<Course>();
        courseAdapter = new CourseAdapter(CoursesActivity.this, courseList);

        recyclerView.setAdapter(courseAdapter);
        EventChangeListener();



        openHomePageButton = findViewById(R.id.home_from_courses_page_btn);
        openHomePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHomePageActivity();
            }
        });

        openCreateCourseButton = findViewById(R.id.create_course_btn);
        openCreateCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateCoursePageActivity();
            }
        });

        editCourseButton = findViewById(R.id.edit_courses_btn);
        editCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditActivity();
            }
        });

    }

    private void EventChangeListener() {
        db.collection("courses").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for (DocumentChange dc: value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        courseList.add(dc.getDocument().toObject(Course.class));
                    }
                    courseAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Populate List with data

    }





}