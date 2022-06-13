package com.example.studentcoursebookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {
    private String userType;

    private EditText userNameTextField;
    private EditText pwdTextField;
    private EditText pwdConfTextField;

    private Button signUpBtn;
    private Button signInBtn;
    private Button studentBtn;
    private Button instructorBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String defaultUserName;
    private String defaultPassword;
    private String defaultPasswordConf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        defaultUserName = getResources().getString(R.string.userName);
        defaultPassword = getResources().getString(R.string.pwd);
        defaultPasswordConf = getResources().getString(R.string.confirmPwd);

        userNameTextField = findViewById(R.id.email);
        pwdTextField = findViewById(R.id.pwd);
        pwdConfTextField = findViewById(R.id.pwdconf);

        signUpBtn = findViewById(R.id.signUpBtn);
        signInBtn = findViewById(R.id.signInBtn);
        studentBtn = findViewById(R.id.student);
        instructorBtn = findViewById(R.id.instructor);

        userNameTextField.setOnEditorActionListener(userNameListener);
        userNameTextField.setOnFocusChangeListener(onFocusListener);

        pwdTextField.setOnEditorActionListener(passwordListener);
        pwdTextField.setOnFocusChangeListener(onFocusListener);

        pwdConfTextField.setOnEditorActionListener(passwordConfListener);
        pwdConfTextField.setOnFocusChangeListener(onFocusListener);

        studentBtn.setOnClickListener(userTypeSelection);
        instructorBtn.setOnClickListener(userTypeSelection);

        signUpBtn.setOnClickListener(signUp);
        signInBtn.setOnClickListener(signIn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }



    /*////////////////////////////////////////////////////////////////////////////////////
    TEXT INPUT METHODS
    ////////////////////////////////////////////////////////////////////////////////////*/

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

                if (pwdConfTextField.getText().toString().equals("")){
                    pwdConfTextField.setText(defaultPasswordConf);
                    pwdConfTextField.setInputType(InputType.TYPE_CLASS_TEXT);
                } else if (pwdConfTextField.getText().toString().equals(defaultPasswordConf)){
                    pwdConfTextField.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    pwdConfTextField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            } else {
                clearEditText(v);
            }

        }
    };

    private void clearEditText(View v) {
        TextView entry = (TextView) v;
        entry.setText("");
        if (v.getId() == pwdTextField.getId()) {
            pwdTextField.setText("");
            pwdTextField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else if (v.getId() == pwdConfTextField.getId()) {
            pwdConfTextField.setText("");
            pwdConfTextField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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


    TextView.OnEditorActionListener passwordListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                pwdConfTextField.requestFocus();
            }
            return false;
        }
    };


    TextView.OnEditorActionListener passwordConfListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                signUpBtn.requestFocus();
                //signUpMethod();
            }
            return false;
        }
    };

    //---------------------------------//
    //---------------------------------//


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

    }


    /*////////////////////////////////////////////////////////////////////////////////////
    SIGN IN METHODS
    ////////////////////////////////////////////////////////////////////////////////////*/

    //Sends user to page after login screen
    private void sendUserToHome() {
        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private View.OnClickListener signIn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendUserToSignInActivity();
        }
    };

    private void sendUserToSignInActivity() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //---------------------------------//
    //---------------------------------//


    private View.OnClickListener signUp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            signUpMethod();
        }
    };

    private void signUpMethod() {
        String userName = userNameTextField.getText().toString();
        String password = pwdTextField.getText().toString();



        if (userName.equals(getResources().getString(R.string.userName)) || password.equals(getResources().getString(R.string.pwd)) || userName.equals("") || password.equals("")) {
            Toast.makeText(SignUpActivity.this, "Enter your Username and Password", Toast.LENGTH_LONG).show();
        } else if (!password.equals(pwdConfTextField.getText().toString())) {
            Toast.makeText(SignUpActivity.this, "Passwords don't match", Toast.LENGTH_LONG).show();
        } else if (password.length() < 6) {
            Toast.makeText(SignUpActivity.this, "Passwords too short", Toast.LENGTH_LONG).show();
        } else if (userType == null) {
            Toast.makeText(SignUpActivity.this, "Select Role", Toast.LENGTH_LONG).show();
        } else {
            // If you're getting an authentication error when creating user try a longer password, the password has to meet google's complexity requirements
            mAuth.createUserWithEmailAndPassword(userName + "@uottawa.ca", password)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "createUserWithEmail:success");
                                FirebaseUser currentUser = mAuth.getCurrentUser();

                                //Create entry in firestore with document name as UID and add info name and role:

                                Map<String, Object> userDataToAdd = new HashMap<>();
                                userDataToAdd.put("role", userType);
                                userDataToAdd.put("name", userName);

                                // Access user data.
                                // Firestore's rules are set up to ensure that users can only accsess the document where the document title equals their UID.
                                // Thank you too : https://medium.com/firebase-tips-tricks/how-to-fix-firestore-error-permission-denied-missing-or-insufficient-permissions-777d591f404
                                // For providing the firestore rules
                                db.collection("users")
                                        .document(currentUser.getUid())
                                        .set(userDataToAdd)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("TAG", "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("TAG", "Error writing document", e);
                                            }
                                        });

                                // send to home
                                sendUserToHome();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }

    }

}


