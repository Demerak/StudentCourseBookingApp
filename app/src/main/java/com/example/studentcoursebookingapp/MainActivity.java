package com.example.studentcoursebookingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {

    private EditText userNameTextField;
    private EditText pwdTextField;
    private Button signInBtn;
    private Button signUpBtn;

    private String defaultUserName;
    private String defaultPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
        defaultUserName = getResources().getString(R.string.userName);
        defaultPassword = getResources().getString(R.string.pwd);

        userNameTextField = findViewById(R.id.email);
        pwdTextField = findViewById(R.id.pwd);
        signInBtn = findViewById(R.id.signInBtn);
        signUpBtn = findViewById(R.id.signUpBtn);

        userNameTextField.setOnEditorActionListener(userNameListener);
        userNameTextField.setOnFocusChangeListener(onFocusListener);

        pwdTextField.setOnEditorActionListener(passwordListener);
        pwdTextField.setOnFocusChangeListener(onFocusListener);

        signInBtn.setOnClickListener(signIn);
        signInBtn.setOnFocusChangeListener(signInFocusListener);

        signUpBtn.setOnClickListener(signUp);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and if so send to home.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            sendUserToHome();
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////////
    INPUT METHODS
    ////////////////////////////////////////////////////////////////////////////////////*/
    private View.OnClickListener signUp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendUserToSignUpActivity();
        }
    };
    private void sendUserToSignUpActivity() {
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    //---------------------------------//
    //---------------------------------//
    private View.OnFocusChangeListener onFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v,boolean hasFocus) {
            if (!hasFocus){
                if (userNameTextField.getText().toString().equals("")){
                    userNameTextField.setText(defaultUserName);
                }
                if (pwdTextField.getText().toString().equals("")){
                    pwdTextField.setText(defaultPassword);
                    pwdTextField.setInputType(InputType.TYPE_CLASS_TEXT);
                } else if (pwdTextField.getText().toString().equals(defaultPassword)) {
                    pwdTextField.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    pwdTextField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            } else {
                clearEditText(v);
            }
        }
    };

    private void clearEditText(View v) {
        if (v.getId() == pwdTextField.getId()) {
            pwdTextField.setText("");
            pwdTextField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            TextView entry = (TextView) v;
            entry.setText("");
        }
    }

    //---------------------------------//
    //---------------------------------//

    TextView.OnEditorActionListener userNameListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                pwdTextField.requestFocus();
            }
            return false;
        }
    };

    // ensures that the enter button in the keyboard signs in the user
    TextView.OnEditorActionListener passwordListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                signInBtn.requestFocus();
            }
        return false;
        }
    };


    /*////////////////////////////////////////////////////////////////////////////////////
    SIGN IN METHODS
    ////////////////////////////////////////////////////////////////////////////////////*/
    //Sends user to page after login screen
    private void sendUserToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private View.OnClickListener signIn = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            signInMethod();
        }

    };
    private View.OnFocusChangeListener signInFocusListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View v,boolean hasFocus) {
            signInMethod();
        }
    };

    private void signInMethod() {
        String userName = userNameTextField.getText().toString();
        String password = pwdTextField.getText().toString();

        if (userName.equals(defaultUserName) || password.equals(defaultPassword) || userName.equals("") || password.equals("")) {
            Toast.makeText(MainActivity.this, "Enter your Username or Email and Password",
                    Toast.LENGTH_LONG).show();
        } else {
            // Auth user and sign in
            // adding @uottawa.ca string to username so sign in can only user username without email
            mAuth.signInWithEmailAndPassword(userName + "@uottawa.ca", password)
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, send user to home
                                Log.d("TAG", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                sendUserToHome();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}