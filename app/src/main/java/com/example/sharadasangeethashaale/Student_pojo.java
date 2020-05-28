package com.example.sharadasangeethashaale;

import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Student_pojo {
    String name;
    @DocumentId
    String phone;
    String classType;
    List<String> daysOfWeek;
    Map<String, ArrayList<String>> times;
    String status;
    int rem_classes,credits,classFees;
    public Student_pojo() {
        //required constructor
    }

    public Student_pojo(String name, List<String> daysOfWeek,Map<String, ArrayList<String>> times,String status,int rem_classes,int credits,String classType,int classFees) {
        this.name = name;
        this.phone = phone;
        this.daysOfWeek = daysOfWeek;
        this.times=times;
        this.status=status;
        this.rem_classes=rem_classes;
        this.credits=credits;
        this.classType=classType;
        this.classFees=classFees;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public int getClassFees() {
        return classFees;
    }

    public void setClassFees(int classFees) {
        this.classFees = classFees;
    }

    public int getRem_classes() {
        return rem_classes;
    }

    public void setRem_classes(int rem_classes) {
        this.rem_classes = rem_classes;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
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
