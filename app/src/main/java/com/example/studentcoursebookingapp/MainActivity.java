package com.example.studentcoursebookingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String userType;
    private TextView emailOrUsername;
    private TextView pwd;
    private Button signInBtn;
    private Button signUpBtn;
    private Button studentBtn;
    private Button instructorBtn;
    private Button adminBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailOrUsername = (TextView) findViewById(R.id.email);
        pwd = (TextView) findViewById(R.id.pwd);
        signInBtn = (Button) findViewById(R.id.signInBtn);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);

        studentBtn = (Button) findViewById(R.id.student);
        instructorBtn = (Button) findViewById(R.id.instructor);
        adminBtn = (Button) findViewById(R.id.admin);

        emailOrUsername.setOnClickListener(editTextOnClickListener);
        pwd.setOnClickListener(editTextOnClickListener);

        studentBtn.setOnClickListener(userTypeSelection);
        instructorBtn.setOnClickListener(userTypeSelection);
        adminBtn.setOnClickListener(userTypeSelection);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailOrUsername.getText().toString().equals(getResources().getString(R.string.userName))
                        || pwd.getText().toString().equals(getResources().getString(R.string.pwd))) {
                    emailOrUsername.setTextColor(getResources().getColor(R.color.garnet));
                    pwd.setTextColor(getResources().getColor(R.color.garnet));
                    Toast.makeText(MainActivity.this, "Enter your Username or Email and Password", Toast.LENGTH_LONG).show();
                }
                validAuth();

                // todo remove this bellow latter
                if(emailOrUsername.getText().toString().equals("admin") && pwd.getText().toString().equals("admin123")) {
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                }
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToSignUpActivity();
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
            instructorBtn.setBackground(getResources().getDrawable(R.drawable.mid_rounded_button));
            adminBtn.setBackground(getResources().getDrawable(R.drawable.right_rounded_button));
        } else if (userTypeSel.getId() == instructorBtn.getId()) {
            studentBtn.setBackground(getResources().getDrawable(R.drawable.left_rounded_button));
            instructorBtn.setBackground(getResources().getDrawable(R.drawable.mid_rounded_button_red));
            adminBtn.setBackground(getResources().getDrawable(R.drawable.right_rounded_button));
        } else if (userTypeSel.getId() == adminBtn.getId()) {
            studentBtn.setBackground(getResources().getDrawable(R.drawable.left_rounded_button));
            instructorBtn.setBackground(getResources().getDrawable(R.drawable.mid_rounded_button));
            adminBtn.setBackground(getResources().getDrawable(R.drawable.right_rounded_button_red));
        }
        Toast.makeText(MainActivity.this, userType, Toast.LENGTH_LONG).show(); // todo remove
    }

    private View.OnClickListener editTextOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearEditText(v);
        }
    };

    private void clearEditText(View v) {
        if (v.getId() == pwd.getId()) {
            pwd.setText("");
            pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            TextView entry = (TextView) v;
            entry.setText("");
        }
    }

    private void validAuth() {
        //todo

    }

    private void sendUserToSignUpActivity() {
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}