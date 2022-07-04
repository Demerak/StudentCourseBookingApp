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
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

public class SelectCourseStudentActivity extends AppCompatActivity {

    private Button homeBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<Course> courseListNotEnroll;
    private ArrayList<Course> courseListEnroll;
    private RecyclerView recyclerViewEnroll;
    private RecyclerView recyclerViewUnEnroll;
    private CourseAdapterEnroll courseAdapterEnroll;
    private CourseAdapterUnEnroll courseAdapterUnEnroll;
    private List<String> courseEnrollList;

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
        mAuth = FirebaseAuth.getInstance();
        courseListNotEnroll = new ArrayList<Course>();
        courseListEnroll = new ArrayList<Course>();

        courseAdapterEnroll = new CourseAdapterEnroll(SelectCourseStudentActivity.this, courseListNotEnroll);
        courseAdapterUnEnroll = new CourseAdapterUnEnroll(SelectCourseStudentActivity.this, courseListEnroll);

        recyclerViewEnroll.setAdapter(courseAdapterEnroll);
        recyclerViewUnEnroll.setAdapter(courseAdapterUnEnroll);

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

        DocumentReference userDoc = db.collection("users").document(mAuth.getCurrentUser().getUid());

        List<String> result;

        // .get("courseEnroll").isSuccessful()
        Log.d("TESTHERE", (String) userDoc.toString());

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
                                                courseListEnroll.add(document.toObject(Course.class));
                                            } else {
                                                courseListNotEnroll.add(document.toObject(Course.class));
                                            }
                                        }
                                        else {
                                            courseListNotEnroll.add(document.toObject(Course.class));
                                        }
                                    }
                                    courseAdapterEnroll.notifyDataSetChanged();
                                    courseAdapterUnEnroll.notifyDataSetChanged();
                                }
                            }
                        });


                    }
                });
            }
        });






        //Log.d("TESTHERE", courseEnrollList.toArray().toString());



//        result = (List<String>) userDoc.get(Source.valueOf(UserField.courseEnroll.toString())).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                Log.d("TESTHERE", "Success" );
//            }
//        });



    }

    public void openHomePageActivity () {
        Intent intentHomeActivity = new Intent(this, HomeStudentActivity.class);
        startActivity(intentHomeActivity);
    }
}