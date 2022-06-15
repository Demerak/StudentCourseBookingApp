package com.example.studentcoursebookingapp;

import androidx.appcompat.app.AppCompatActivity;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class HomeActivity extends AppCompatActivity {

    private TextView usr_name;
    private TextView usr_role;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser mUser;

    private Button signOutButton, openCoursePageButton, openAccountPageBtn;



    public void openCoursePageActivity () {
        Intent intentCourseActivity = new Intent(this, CoursesActivity.class);
        startActivity(intentCourseActivity);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        usr_name = findViewById(R.id.blanc1);
        usr_role = findViewById(R.id.blanc2);

        signOutButton = findViewById(R.id.sign_out_btn);
        signOutButton.setOnClickListener(signOut);


        openCoursePageButton = findViewById(R.id.view_courses_page_btn);
        openCoursePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(usr_role.getText().toString().equals("admin")){

                    openCoursePageActivity();

                }


            }
        });

        openAccountPageBtn = findViewById(R.id.account_page_btn);
        openAccountPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(usr_role.getText().toString().equals("admin")){

                    openAccountPage();

                }

            }
        });
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#8E001A"));

    }

    public void openAccountPage () {
        Intent intentAccount = new Intent(this, AccountTwoActivity.class);
        startActivity(intentAccount);
    }

    @Override
    public void onStart() {
        super.onStart();
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
                        usr_role.setText(document.getString("role"));
                        usr_name.setText(document.getString("name"));
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
        });

    }

    private View.OnClickListener signOut = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };

}