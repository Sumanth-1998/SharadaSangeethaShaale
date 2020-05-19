package com.example.sharadasangeethashaale;

import com.google.firebase.firestore.DocumentId;

public class attendance_pojo {
    String formtime,totime,attendance;

    @DocumentId
    String name;

    public attendance_pojo() {
    }

    public attendance_pojo(String name, String formtime, String totime, String attendance) {
        this.name = name;
        this.formtime = formtime;
        this.totime = totime;
        this.attendance = attendance;
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
