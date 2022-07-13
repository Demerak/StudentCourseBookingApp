package com.example.studentcoursebookingapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CourseAdapterUnEnroll extends RecyclerView.Adapter<CourseAdapterUnEnroll.MyViewHolder> {

    private Context context;
    private ArrayList<Course> courseArrayList;

    public CourseAdapterUnEnroll(Context context, ArrayList<Course> userArrayList) {
        this.context = context;
        this.courseArrayList = userArrayList;
    }

    @NonNull
    @Override
    public CourseAdapterUnEnroll.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.course_list_item_student_unenroll, parent, false);

        return new CourseAdapterUnEnroll.MyViewHolder(v, courseArrayList);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapterUnEnroll.MyViewHolder holder, int position) {
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

            itemView.findViewById(R.id.course_unenroll_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ClickWork", "OnClick: Edit Course Click");
                    int position = getAdapterPosition();

                    Log.d("ClickWork", courseArrayList.get(position).getName());
                    Course course = courseArrayList.get(position);


                    //---Atomically add new student to the "students" array field in the course document
                    String userUID = mAuth.getCurrentUser().getUid();
                    //

                    // add the course to the courseEnroll field of the user
                    Log.d("ClickWork", String.valueOf(mAuth.getCurrentUser().getUid()));
                    DocumentReference userDoc = db.collection("users").document(mAuth.getCurrentUser().getUid());

                    db.collection("courses").whereEqualTo(CourseField.courseId.toString(),
                            course.getCourseId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                String documentID = documentSnapshot.getId();
                                userDoc.update(UserField.courseEnroll.toString(), FieldValue.arrayRemove(documentID));
                                //--
                                DocumentReference courseStudents = db.collection("courses").document(documentID);
                                courseStudents.update("students", FieldValue.arrayRemove(userUID));
                            }
                        }
                    });

                    // probably not the best way to do it but it's working
                    Intent editCourseIntent = new Intent(v.getContext(), SelectCourseStudentActivity.class);
                    editCourseIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    v.getContext().startActivity(editCourseIntent);
                }
            });
        }
    }
}
