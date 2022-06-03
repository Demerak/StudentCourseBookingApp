package com.example.studentcoursebookingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView email = (TextView) findViewById(R.id.email);
        TextView pwd = (TextView) findViewById(R.id.pwd);
        Button signInBtn = (Button) findViewById(R.id.signInBtn);
        Button singUpBtn = (Button) findViewById(R.id.signUpBtn);

        email.setOnClickListener(editTextOnClickListener);
        pwd.setOnClickListener(editTextOnClickListener);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().equals("admin") && pwd.getText().toString().equals("admin123")) {
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
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
}