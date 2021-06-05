package com.example.sharadasangeethashaale.recycleradapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharadasangeethashaale.R;
import com.example.sharadasangeethashaale.attendance_pojo;
import com.example.sharadasangeethashaale.student_attendance_holder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class attendanceAdaper extends RecyclerView.Adapter<student_attendance_holder> {

    private List<attendance_pojo> attendances;
    public attendanceAdaper(List<attendance_pojo> attendances) {
        this.attendances = attendances;
    }

    @NonNull
    @Override
    public student_attendance_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_attendance_layout, parent, false);
        return new student_attendance_holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull student_attendance_holder holder, int position) {
            attendance_pojo attendance = attendances.get(position);
            holder.date.setText(new SimpleDateFormat("dd/MM/yyyy").format(attendance.getClassDate()));
            holder.presence.setText(getPresence(attendance.getAttendance()));
    }

    @Override
    public int getItemCount() {
        return attendances.size();
    }

    public String getPresence(String attendance){
        switch (attendance){
            case "A  ":return "ABSENT";
            case "P  ":return  "PRESENT";
            case "C  ":return  "CANCELLED";
        }
        return "";
    }
}
