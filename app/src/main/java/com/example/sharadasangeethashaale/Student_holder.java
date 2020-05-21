package com.example.sharadasangeethashaale;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

public class Student_holder extends RecyclerView.ViewHolder {
    public TextView nameTextView,phoneTextView,daysOfWeek;
    public CardView studentCard;
    public ImageButton deleteButton;
    public TextView nameLetter,rem_clas;

    public Student_holder(@NonNull View itemView) {
        super(itemView);
        this.nameTextView=itemView.findViewById(R.id.nameTextView);
        this.phoneTextView=itemView.findViewById(R.id.phoneTextView);
        this.daysOfWeek=itemView.findViewById(R.id.daysOfWeek);
        this.studentCard=itemView.findViewById(R.id.studentCardView);
        this.deleteButton=itemView.findViewById(R.id.deleteButton);
        this.nameLetter=itemView.findViewById(R.id.nameLetter);
        this.rem_clas=itemView.findViewById(R.id.rem_clas);
    }
}
