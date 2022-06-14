package com.example.studentcoursebookingapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import static androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.MyViewHolder>{

    Context context;
    ArrayList<Course> courseArrayList;

    public CourseAdapter(Context context, ArrayList<Course> userArrayList) {
        this.context = context;
        this.courseArrayList = userArrayList;
    }

    @NonNull
    @Override
    public CourseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.course_list_item, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.MyViewHolder holder, int position) {
        Course course = courseArrayList.get(position);
        holder.courseName.setText(course.getName());
        holder.courseNumber.setText(course.getNumber());
    }

    @Override
    public int getItemCount() {
        return courseArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView courseName, courseNumber;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name_txt_view);
            courseNumber = itemView.findViewById(R.id.course_number_txt_view);





        }
    }
}
