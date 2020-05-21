package com.example.sharadasangeethashaale;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class payments_holder extends RecyclerView.ViewHolder {
    public TextView name,date,amount,paymentId;

    public payments_holder(@NonNull View itemView) {
        super(itemView);
        this.name=itemView.findViewById(R.id.name);
        this.date=itemView.findViewById(R.id.date);
        this.amount=itemView.findViewById(R.id.amount);
        this.paymentId=itemView.findViewById(R.id.paymentId);
    }
}
