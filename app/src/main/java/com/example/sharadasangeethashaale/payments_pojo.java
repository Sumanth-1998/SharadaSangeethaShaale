package com.example.sharadasangeethashaale;

import com.google.firebase.firestore.DocumentId;

import java.text.SimpleDateFormat;
import java.util.Date;

public class payments_pojo
{
    @DocumentId
    String payment_id;

    String phone;

    String name,amount;
    Date date;

    public payments_pojo() {
    }

    public payments_pojo(String phone, String name, Date date, String amount) {
        this.phone = phone;
        this.name = name;
        this.date = date;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Payment of *INR. "+this.amount+"* received from *"+this.name+"* with thanks on *"+new SimpleDateFormat("dd/MM/yyyy").format(this.date)+"*\n";
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
