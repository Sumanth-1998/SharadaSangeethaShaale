package com.example.sharadasangeethashaale;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Schedule_holder extends RecyclerView.ViewHolder
{
    public CheckBox nameCheckBox;
    public TextView fromTime,toTime;


    public Schedule_holder(@NonNull View itemView) {
        super(itemView);
        this.nameCheckBox=itemView.findViewById(R.id.nameCheckBox);
        this.fromTime=itemView.findViewById(R.id.fromTime);
        this.toTime=itemView.findViewById(R.id.toTime);
    }
}
