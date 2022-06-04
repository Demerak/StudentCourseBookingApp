package com.example.studentcoursebookingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    private String userType;
    private Button prevButtonSelected;

    private TextView emailOrUsername;
    private TextView pwd;
    private TextView pwdConf;
    private Button signUpBtn;
    private Button studentBtn;
    private Button instructorBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailOrUsername = (TextView) findViewById(R.id.email);
        pwd = (TextView) findViewById(R.id.pwd);
        pwdConf = (TextView) findViewById(R.id.pwdconf);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        studentBtn = (Button) findViewById(R.id.student);
        instructorBtn = (Button) findViewById(R.id.instructor);

        emailOrUsername.setOnClickListener(editTextOnClickListener);
        pwd.setOnClickListener(editTextPwdOnClickListener);
        pwdConf.setOnClickListener(editTextPwdOnClickListener);
        studentBtn.setOnClickListener(userTypeSelection);
        instructorBtn.setOnClickListener(userTypeSelection);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(emailOrUsername.getText().toString().equals("admin") && pwd.getText().toString().equals("admin123")) {
                    Toast.makeText(SignUpActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                }
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
        Toast.makeText(SignUpActivity.this, userType, Toast.LENGTH_LONG).show();
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
}