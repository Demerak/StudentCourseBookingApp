package com.example.studentcoursebookingapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class HomeInstructorActivity extends AppCompatActivity {

    private TextView usr_name_view;
    private TextView usr_role_view;

    private String usr_role;
    private String usr_name;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser mUser;

    private Button signOutButton;
    private Button assignedCourseButton;
    private Button availableCourseButton;

    private Button editAssignedCourseButton;
    private Button viewAllCoursesButton;


    private ScrollView scrollCourse;
    private LinearLayout layoutCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_instructor);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        usr_name_view = findViewById(R.id.blanc1);
        usr_role_view = findViewById(R.id.blanc2);

        signOutButton = findViewById(R.id.sign_out_btn);
        signOutButton.setOnClickListener(signOut);

        assignedCourseButton = findViewById(R.id.view_assigned_courses_button);
        assignedCourseButton.setOnClickListener(openAssignedCourses);

        availableCourseButton = findViewById(R.id.view_available_courses_button);
        availableCourseButton.setOnClickListener(openAvailableCourses);

        editAssignedCourseButton = findViewById(R.id.edit_assigned_courses_button);
        editAssignedCourseButton.setOnClickListener(editAssignedCourses);

        viewAllCoursesButton = findViewById(R.id.view_all_courses_button);
        viewAllCoursesButton.setOnClickListener(openAllCourses);


        scrollCourse = findViewById(R.id.instructor_scroll_view);
        layoutCourse = findViewById(R.id.scroll_view_linear_layout);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#8E001A"));

    }


    @Override
    public void onStart() {
        super.onStart();
        setUserNameRole();
    }

    /*////////////////////////////////////////////////////////////////////////////////////
    CLICK METHODS
    ////////////////////////////////////////////////////////////////////////////////////*/


    private View.OnClickListener signOut = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mAuth.signOut();
            Intent intent = new Intent(HomeInstructorActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };

    private View.OnClickListener openAssignedCourses = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearView();
            writeAssignedCourses();
        }
    };

    private View.OnClickListener openAvailableCourses = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearView();
            writeAvailableCourses();
        }
    };

    private View.OnClickListener editAssignedCourses = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearView();
            editAssignedCourses();
        }
    };

    private View.OnClickListener openAllCourses = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearView();
            writeAllCourses();
        }
    };


    /*////////////////////////////////////////////////////////////////////////////////////
    HELPER METHODS
    ////////////////////////////////////////////////////////////////////////////////////*/
    //updates name and role for current user
    private void setUserNameRole(){
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Access user data.
        // Firestore's rules are set up to ensure that users can only access the document where the document title equals their UID.
        // Thank you too : https://medium.com/firebase-tips-tricks/how-to-fix-firestore-error-permission-denied-missing-or-insufficient-permissions-777d591f404
        // For providing the firestore rules
        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            usr_role_view.setText(document.getString("role"));
                            usr_name_view.setText(document.getString("name"));
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });
    }

    // clears scroll view
    private void clearView(){
        layoutCourse.removeAllViews();
    }

    // set instructor field in course document
    private void teachCourse (String id, String name, String number, int functionFrom)  {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // loop over all course documents, if course has a instructor field, show toast error message,
        // if not, add instructor to course
        db.collection("courses")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.get("instructor") == null) {
                                // Add instructor to Course
                                Map<String, Object> course = new HashMap<>();
                                course.put(CourseField.name.toString(), name);
                                course.put(CourseField.courseId.toString(), number);
                                course.put("instructor", currentUser.getUid());

                                db.collection("courses").document(id)
                                        .set(course)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("TAG", "DocumentSnapshot successfully written!");
                                                Toast.makeText(HomeInstructorActivity.this, "Course " + name +" Assigned to You",
                                                        Toast.LENGTH_SHORT).show();
                                                if (functionFrom == 0){
                                                    writeAvailableCourses();
                                                } else if (functionFrom ==1){
                                                    writeAllCourses();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("TAG", "Error writing document", e);
                                            }
                                        });

                            } else {
                                Log.w("TAG", "tried to assign self to course with instructor");
                                Toast.makeText(HomeInstructorActivity.this, "Course Already has Instructor!.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });

    }

    // edit document fields, adding fields that we want to preserve
    private void unAssignFromThisCourse(String id, String name, String number){
        // get course document matching given id, put fields that we want to preserve
        Map<String, Object> course = new HashMap<>();
        course.put(CourseField.name.toString(), name);
        course.put(CourseField.courseId.toString(), number);
        db.collection("courses").document(id)
                .set(course)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                        Toast.makeText(HomeInstructorActivity.this, "Course No Longer Assigned",
                                Toast.LENGTH_SHORT).show();
                        editAssignedCourses(); // rewrite edit assign courses
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                    }
                });
    }

    // when course pressed in edit assigned courses, this method is called
    private void editThisCourse (String id, String name, String number)  {
        clearView();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Welcome String
        TextView ttv = new TextView(HomeInstructorActivity.this);
        String welcomeString = "Course: " + name + " id: " + number;
        ttv.setText(welcomeString);
        ttv.setTextSize(30);
        layoutCourse.addView(ttv);

        // Button to unassigned current user from course
        Button editBtn = new Button(HomeInstructorActivity.this);
        String btnString = "Edit Course";
        editBtn.setText(btnString);

        Button deleteBtn = new Button(HomeInstructorActivity.this);
        btnString = "unAssign course";
        deleteBtn.setText(btnString);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Course course = new Course(name, number);
                Intent editCourseIntent = new Intent(HomeInstructorActivity.this, EditCourseInstructor.class);
                Log.d("ClickWork", course.getName());
                editCourseIntent.putExtra("course", course);
                startActivity(editCourseIntent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                unAssignFromThisCourse(id,name,number);
            }
        });

        layoutCourse.addView(editBtn);
        layoutCourse.addView(deleteBtn);


    }

    /*////////////////////////////////////////////////////////////////////////////////////
    COURSE METHODS
    ////////////////////////////////////////////////////////////////////////////////////*/

    // called from write all courses, clear and update view with course matching search query
    private void searchCourses(String searchquery){
        clearView();

        // Welcome String
        TextView ttv = new TextView(HomeInstructorActivity.this);
        String welcomeString = "Search Result:";
        ttv.setText(welcomeString);
        ttv.setTextSize(12);
        layoutCourse.addView(ttv);

        // get all course documents and display courses that match search query
        db.collection("courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        if (document.get("instructor") == null) {
                            String courseName = document.getString(CourseField.name.toString());
                            String courseNum = document.getString(CourseField.courseId.toString());
                            String docString = courseName + " -- " + courseNum;


                            Button btn = new Button(HomeInstructorActivity.this);
                            String btnString = docString;
                            btn.setText(btnString);


                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view){
                                    teachCourse(document.getId(),courseName,courseNum,1);
                                }
                            });

                            btn.setTextSize(12);
                            if (Objects.requireNonNull(document.getString(CourseField.name.toString())).equalsIgnoreCase(searchquery) ||
                                Objects.requireNonNull(document.getString(CourseField.courseId.toString())).equalsIgnoreCase(searchquery)){

                                layoutCourse.addView(btn);
                            }
                        } else {
                            String courseName = document.getString(CourseField.name.toString());
                            String courseNum = document.getString(CourseField.courseId.toString());

                            db.collection("users")
                                    .document(document.getString("instructor"))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                String docString = courseName + " -- " + courseNum + " instructor: " + document.getString("name");

                                                Button btn = new Button(HomeInstructorActivity.this);
                                                String btnString = docString;
                                                btn.setText(btnString);

                                                btn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view){
                                                        teachCourse(document.getId(),courseName,courseNum,1);
                                                    }
                                                });

                                                btn.setTextSize(12);

                                                if (Objects.requireNonNull(courseName).equalsIgnoreCase(searchquery) ||
                                                        Objects.requireNonNull(courseNum).equalsIgnoreCase(searchquery) ||
                                                        Objects.requireNonNull(document.getString("name")).equalsIgnoreCase(searchquery)
                                                ){
                                                    layoutCourse.addView(btn);
                                                }

                                            } else {
                                                Log.d("TAG", "get failed with ", task.getException());
                                            }
                                        }
                                    });

                        }

                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });

        // Welcome String
        Button btn = new Button(HomeInstructorActivity.this);
        String btnString = "See All Courses";
        btn.setText(btnString);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                writeAllCourses();
            }
        });

        btn.setTextSize(12);
        layoutCourse.addView(btn);

    }

    // write all courses, regardless of instructor state
    private void writeAllCourses() {
        clearView();

        // Welcome String
        TextView ttv = new TextView(HomeInstructorActivity.this);
        String welcomeString = "Choose a course below to teach. Can't assign yourself to a course? Hit the view available courses button to see courses without instructors";
        ttv.setText(welcomeString);
        ttv.setTextSize(12);
        layoutCourse.addView(ttv);

        // Search Bar
        SearchView search = new SearchView(HomeInstructorActivity.this);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCourses(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("well", " this worked");
                return false;
            }
        });
        layoutCourse.addView(search);

        // Loop over all courses, and for each course and display them as buttons that will assign them to current user.
        // if the course has an instructor get the instructor name from users collection.
        // firebase addoncompletelisteners nested because each are async
        db.collection("courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        if (document.get("instructor") == null) {
                            String courseName = document.getString(CourseField.name.toString());
                            String courseNum = document.getString(CourseField.courseId.toString());
                            String docString = courseName + " -- " + courseNum;


                            Button btn = new Button(HomeInstructorActivity.this);
                            String btnString = docString;
                            btn.setText(btnString);


                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view){
                                    teachCourse(document.getId(),courseName,courseNum,1);
                                }
                            });

                            btn.setTextSize(12);
                            layoutCourse.addView(btn);



                        } else {
                            String courseName = document.getString(CourseField.name.toString());
                            String courseNum = document.getString(CourseField.courseId.toString());

                            db.collection("users")
                                    .document(document.getString("instructor"))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                String docString = courseName + " -- " + courseNum + " instructor: " + document.getString("name");




                                                Button btn = new Button(HomeInstructorActivity.this);
                                                String btnString = docString;
                                                btn.setText(btnString);


                                                btn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view){
                                                        teachCourse(document.getId(),courseName,courseNum,1);
                                                    }
                                                });

                                                btn.setTextSize(12);
                                                layoutCourse.addView(btn);



                                            } else {
                                                Log.d("TAG", "get failed with ", task.getException());
                                            }
                                        }
                                    });

                        }

                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    // write courses without an instructor
    private void writeAvailableCourses(){
        clearView();

        // Welcome String
        TextView ttv = new TextView(HomeInstructorActivity.this);
        String welcomeString = "Choose a course below to teach. Don't see a course? Hit the see all courses button";
        ttv.setText(welcomeString);
        ttv.setTextSize(12);
        layoutCourse.addView(ttv);

        // get all documents in course collection and for all courses that have no instructor field
        db.collection("courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.get("instructor") == null) {

                            String courseName = document.getString(CourseField.name.toString());
                            String courseNum = document.getString(CourseField.courseId.toString());
                            String docString = courseName + " -- " + courseNum;

                            Button btn = new Button(HomeInstructorActivity.this);
                            String btnString = docString;
                            btn.setText(btnString);

                            // create buttons that assign current user to course document instructor field
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view){
                                    teachCourse(document.getId(),courseName,courseNum,0);
                                }
                            });

                            btn.setTextSize(12);
                            layoutCourse.addView(btn);
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });

    }

    // write all courses that are assigned to current instructor
    private void writeAssignedCourses(){
        // Welcome String
        TextView ttv = new TextView(HomeInstructorActivity.this);
        String welcomeString = "Currently Assigned Courses:";
        ttv.setText(welcomeString);
        ttv.setTextSize(30);
        layoutCourse.addView(ttv);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Get all documents in course collection and display those that have an instructor field that match current user UID
        db.collection("courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //list.add(document.getId());
                        if (Objects.equals(document.getString("instructor"), currentUser.getUid())) {
                            String textName = document.getString(CourseField.name.toString());
                            String textNum = document.getString(CourseField.courseId.toString());
                            String docString = textName + " -- " + textNum;
                            TextView tv = new TextView(HomeInstructorActivity.this);
                            tv.setText(docString);
                            ttv.setTextSize(30);
                            layoutCourse.addView(tv);
                        }
                    }
                    Log.d("TAG", list.toString());
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });

    }

    // write all courses that are assigned to current instructor, with buttons for each course that clear the view and give more options to edit the selected course
    private void editAssignedCourses(){
        clearView();

        // Welcome String
        TextView ttv = new TextView(HomeInstructorActivity.this);
        String welcomeString = "Edit Assigned Courses, Click to Select Course";
        ttv.setText(welcomeString);
        ttv.setTextSize(30);
        layoutCourse.addView(ttv);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // get all courses, and filter for courses with an instructor field that matches the current user
        // for all connected courses, create buttons that select that course and allow for more editing
        db.collection("courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //list.add(document.getId());
                        if (Objects.equals(document.getString("instructor"), currentUser.getUid())) {
                            String courseName = document.getString(CourseField.name.toString());
                            String courseNum = document.getString(CourseField.courseId.toString());
                            String docString = courseName + " -- " + courseNum;

                            Button btn = new Button(HomeInstructorActivity.this);
                            String btnString = docString;
                            btn.setText(btnString);

                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view){
                                    editThisCourse(document.getId(),courseName,courseNum);
                                }
                            });

                            layoutCourse.addView(btn);

                        }
                    }
                    Log.d("TAG", list.toString());
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}