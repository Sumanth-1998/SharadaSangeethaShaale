package com.example.sharadasangeethashaale;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class attendance_pojo {
    String formtime,totime,attendance,phone;
    Date classDate;


    String name;

    public attendance_pojo() {
    }

    public attendance_pojo(String name, String formtime, String totime, String attendance,String phone,Date classDate) {
        this.name = name;
        this.formtime = formtime;
        this.totime = totime;
        this.attendance = attendance;
        this.phone=phone;
        this.classDate=classDate;
    }

    public Date getClassDate() {
        return classDate;
    }

    public void setClassDate(Date classDate) {
        this.classDate = classDate;
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

    public String getFormtime() {
        return formtime;
    }

    public void setFormtime(String formtime) {
        this.formtime = formtime;
    }

    public String getTotime() {
        return totime;
    }

    public void setTotime(String totime) {
        this.totime = totime;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }
}
