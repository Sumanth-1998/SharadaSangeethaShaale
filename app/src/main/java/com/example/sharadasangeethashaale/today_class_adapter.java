package com.example.sharadasangeethashaale;

import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class today_class_adapter extends RecyclerView.ViewHolder {
    public Spinner attendanceSpinner;
    public TextView nameTextView,fromTime,toTime;
    public today_class_adapter(@NonNull View itemView) {
        super(itemView);
        this.attendanceSpinner=itemView.findViewById(R.id.attendance_spinner);
        this.nameTextView=itemView.findViewById(R.id.nameTextView);
        this.fromTime=itemView.findViewById(R.id.fromTime);
        this.toTime=itemView.findViewById(R.id.toTime);

    }
}
