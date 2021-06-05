package com.example.sharadasangeethashaale;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class student_attendance_holder extends RecyclerView.ViewHolder {
    public TextView date,presence;
    public student_attendance_holder(@NonNull View itemView) {
        super(itemView);
        this.date=itemView.findViewById(R.id.date);
        this.presence=itemView.findViewById(R.id.presence);
    }
}
