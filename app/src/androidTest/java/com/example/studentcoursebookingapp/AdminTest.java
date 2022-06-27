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

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminTest {

    // Android UI tests
    // Note: I use Thread.sleep() which is obviously bad practice but it's suitable for this exercise.
    //       I should use idling resources instead.

    @Rule
    public ActivityScenarioRule<MainActivity> activityTestRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testA_adminLogin() throws InterruptedException {
        Thread.sleep(2500); // allow the app to launch

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
        onView(withId(R.id.sign_out_btn_admin)).check(matches(isDisplayed()));
    }

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

