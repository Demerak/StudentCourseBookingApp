package com.example.studentcoursebookingapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class CourseAdapterEnroll extends RecyclerView.Adapter<CourseAdapterEnroll.MyViewHolder>{

    private Context context;
    private ArrayList<Course> courseArrayList;

    public CourseAdapterEnroll(Context context, ArrayList<Course> userArrayList) {
        this.context = context;
        this.courseArrayList = userArrayList;
    }

    ViewGroup thisParent;
    @NonNull
    @Override
    public CourseAdapterEnroll.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.course_list_item_student_enroll, parent, false);
        thisParent = parent;
        return new MyViewHolder(v, courseArrayList);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapterEnroll.MyViewHolder holder, int position) {
        Course course = courseArrayList.get(position);
        holder.courseName.setText(course.getName());
        holder.courseNumber.setText(course.getCourseId());
    }

    @Override
    public int getItemCount() {
        return courseArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView courseName, courseNumber;
        private FirebaseAuth mAuth = FirebaseAuth.getInstance();
        private FirebaseFirestore db = FirebaseFirestore.getInstance();

        public MyViewHolder(@NonNull View itemView, ArrayList<Course> courseArrayList) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name_txt_view);
            courseNumber = itemView.findViewById(R.id.course_number_txt_view);
            itemView.setOnClickListener((v) -> {
                int position = getAdapterPosition();
                Log.d("clickTest", "OnClick -> Course Click: " + courseArrayList.get(position).getName());
            });

            itemView.findViewById(R.id.course_enroll_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ClickWork", "OnClick: Edit Course Click");
                    int position = getAdapterPosition();

                    Log.d("ClickWork", courseArrayList.get(position).getName());
                    Course course = courseArrayList.get(position);

                    // add the course to the courseEnroll field of the user
                    Log.d("ClickWork", String.valueOf(mAuth.getCurrentUser().getUid()));

                    //---Atomically add new student to the "students" array field in the course document
                    String userUID = mAuth.getCurrentUser().getUid();
                    //---
                    DocumentReference userDoc = db.collection("users").document(userUID);

                    db.collection("courses").whereEqualTo(CourseField.courseId.toString(),
                            course.getCourseId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                        DocumentSnapshot courseDocument = task.getResult().getDocuments().get(0);
                                        String documentID = courseDocument.getId();
                                        DocumentReference courseDoc = db.collection("courses").document(documentID);

                                        //*/ --
                                        List<String> coursesWithOverlappingTimes = new ArrayList<String>();

                                        db.collection("users")
                                       .document(userUID)
                                       .get()
                                       .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                              @Override
                                              public void onComplete(@NonNull Task<DocumentSnapshot> userTask) {
                                                  if (userTask.isSuccessful()) {
                                                      DocumentSnapshot userDocument = userTask.getResult();

                                                      if (userDocument.exists()) {
                                                          //
                                                          List<String> coursesEnrolled = (List<String>) userDocument.get("courseEnroll");

                                                          if (coursesEnrolled.size() != 0){
                                                              final int[] alldone = {0};

                                                              for (int i = 0; i < coursesEnrolled.size(); i++) { //while loop to ensure all complete listeners are finished.

                                                                  String courseUID = coursesEnrolled.get(i);

                                                                   db.collection("courses")
                                                                   .document(courseUID)
                                                                   .get()
                                                                   .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                          @Override
                                                                          public void onComplete(@NonNull Task<DocumentSnapshot> courseTask) {
                                                                              if (courseTask.isSuccessful()) {
                                                                                  DocumentSnapshot enrolledCourseDocument = courseTask.getResult();
                                                                                  if (enrolledCourseDocument.exists()) {
                                                                                      // Determine time conflicts, and must test if course has a day associated with it
                                                                                      if (courseDocument.contains("courseDay2") && enrolledCourseDocument.contains("courseDay2")){
                                                                                          String newCourseDay1 = courseDocument.get("courseDay1").toString();
                                                                                          Float newCourseStartTime1 = Float.parseFloat(courseDocument.get("courseStartTime1").toString().replace(':','.'));
                                                                                          Float newCourseEndTime1 = Float.parseFloat(courseDocument.get("courseEndTime1").toString().replace(':','.'));
                                                                                          String enrolledCourseDay1 = enrolledCourseDocument.get("courseDay1").toString();
                                                                                          Float enrolledCourseStartTime1 = Float.parseFloat(enrolledCourseDocument.get("courseStartTime1").toString().replace(':','.'));
                                                                                          Float enrolledCourseEndTime1 = Float.parseFloat(enrolledCourseDocument.get("courseEndTime1").toString().replace(':','.'));

                                                                                          String newCourseDay2 = courseDocument.get("courseDay2").toString();
                                                                                          Float newCourseStartTime2 = Float.parseFloat(courseDocument.get("courseStartTime2").toString().replace(':','.'));
                                                                                          Float newCourseEndTime2 = Float.parseFloat(courseDocument.get("courseEndTime2").toString().replace(':','.'));
                                                                                          String enrolledCourseDay2 = enrolledCourseDocument.get("courseDay2").toString();
                                                                                          Float enrolledCourseStartTime2 = Float.parseFloat(enrolledCourseDocument.get("courseStartTime2").toString().replace(':','.'));
                                                                                          Float enrolledCourseEndTime2 = Float.parseFloat(enrolledCourseDocument.get("courseEndTime2").toString().replace(':','.'));

                                                                                          if (
                                                                                              (newCourseDay1.equals(enrolledCourseDay1)&&
                                                                                              ((newCourseStartTime1>enrolledCourseStartTime1 && newCourseStartTime1<enrolledCourseEndTime1) ||
                                                                                              (newCourseEndTime1>enrolledCourseStartTime1 && newCourseEndTime1<enrolledCourseEndTime1))) ||

                                                                                              (newCourseDay2.equals(enrolledCourseDay2)&&
                                                                                              ((newCourseStartTime2>enrolledCourseStartTime2 && newCourseStartTime2<enrolledCourseEndTime2) ||
                                                                                              (newCourseEndTime2>enrolledCourseStartTime2 && newCourseEndTime2<enrolledCourseEndTime2))) ||

                                                                                              (newCourseDay1.equals(enrolledCourseDay2)&&
                                                                                              ((newCourseStartTime1>enrolledCourseStartTime2 && newCourseStartTime1<enrolledCourseEndTime2) ||
                                                                                              (newCourseEndTime1>enrolledCourseStartTime2 && newCourseEndTime1<enrolledCourseEndTime2))) ||

                                                                                              (newCourseDay2.equals(enrolledCourseDay1)&&
                                                                                              ((newCourseStartTime2>enrolledCourseStartTime1 && newCourseStartTime2<enrolledCourseEndTime1) ||
                                                                                              (newCourseEndTime2>enrolledCourseStartTime1 && newCourseEndTime2<enrolledCourseEndTime1)))

                                                                                          ){
                                                                                              coursesWithOverlappingTimes.add(enrolledCourseDocument.get("courseId").toString());
                                                                                          }

                                                                                      }
                                                                                      else if (courseDocument.contains("courseDay1") && enrolledCourseDocument.contains("courseDay2")) {
                                                                                          String newCourseDay1 = courseDocument.get("courseDay1").toString();
                                                                                          Float newCourseStartTime1 = Float.parseFloat(courseDocument.get("courseStartTime1").toString().replace(':','.'));
                                                                                          Float newCourseEndTime1 = Float.parseFloat(courseDocument.get("courseEndTime1").toString().replace(':','.'));
                                                                                          String enrolledCourseDay1 = enrolledCourseDocument.get("courseDay1").toString();
                                                                                          Float enrolledCourseStartTime1 = Float.parseFloat(enrolledCourseDocument.get("courseStartTime1").toString().replace(':','.'));
                                                                                          Float enrolledCourseEndTime1 = Float.parseFloat(enrolledCourseDocument.get("courseEndTime1").toString().replace(':','.'));
                                                                                          String enrolledCourseDay2 = enrolledCourseDocument.get("courseDay2").toString();
                                                                                          Float enrolledCourseStartTime2 = Float.parseFloat(enrolledCourseDocument.get("courseStartTime2").toString().replace(':','.'));
                                                                                          Float enrolledCourseEndTime2 = Float.parseFloat(enrolledCourseDocument.get("courseEndTime2").toString().replace(':','.'));

                                                                                          if (
                                                                                              (newCourseDay1.equals(enrolledCourseDay1)&&
                                                                                              ((newCourseStartTime1>enrolledCourseStartTime1 && newCourseStartTime1<enrolledCourseEndTime1) ||
                                                                                              (newCourseEndTime1>enrolledCourseStartTime1 && newCourseEndTime1<enrolledCourseEndTime1))) ||

                                                                                              (newCourseDay1.equals(enrolledCourseDay2)&&
                                                                                              ((newCourseStartTime1>enrolledCourseStartTime2 && newCourseStartTime1<enrolledCourseEndTime2) ||
                                                                                              (newCourseEndTime1>enrolledCourseStartTime2 && newCourseEndTime1<enrolledCourseEndTime2)))
                                                                                          ){
                                                                                              coursesWithOverlappingTimes.add(enrolledCourseDocument.get("courseId").toString());
                                                                                          }

                                                                                      } else if (courseDocument.contains("courseDay2") && enrolledCourseDocument.contains("courseDay1")) {
                                                                                          String newCourseDay1 = courseDocument.get("courseDay1").toString();
                                                                                          Float newCourseStartTime1 = Float.parseFloat(courseDocument.get("courseStartTime1").toString().replace(':','.'));
                                                                                          Float newCourseEndTime1 = Float.parseFloat(courseDocument.get("courseEndTime1").toString().replace(':','.'));
                                                                                          String newCourseDay2 = courseDocument.get("courseDay2").toString();
                                                                                          Float newCourseStartTime2 = Float.parseFloat(courseDocument.get("courseStartTime2").toString().replace(':','.'));
                                                                                          Float newCourseEndTime2 = Float.parseFloat(courseDocument.get("courseEndTime2").toString().replace(':','.'));
                                                                                          String enrolledCourseDay1 = enrolledCourseDocument.get("courseDay1").toString();
                                                                                          Float enrolledCourseStartTime1 = Float.parseFloat(enrolledCourseDocument.get("courseStartTime1").toString().replace(':','.'));
                                                                                          Float enrolledCourseEndTime1 = Float.parseFloat(enrolledCourseDocument.get("courseEndTime1").toString().replace(':','.'));

                                                                                          if (
                                                                                              (newCourseDay1.equals(enrolledCourseDay1)&&
                                                                                              ((newCourseStartTime1>enrolledCourseStartTime1 && newCourseStartTime1<enrolledCourseEndTime1) ||
                                                                                              (newCourseEndTime1>enrolledCourseStartTime1 && newCourseEndTime1<enrolledCourseEndTime1))) ||

                                                                                              (newCourseDay2.equals(enrolledCourseDay1)&&
                                                                                              ((newCourseStartTime2>enrolledCourseStartTime1 && newCourseStartTime2<enrolledCourseEndTime1) ||
                                                                                              (newCourseEndTime2>enrolledCourseStartTime1 && newCourseEndTime2<enrolledCourseEndTime1)))
                                                                                          ){
                                                                                              coursesWithOverlappingTimes.add(enrolledCourseDocument.get("courseId").toString());
                                                                                          }
                                                                                      } else if (courseDocument.contains("courseDay1") && enrolledCourseDocument.contains("courseDay1")) {
                                                                                          String newCourseDay1 = courseDocument.get("courseDay1").toString();
                                                                                          Float newCourseStartTime1 = Float.parseFloat(courseDocument.get("courseStartTime1").toString().replace(':','.'));
                                                                                          Float newCourseEndTime1 = Float.parseFloat(courseDocument.get("courseEndTime1").toString().replace(':','.'));

                                                                                          String enrolledCourseDay1 = enrolledCourseDocument.get("courseDay1").toString();
                                                                                          Float enrolledCourseStartTime1 = Float.parseFloat(enrolledCourseDocument.get("courseStartTime1").toString().replace(':','.'));
                                                                                          Float enrolledCourseEndTime1 = Float.parseFloat(enrolledCourseDocument.get("courseEndTime1").toString().replace(':','.'));

                                                                                          if (
                                                                                              (newCourseDay1.equals(enrolledCourseDay1)&&
                                                                                              ((newCourseStartTime1>enrolledCourseStartTime1 && newCourseStartTime1<enrolledCourseEndTime1) ||
                                                                                              (newCourseEndTime1>enrolledCourseStartTime1 && newCourseEndTime1<enrolledCourseEndTime1)))
                                                                                          ){
                                                                                              coursesWithOverlappingTimes.add(enrolledCourseDocument.get("courseId").toString());
                                                                                          }
                                                                                      }
                                                                                  }
                                                                                  else {
                                                                                      Log.d("TAG", "No such document");


                                                                                  }
                                                                              } else {
                                                                                  Log.d("TAG", "get failed with ", task.getException());
                                                                              }
                                                                              alldone[0]++;
                                                                              if(alldone[0] == coursesEnrolled.size()){
                                                                                  if (coursesWithOverlappingTimes.size() == 0){
                                                                                      //-- add student UID to course document "students" array field
                                                                                      DocumentReference courseDoc = db.collection("courses").document(documentID);
                                                                                      courseDoc.update("students", FieldValue.arrayUnion(userUID));

                                                                                      //enroll course
                                                                                      userDoc.update(UserField.courseEnroll.toString(), FieldValue.arrayUnion(documentID));

                                                                                      Intent editCourseIntent = new Intent(v.getContext(), SelectCourseStudentActivity.class);
                                                                                      editCourseIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                                      v.getContext().startActivity(editCourseIntent);
                                                                                  } else {
                                                                                      String toastStr = "Conflicts With: ";
                                                                                      if (coursesWithOverlappingTimes.size()>1){
                                                                                          for (int n = 0; n<coursesWithOverlappingTimes.size(); n++ ){
                                                                                              toastStr += coursesWithOverlappingTimes.get(n) + ", ";
                                                                                          }
                                                                                      } else {
                                                                                          toastStr += coursesWithOverlappingTimes.get(0);
                                                                                      }
                                                                                      Toast.makeText(SelectCourseStudentActivity.getContext(), toastStr ,Toast.LENGTH_SHORT).show();

                                                                                      Intent editCourseIntent = new Intent(v.getContext(), SelectCourseStudentActivity.class);
                                                                                      editCourseIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                                      v.getContext().startActivity(editCourseIntent);

                                                                                  }
                                                                              }
                                                                          }
                                                                   });
                                                              }
                                                          } else {
                                                              //-- add student UID to course document "students" array field
                                                              DocumentReference courseDoc = db.collection("courses").document(documentID);
                                                              courseDoc.update("students", FieldValue.arrayUnion(userUID));

                                                              //enroll course
                                                              userDoc.update(UserField.courseEnroll.toString(), FieldValue.arrayUnion(documentID));

                                                              Intent editCourseIntent = new Intent(v.getContext(), SelectCourseStudentActivity.class);
                                                              editCourseIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                              v.getContext().startActivity(editCourseIntent);
                                                          }
                                                      } else {
                                                          //-- add student UID to course document "students" array field
                                                          DocumentReference courseDoc = db.collection("courses").document(documentID);
                                                          courseDoc.update("students", FieldValue.arrayUnion(userUID));

                                                          //enroll course
                                                          userDoc.update(UserField.courseEnroll.toString(), FieldValue.arrayUnion(documentID));

                                                          Intent editCourseIntent = new Intent(v.getContext(), SelectCourseStudentActivity.class);
                                                          editCourseIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                          v.getContext().startActivity(editCourseIntent);
                                                      }
                                                  }
                                              }
                                       });

                                        // */
                                        /*

                                        userDoc.update(UserField.courseEnroll.toString(), FieldValue.arrayUnion(documentID));

                                        //-- add student UID to course document "students" array field
                                        DocumentReference course = db.collection("courses").document(documentID);
                                        course.update("students", FieldValue.arrayUnion(userUID));
                                        //*/

                                    }
                                }
                    });


                    // probably not the best way to do it but it's working
                    /*
                    Intent editCourseIntent = new Intent(v.getContext(), SelectCourseStudentActivity.class);
                    editCourseIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    v.getContext().startActivity(editCourseIntent);
                    */

                }
            });
        }
    }
}
