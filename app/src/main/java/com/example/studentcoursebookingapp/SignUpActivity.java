package com.example.studentcoursebookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private String userType;
    private Button prevButtonSelected;

    private TextView emailOrUsername;
    private TextView pwd;
    private TextView pwdConf;
    private Button signUpBtn;
    private Button signInBtn;
    private Button studentBtn;
    private Button instructorBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailOrUsername = (TextView) findViewById(R.id.email);
        pwd = (TextView) findViewById(R.id.pwd);
        pwdConf = (TextView) findViewById(R.id.pwdconf);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        signInBtn = (Button) findViewById(R.id.signInBtn);
        studentBtn = (Button) findViewById(R.id.student);
        instructorBtn = (Button) findViewById(R.id.instructor);

        emailOrUsername.setOnClickListener(editTextOnClickListener);
        pwd.setOnClickListener(editTextPwdOnClickListener);
        pwdConf.setOnClickListener(editTextPwdOnClickListener);
        studentBtn.setOnClickListener(userTypeSelection);
        instructorBtn.setOnClickListener(userTypeSelection);

        mAuth = FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailOrUsername.getText().toString().equals(getResources().getString(R.string.userName))
                        || pwd.getText().toString().equals(getResources().getString(R.string.pwd))) {
                    emailOrUsername.setTextColor(getResources().getColor(R.color.garnet));
                    pwd.setTextColor(getResources().getColor(R.color.garnet));
                    Toast.makeText(SignUpActivity.this, "Enter your Username or Email and Password", Toast.LENGTH_LONG).show();
                }
                validAuth();

                // todo remove this bellow latter
                if(emailOrUsername.getText().toString().equals("admin") && pwd.getText().toString().equals("admin123")) {
                    Toast.makeText(SignUpActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                }
            }
        });


        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToSignInActivity();
            }
        });
    }

    private View.OnClickListener userTypeSelection = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userTypeSelection(v);
        }
    };

    private void userTypeSelection(View v) {
        Button userTypeSel = (Button) v;
        userType = userTypeSel.getText().toString();
        if (userTypeSel.getId() == studentBtn.getId()) {
            studentBtn.setBackground(getResources().getDrawable(R.drawable.left_rounded_button_red));
            instructorBtn.setBackground(getResources().getDrawable(R.drawable.right_rounded_button));
        } else if (userTypeSel.getId() == instructorBtn.getId()) {
            instructorBtn.setBackground(getResources().getDrawable(R.drawable.right_rounded_button_red));
            studentBtn.setBackground(getResources().getDrawable(R.drawable.left_rounded_button));
        }
        Toast.makeText(SignUpActivity.this, userType, Toast.LENGTH_LONG).show(); // todo remove
    }

    private View.OnClickListener editTextOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearEditText(v);
        }
    };

    private void clearEditText(View v) {
        TextView entry = (TextView) v;
        entry.setText("");
    }

    private View.OnClickListener editTextPwdOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearPwdEditText(v);
        }
    };

    private void clearPwdEditText(View v) {
        TextView entry = (TextView) v;
        entry.setText("");
        entry.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    private void validAuth() {
        // todo
        String inputEmailOrUsername = emailOrUsername.getText().toString();
        String inputPwd = pwd.getText().toString();
        String inputConfPwd = pwdConf.getText().toString();

        if (!inputPwd.equals(inputConfPwd)) {
            // todo
        } else{
            // Firebase doesn't support username & password login, need to explore https://firebase.google.com/docs/auth/android/custom-auth
            mAuth.createUserWithEmailAndPassword(inputEmailOrUsername, inputPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // todo display that successful
                        Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        // todo display that unsuccessful
                        Toast.makeText(SignUpActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToSignInActivity() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}