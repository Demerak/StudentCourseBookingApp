package com.example.studentcoursebookingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectCourseStudentActivity extends AppCompatActivity {

    private Button homeBtn, filterBtn;
    private EditText searchText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<Course> courseListNotEnroll;
    private ArrayList<Course> courseListEnrolledIn;
    private RecyclerView recyclerViewAvailableCourses;
    private RecyclerView recyclerViewCourseEnrolledIn;
    private CourseAdapterEnroll courseAdapterAvailableCourses;
    private CourseAdapterUnEnroll courseAdapterCourseEnrolledIn;
    private List<String> courseEnrollList;
    private Spinner dayOfWeekSpinner;
    private ArrayAdapter<String> spinnerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course_student);

        recyclerViewAvailableCourses = findViewById(R.id.recyclerViewerStudAvailableCourse);
        recyclerViewAvailableCourses.setHasFixedSize(true);
        recyclerViewAvailableCourses.setLayoutManager(new LinearLayoutManager((this)));

        recyclerViewCourseEnrolledIn = findViewById(R.id.recyclerViewerStudCourseEnrolledIn);
        recyclerViewCourseEnrolledIn.setHasFixedSize(true);
        recyclerViewCourseEnrolledIn.setLayoutManager(new LinearLayoutManager((this)));

        db = db.getInstance();
        mAuth = FirebaseAuth.getInstance();
        courseListNotEnroll = new ArrayList<Course>();
        courseListEnrolledIn = new ArrayList<Course>();

        courseAdapterAvailableCourses = new CourseAdapterEnroll(SelectCourseStudentActivity.this, courseListNotEnroll);
        courseAdapterCourseEnrolledIn = new CourseAdapterUnEnroll(SelectCourseStudentActivity.this, courseListEnrolledIn);

        recyclerViewAvailableCourses.setAdapter(courseAdapterAvailableCourses);
        recyclerViewCourseEnrolledIn.setAdapter(courseAdapterCourseEnrolledIn);

        searchText = findViewById(R.id.searchFieldSelectCourseStudent);
        searchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setText("");
            }
        });

        dayOfWeekSpinner = findViewById(R.id.spinnerSelectCourseStudent);
        spinnerAdapter = new ArrayAdapter<>(SelectCourseStudentActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.dayOfWeek2));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayOfWeekSpinner.setAdapter(spinnerAdapter);

        filterBtn = findViewById(R.id.filterBtnSelectCourseStudent);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { searchCourse(); }
        });

        homeBtn = findViewById(R.id.homeBtnStudSelect);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openHomePageActivity(); }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        DocumentReference userDoc = db.collection("users").document(mAuth.getCurrentUser().getUid());

        List<String> result;

        // .get("courseEnroll").isSuccessful()
        Log.d("TESTHERE", (String) userDoc.toString());

        courseListNotEnroll.clear();
        courseListEnrolledIn.clear();

        userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // check if the UserField.courseEnroll field exist
                boolean fieldExist = documentSnapshot.contains(UserField.courseEnroll.toString());
                if (!fieldExist) { // if it doesn't exist, create the field with null value
                    userDoc.update(UserField.courseEnroll.toString(), null);
                }

                userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        boolean nullResult = documentSnapshot.get(UserField.courseEnroll.toString()) == null;
                        courseEnrollList = (List<String>) documentSnapshot.get(UserField.courseEnroll.toString());
                        Log.d("TESTHERE", String.valueOf((List<String>) documentSnapshot.get(UserField.courseEnroll.toString())));

                        db.collection("courses").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (value != null){
                                    for (DocumentSnapshot document: value.getDocuments()) {
                                        Log.d("SUCCESS", document.getId() + " => " + document.getData() + document.toObject(Course.class).getName());
                                        if (courseEnrollList != null) {

                                            if (courseEnrollList.contains(document.getId())) {
                                                courseListEnrolledIn.add(document.toObject(Course.class));
                                            } else {
                                                courseListNotEnroll.add(document.toObject(Course.class));
                                            }
                                        }
                                        else {
                                            courseListNotEnroll.add(document.toObject(Course.class));
                                        }
                                    }
                                    courseAdapterAvailableCourses.notifyDataSetChanged();
                                    courseAdapterCourseEnrolledIn.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });
            }
        });
    }


    private void searchCourse() {
        // Can currently only search by either the course name/code or by day of the week

        Log.d("courseEnroll", courseEnrollList.toString());

        String query = searchText.getText().toString().trim();
        Log.d("Text", query);

        String courseCodeRegex = "[A-Z]{3}\\s+[0-9]{4}";
        Pattern courseCodePattern = Pattern.compile(courseCodeRegex);
        Matcher courseCodeMatch = courseCodePattern.matcher(query);
        if (courseCodeMatch.matches()) {
            Log.d("CourseCodeOrName", "Course Code");
            db.collection("courses").whereEqualTo(CourseField.courseId.toString(), query).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        courseListNotEnroll.clear();
                        Log.d("CourseCodeOrName", "Course Code Found In FireStore");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!courseEnrollList.contains(document.getId())) {
                                courseListNotEnroll.add(document.toObject(Course.class));
                            }
                        }
                        courseAdapterAvailableCourses.notifyDataSetChanged();
                    }
                }
            });
        } else if (query.equals("") || query.equals("Search")) {
            Log.d("CourseCodeOrName", "Query Empty");

            String dayOfWeekShort = dayOfWeekSpinner.getSelectedItem().toString();
            if (!dayOfWeekShort.equals("All")) {
                String dayOfWeek = getFullDayOfWeek(dayOfWeekShort);
                db.collection("courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            courseListNotEnroll.clear();
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                Log.d("courseEnroll", "here" + document.getId());
                                if (!courseEnrollList.contains(document.getId())) {
                                    Course course = document.toObject(Course.class);
                                    Log.d("courseData2", course.getCourseId());
                                    Log.d("courseData2", course.getName());
                                    Log.d("courseData2", course.getCourseDay1());
                                    Log.d("courseData2", course.getCourseDay2());
                                    Log.d("courseData2", dayOfWeek);
                                    Log.d("courseData2", " ");
                                    if (course.getCourseDay1().equals(dayOfWeek) || course.getCourseDay2().equals(dayOfWeek)) {
                                        courseListNotEnroll.add(course);
                                    }
                                }
                            }
                            courseAdapterAvailableCourses.notifyDataSetChanged();
                        }
                    }
                });
            } else {
                onStart();
            }
        } else {
            Log.d("CourseCodeOrName", "Course Name");
            db.collection("courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        courseListNotEnroll.clear();
                        for (QueryDocumentSnapshot document: task.getResult()) {
                            if (!courseEnrollList.contains(document.getId())) {
                                Course course = document.toObject(Course.class);
                                if (course.getName().contains(query)) {
                                    Log.d("CourseCodeOrName", "Course Name Found In FireStore -> " + course.getName() + " vs query "  + query);
                                    courseListNotEnroll.add(course);
                                }
                            }
                        }
                        courseAdapterAvailableCourses.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    private String getFullDayOfWeek(String dayShortcut) {
        switch (dayShortcut) {
            case "Mon":
                return "Monday";
            case "Tues":
                return "Tuesday";
            case "Wed":
                return "Wednesday";
            case "Thur":
                return "Thursday";
            case "Fri":
                return "Friday";
            default:
                return "All";
        }
    }

    private void openHomePageActivity () {
        Intent intentHomeActivity = new Intent(this, HomeStudentActivity.class);
        startActivity(intentHomeActivity);
    }
}