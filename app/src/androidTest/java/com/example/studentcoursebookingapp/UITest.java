package com.example.studentcoursebookingapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertTrue;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.SecureRandom;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UITest {

    // Android UI tests
    // Note: I use Thread.sleep() which is obviously bad practice but it's suitable for this exercise.
    //       I should use idling resources instead.

    private String role;

    @Rule
    public ActivityScenarioRule<MainActivity> activityTestRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testA_adminLogin() throws InterruptedException {
        Thread.sleep(3000); // allow the app to launch
        signOut();
        Thread.sleep(5000);

        Log.d("errorHere", "role in meth: " + role);
        if (role != null) {
            switch (role) {
                case "admin":
                    onView(withId(R.id.sign_out_btn_admin)).perform(click());
                    break;
                case "instructor":
                    onView(withId(R.id.sign_out_btn_instructor)).perform(click());
                    break;
                case "student":
                    onView(withId(R.id.sign_out_btn_student)).perform(click());
                    break;
            }
        }

//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            Log.e("errorhere", "here");
//            try {
//                onView(withId(R.id.sign_out_btn_admin)).perform(click());
//            } catch (Error e) {
//                // since the application automatically login the user if he previously log in the app
//                // and exited the app without signing out, we need to sign out the current user
//            }
//        }
        Thread.sleep(2500); // allow the app to launch
        onView(withId(R.id.email)).perform(typeText("admin"), closeSoftKeyboard());
        onView(withId(R.id.pwd)).perform(typeText("admin123"), closeSoftKeyboard());
        onView(withId(R.id.signInBtnLogin)).perform(click());
        Thread.sleep(2500); // allow time for the transition animation to complete
        onView(withId(R.id.email)).check(doesNotExist()); // if the email field isn't in this view, this means the login was successful
    }

    @Test
    public void testB_userAlreadyLogin() throws InterruptedException {
        // this test will check if the user is still login even if the app close
        Thread.sleep(3000);
        if (role != null) {
            switch (role) {
                case "admin":
                    onView(withId(R.id.sign_out_btn_admin)).check(matches(isDisplayed()));
                    break;
                case "instructor":
                    onView(withId(R.id.sign_out_btn_instructor)).check(matches(isDisplayed()));
                    break;
                case "student":
                    onView(withId(R.id.sign_out_btn_student)).check(matches(isDisplayed()));
                    break;
            }
        }
    }

    @Test
    public void testC_signUpStudent() throws InterruptedException {
        // this test will check if the user is still login even if the app close
        Thread.sleep(3000);
        signOut();
        Thread.sleep(5000);
        Log.d("errorHere", "role in meth: " + role);
        if (role != null) {
            switch (role) {
                case "admin":
                    onView(withId(R.id.sign_out_btn_admin)).perform(click());
                    break;
                case "instructor":
                    onView(withId(R.id.sign_out_btn_instructor)).perform(click());
                    break;
                case "student":
                    onView(withId(R.id.sign_out_btn_student)).perform(click());
                    break;
            }
        }

        onView(withId(R.id.signUpBtnLogin)).perform(click());
        SecureRandom random = new SecureRandom();
        int randomNum = random.nextInt(100000);
        String userName = "testStudent" + randomNum;
        String pwd = "Stud" + randomNum;

        onView(withId(R.id.emailSignUp)).perform(typeText(userName), closeSoftKeyboard());
        onView(withId(R.id.pwdSignUp)).perform(typeText(pwd), closeSoftKeyboard());
        onView(withId(R.id.pwdconf)).perform(typeText(pwd), closeSoftKeyboard());
        Thread.sleep(2500);
        onView(withId(R.id.student)).perform(click());
        Thread.sleep(2500);
        onView(withId(R.id.signUpBtn)).perform(click());
        Thread.sleep(2500);
        onView(withId(R.id.roleStudent)).check(matches(isDisplayed()));
    }

    @Test
    public void testD_signUpInstructor() throws InterruptedException {
        // this test will check if the user is still login even if the app close
        Thread.sleep(3000);
        signOut();
        Thread.sleep(5000);
        Log.d("errorHere", "role in meth: " + role);
        if (role != null) {
            switch (role) {
                case "admin":
                    onView(withId(R.id.sign_out_btn_admin)).perform(click());
                    break;
                case "instructor":
                    onView(withId(R.id.sign_out_btn_instructor)).perform(click());
                    break;
                case "student":
                    onView(withId(R.id.sign_out_btn_student)).perform(click());
                    break;
            }
        }

        onView(withId(R.id.signUpBtnLogin)).perform(click());
        SecureRandom random = new SecureRandom();
        int randomNum = random.nextInt(100000);
        String userName = "testInst" + randomNum;
        String pwd = "Inst" + randomNum;

        onView(withId(R.id.emailSignUp)).perform(typeText(userName), closeSoftKeyboard());
        onView(withId(R.id.pwdSignUp)).perform(typeText(pwd), closeSoftKeyboard());
        onView(withId(R.id.pwdconf)).perform(typeText(pwd), closeSoftKeyboard());
        Thread.sleep(2500);
        onView(withId(R.id.instructor)).perform(click());
        Thread.sleep(2500);
        onView(withId(R.id.signUpBtn)).perform(click());
        Thread.sleep(2500);
        onView(withId(R.id.roleInstructor)).check(matches(isDisplayed()));
    }

    public void alreadyLogin() {
        // this method will sign out the user if he's already login
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //mAuth.signOut();
        if (currentUser != null) {
            try {
                onView(withId(R.id.sign_out_btn_admin)).perform(click());
            } catch (Error e) {
                // since the application automatically login the user if he previously log in the app
                // and exited the app without signing out, we need to sign out the current user
            }
        }
    }

    public void signOut() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d("errorHere", "error " + user.toString());
            db.collection("users")
                    .document(user.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                String role = document.getString("role").toString();
                                Log.d("errorHere", "role: " + role);
                                setRole(role);
                            } else {
                                Log.d("errorHere", "get failed with ", task.getException());
                            }
                        }
                    });
        }
    }

    private void setRole(String role) {
        this.role = role;
}

    // https://developer.android.com/training/testing/espresso/intents TODO

//    @Test
//    public void testC_createCourse() throws InterruptedException {
//        Thread.sleep(3000);
//        onView(withId(R.id.view_courses_page_btn)).perform(click());
//        onView(withId(R.id.create_course_btn)).perform(click());
//        onView(withId(R.id.course_name_field)).perform(typeText("admin123"), closeSoftKeyboard());
//        onView(withId(R.id.course_number_field)).perform(typeText("admin123"), closeSoftKeyboard());
//        onView(withId(R.id.recyclerViewer)).check(matches(atPosition(0, withText("Test Text"))));
//    }








//    public void testEditCourseBtn() {
//        // this method test the edit course button within the recycle view of the course list activity
//        onView(withId(R.id.recyclerViewer)).perform(RecyclerViewActions.actionOnItemAtPosition(2,onView(withId(R.id.course_edit_btn)).perform(click())));
//    }
}

