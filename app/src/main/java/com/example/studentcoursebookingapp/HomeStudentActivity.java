package com.example.studentcoursebookingapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class HomeStudentActivity extends AppCompatActivity {

    private TextView usr_name_view;
    private TextView usr_role_view;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser mUser;

    private Button viewCourseBtn, signOutBtn;
    private Button openCoursePageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_student);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        usr_name_view = findViewById(R.id.blanc1);
        usr_role_view = findViewById(R.id.roleStudent);

        viewCourseBtn = findViewById(R.id.viewCourseBtn_Student);
        viewCourseBtn.setOnClickListener(viewCourse);

        signOutBtn = findViewById(R.id.sign_out_btn_student);
        signOutBtn.setOnClickListener(signOut);

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
    //updates name and role for current user
    private void setUserNameRole(){
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Access user data.
        // Firestore's rules are set up to ensure that users can only accsess the document where the document title equals their UID.
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

    private View.OnClickListener viewCourse = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeStudentActivity.this, SelectCourseStudentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };

    private View.OnClickListener signOut = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mAuth.signOut();
            Intent intent = new Intent(HomeStudentActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };

}