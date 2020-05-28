package com.example.sharadasangeethashaale;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TreeMap;

public class attendance_adapter extends RecyclerView.Adapter<student_attendance_holder> {

    TreeMap<String,String> attendances;
    public attendance_adapter(TreeMap<String,String> attendances){
        this.attendances=attendances;
    }

    @NonNull
    @Override
    public student_attendance_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View attendanceView = inflater.inflate(R.layout.student_attendance_layout, parent, false);

        // Return a new holder instance
        student_attendance_holder viewHolder = new student_attendance_holder(attendanceView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull student_attendance_holder holder, int position) {
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        Log.d("Model",attendances.keySet().toArray()[position].toString());
        holder.date.setText(attendances.keySet().toArray()[position].toString());
        holder.presence.setText(attendances.get(attendances.keySet().toArray()[position].toString()));
    }

    @Override
    public int getItemCount() {
        return attendances.size();
    }
}
