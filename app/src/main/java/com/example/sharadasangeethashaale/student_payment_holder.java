package com.example.sharadasangeethashaale;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class student_payment_holder extends RecyclerView.ViewHolder{
    public TextView date,amount,paymentId;

    public student_payment_holder(@NonNull View itemView) {
        super(itemView);

        this.date=itemView.findViewById(R.id.date);
        this.amount=itemView.findViewById(R.id.amount);
        this.paymentId=itemView.findViewById(R.id.paymentId);
    }
}
