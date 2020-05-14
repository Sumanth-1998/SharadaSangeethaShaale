package com.example.sharadasangeethashaale;

import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Student_pojo {
    String name;
    @DocumentId
    String phone;
    List<String> daysOfWeek;
    Map<String, ArrayList<String>> times;
    String status;
    public Student_pojo() {
        //required constructor
    }

    public Student_pojo(String name, List<String> daysOfWeek,Map<String, ArrayList<String>> times,String status) {
        this.name = name;
        this.phone = phone;
        this.daysOfWeek = daysOfWeek;
        this.times=times;
        this.status=status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, ArrayList<String>> getTimes() {
        return times;
    }

    public void setTimes(Map<String, ArrayList<String>> times) {
        this.times = times;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<String> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }
}
