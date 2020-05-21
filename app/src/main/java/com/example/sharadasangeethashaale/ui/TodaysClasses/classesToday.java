package com.example.sharadasangeethashaale.ui.TodaysClasses;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharadasangeethashaale.R;
import com.example.sharadasangeethashaale.Student_holder;
import com.example.sharadasangeethashaale.Student_pojo;
import com.example.sharadasangeethashaale.attendance_pojo;
import com.example.sharadasangeethashaale.today_class_adapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.VISIBLE;

public class classesToday extends Fragment implements DatePickerListener {

    private HomeViewModel homeViewModel;
    String dayName;
    private FirestoreRecyclerAdapter<Student_pojo, today_class_adapter> adapter;
    private FirestoreRecyclerAdapter<attendance_pojo, today_class_adapter> attendanceadapter;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    Button attendance,calendar,updateAttendance;
    ArrayList<CardView> cardsList;
    String date;
    int flag;
    DateTime dateChosen;
    DatePickerDialog dpick;
    String name,fromTime,toTime;
    Map<String,String> prev,phone;
    attendance_pojo att;
    WriteBatch batch1;
    List<String> attNamesList,allNamesDisplayed;
    FloatingActionButton fab;
    Dialog dialog;
    private TextView timeTextView;
    TimePickerDialog tpick;
    private List<String> addstuspinneritems;
    Map<String,String> getPhone;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        db=FirebaseFirestore.getInstance();
        View root = inflater.inflate(R.layout.fragment_classes_today, container, false);
        cardsList=new ArrayList<>();
        dialog=new Dialog(getContext());
        fab=root.findViewById(R.id.addStuAtt);
        allNamesDisplayed=new ArrayList<>();
        addstuspinneritems=new ArrayList<>();
        updateAttendance=root.findViewById(R.id.updateAttendance);
        final HorizontalPicker picker = (HorizontalPicker) root.findViewById(R.id.datePicker);
        recyclerView=root.findViewById(R.id.todayRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        prev=new HashMap<>();
        phone=new HashMap<>();
        getPhone=new HashMap<>();
        attNamesList=new ArrayList<>();
        attendance=root.findViewById(R.id.attendance);
        calendar=root.findViewById(R.id.calendar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.add_student_dialog);
                TextView  title=dialog.findViewById(R.id.titleText);
                Button submitButton=dialog.findViewById(R.id.submitButton);
                title.setText("ADD STUDENT TO TODAY'S CLASS");
                submitButton.setText("ADD TO CLASS");
                final Spinner studentSpinner=dialog.findViewById(R.id.studentSpinner);
                db.collection("students").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<Student_pojo> stu=queryDocumentSnapshots.toObjects(Student_pojo.class);
                                addstuspinneritems.clear();
                                for(Student_pojo s:stu){
                                    getPhone.put(s.getName(),s.getPhone());
                                    if(!allNamesDisplayed.contains(s.getName())){
                                        addstuspinneritems.add(s.getName());

                                    }
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        getActivity(),
                                        android.R.layout.simple_spinner_item,
                                        addstuspinneritems
                                );
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                studentSpinner.setAdapter(adapter);
                            }
                        });
                Button close=dialog.findViewById(R.id.closeButton);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                final TextView fromTv,toTv;
                fromTv=dialog.findViewById(R.id.fromTimeTV);
                toTv=dialog.findViewById(R.id.toTimeTV);
                fromTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTime(v);
                    }
                });
                toTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTime(v);
                    }
                });

                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WriteBatch batch2 = db.batch();
                        int flag=0;
                        String entName=studentSpinner.getSelectedItem().toString();

                        fromTime=fromTv.getText().toString();
                        toTime=toTv.getText().toString();

                        if(entName.equals("Select")){
                            flag=1;
                            ((TextInputLayout)studentSpinner.getParent()).setError("Please select a student");
                        }
                        if(fromTime.equals("From")){
                            flag=1;
                            ((TextInputLayout)fromTv.getParent()).setError("Please select start time");
                        }
                        if(toTime.equals("To")){
                            flag=1;
                            ((TextInputLayout)toTv.getParent()).setError("Please select end time");
                        }
                        if(flag==0){
                            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
                            String date=sdf.format(new Date());
                            attendance_pojo at=new attendance_pojo(entName,fromTime,toTime,"P  ",getPhone.get(entName),new Date());
                            DocumentReference df=db.collection("attendance").document(date).collection("student").document(at.getName());
                            batch2.set(df,at);

                            batch2.update(db.collection("students").document(getPhone.get(at.getName())), "rem_classes",FieldValue.increment(-1));
                            batch2.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(), "Student Added Successfully!", Toast.LENGTH_SHORT).show();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "Failed to add student to the class!!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                    }
                });
                dialog.show();
            }
        });
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int date=cal.get(Calendar.DATE);
                int month=cal.get(Calendar.MONTH);
                int year=cal.get(Calendar.YEAR);



                dpick = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
                                DateTime dt = formatter.parseDateTime(""+dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                                picker.setDate(dt);
                                Log.d("Original2",dt.toString());
                            }
                        }, year, month, date);
                dpick.getDatePicker().setMinDate(new DateTime().minusDays(30).getMillis());
                dpick.show();
            }
        });


        updateAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Phone getss",phone.toString());
                batch1 = db.batch();
                Toast.makeText(getActivity(), "Update Hit", Toast.LENGTH_SHORT).show();
                db.collection("attendance").document(date).collection("student").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot d:queryDocumentSnapshots){
                                    prev.put(d.getId(),d.getString("attendance"));
                                }
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(cardsList!=null){

                                    for(CardView cardView:cardsList) {
                                        final String attendance = ((Spinner) cardView.findViewById(R.id.attendance_spinner)).getSelectedItem().toString();
                                        Toast.makeText(getContext(), "Hit", Toast.LENGTH_SHORT).show();
                                        name = ((TextView) cardView.findViewById(R.id.nameTextView)).getText().toString();
                                        String fromtime = ((TextView) cardView.findViewById(R.id.fromTime)).getText().toString();
                                        String toTime = ((TextView) cardView.findViewById(R.id.toTime)).getText().toString();
                                        attendance_pojo att = new attendance_pojo(name, fromtime, toTime, attendance, phone.get(name), new Date());
                                        batch1.set(db.collection("attendance").document(date).collection("student").document(name), att);
                                        //Log.d("Break5",prev.get(name));
                                        if (prev.containsKey(name)){
                                            if (!attendance.equals(prev.get(name))) {
                                                if (!prev.get(name).equals("P  ") && attendance.equals("P  ")) {
                                                    batch1.update(db.collection("students").document(phone.get(name)), "rem_classes", FieldValue.increment(-1));
                                                }
                                                if (prev.get(name).equals("P  ") && !attendance.equals("P  ")) {
                                                    //Log.d("Phone get",phone.get(name));
                                                    batch1.update(db.collection("students").document(phone.get(name)), "rem_classes", FieldValue.increment(1));
                                                }
                                            }
                                    }
                        /*db.collection("attendance").document(date).collection("students").document(name).set(att)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(), "Data Added Successfully", Toast.LENGTH_SHORT).show();

                                            db.collection("students").whereEqualTo("name",name).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(attendance.equals("P  ") && !attendance.equals(prev.get(name))) {
                                                                Student_pojo s = task.getResult().toObjects(Student_pojo.class).get(0);
                                                                s.setRem_classes(s.getRem_classes() - 1);
                                                                db.collection("students").document(s.getPhone()).set(s);
                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getActivity(), "Failed to update attendance", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        onDateSelected(dateChosen);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Data failed to add", Toast.LENGTH_SHORT).show();
                                    }
                                });*/
                                    }



                                    batch1.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Break","Yess");
                                        }
                                    });

                                }
                            }
                        });

            }
        });
        // initialize it and attach a listener

        picker.setListener(this).setOffset(30).init();

        picker.setDate(new DateTime());
        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Phone get1",phone.toString());
                WriteBatch batch = db.batch();
                if(cardsList!=null){

                    for(CardView cardView:cardsList){
                        final String attendance=((Spinner)cardView.findViewById(R.id.attendance_spinner)).getSelectedItem().toString();
                        Toast.makeText(getContext(), "Hiiiiiiit", Toast.LENGTH_SHORT).show();
                        name=((TextView)cardView.findViewById(R.id.nameTextView)).getText().toString();
                        String fromtime=((TextView)cardView.findViewById(R.id.fromTime)).getText().toString();
                        String toTime=((TextView)cardView.findViewById(R.id.toTime)).getText().toString();
                        attNamesList.add(name);
                        Log.d("Phone set",phone.toString());
                        att=new attendance_pojo(name,fromtime,toTime,attendance,phone.get(name),new Date());
                        DocumentReference df=db.collection("attendance").document(date).collection("student").document(att.getName());
                        batch.set(df,att);
                        batch.update(db.collection("students").document(phone.get(name)), "rem_classes",FieldValue.increment(-1));

                        /*db.collection("attendance").document(date).collection("students").document(att.getName()).set(att)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Enter",att.getName());
                                        Toast.makeText(getActivity(), "Data Added Successfully", Toast.LENGTH_SHORT).show();
                                        //if(attendance.equals("P  "))
                                        Log.d("Break2",phone.get(name));
                                        db.collection("students").document(phone.get(name)).update("rem_classes",FieldValue.increment(-1));

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Data failed to add", Toast.LENGTH_SHORT).show();
                                    }
                                });*/
                    }


                    //batch.set(db.collection("attendance").document(date),indexMap, SetOptions.merge());
                    /*batch.set(db.collection("attendance").document(date),new HashMap<String,Date>(){{
                        put("date",new Date());
                    }}, SetOptions.merge());*/
                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                            onDateSelected(dateChosen);
                        }
                    });
                }
            }
        });




        return root;
    }


    public  void setTime(View v){
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        timeTextView=(TextView)v;
        tpick=new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String ampm = "AM", prefix = "";
                        int hour = hourOfDay, hourInDay = 24;
                        String min = Integer.toString(minute);

                        if (minute < 10)
                            min = "0" + minute;
                        if (hourOfDay >= 12) {
                            ampm = "PM";
                        }
                        if (hourOfDay != 12)
                            hour = hourOfDay % 12;

                        if (hour < 10)
                            prefix = "0";
                        String time= prefix + hour + ":" + min + " " + ampm;
                        timeTextView.setText(time);
                        ((TextInputLayout)timeTextView.getParent()).setError(null);
                    }
                },hour,min,false);
        tpick.show();
    }


    @Override
    public void onDateSelected(final DateTime dateSelected) {
        //Toast.makeText(getActivity(), "Date set", Toast.LENGTH_SHORT).show();
        //Toast.makeText(getActivity(), ""+dateSelected.getDayOfWeek(), Toast.LENGTH_SHORT).show();
        //Log.d("Original",dateSelected.toString());
        attendance.setVisibility(View.INVISIBLE);
        updateAttendance.setVisibility(View.INVISIBLE);
        dateChosen=dateSelected;
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        date=sdf.format(dateSelected.toDate());
        //Toast.makeText(getContext(), ""+date, Toast.LENGTH_SHORT).show();
        cardsList.clear();

        if(dateSelected.getDayOfYear()>new DateTime().getDayOfYear()){
            displayPresentAttendance(dateSelected);
            fab.setVisibility(View.INVISIBLE);
        }
        else if(dateSelected.getDayOfYear()==new DateTime().getDayOfYear()){

            db.collection("attendance").document(date).collection("student").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            QuerySnapshot querySnapshot=task.getResult();
                            if(querySnapshot.isEmpty())
                                displayPresentAttendance(dateSelected);
                            else
                                displayOldAttendance();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed to get data", Toast.LENGTH_SHORT).show();
                        }
                    });
            fab.setVisibility(View.VISIBLE);
        }
        else {
            displayOldAttendance();
            fab.setVisibility(View.VISIBLE);

        }
    }
    public void displayPresentAttendance(DateTime dateSelected){
        attendance.setVisibility(View.INVISIBLE);
        phone.clear();
        allNamesDisplayed.clear();
        //Toast.makeText(getActivity(), "Entered", Toast.LENGTH_SHORT).show();
        dayName = getDayName(dateSelected.getDayOfWeek());
        Query query = db.collection("students").whereArrayContains("daysOfWeek", dayName).whereEqualTo("status", "active");
        FirestoreRecyclerOptions<Student_pojo> options = new FirestoreRecyclerOptions.Builder<Student_pojo>()
                .setQuery(query, Student_pojo.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<Student_pojo, today_class_adapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull today_class_adapter holder, int position, @NonNull Student_pojo model) {

                holder.nameTextView.setText(model.getName());
                phone.put(model.getName(),model.getPhone());
                allNamesDisplayed.add(model.getName());
                Log.d("Phone in att",phone.toString());
                Log.d("Break",phone.toString());
                //Toast.makeText(getContext(), "" + model.getName(), Toast.LENGTH_SHORT).show();
                holder.fromTime.setText(model.getTimes().get(dayName).get(0));
                holder.toTime.setText(model.getTimes().get(dayName).get(1));
                cardsList.add(holder.cardView);
                attendance.setVisibility(View.INVISIBLE);
            }

            @NonNull
            @Override
            public today_class_adapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.today_class_card, parent, false);
                return new today_class_adapter(v);
            }
        };
        /*if(dateSelected.getDayOfYear()>new DateTime().getDayOfYear()) {
            //displayPresentAttendance(dateSelected);
            attendance.setVisibility(View.INVISIBLE);
        }else{
            attendance.setVisibility(View.VISIBLE);
        }*/
        adapter.startListening();
        attendance.setText("SUBMIT ATTENDANCE");

        recyclerView.setAdapter(adapter);
    }
    public void displayOldAttendance(){
        flag=0;
        //cardsList.clear();
        allNamesDisplayed.clear();
        phone.clear();
        Query query=db.collection("attendance").document(date).collection("student");
        FirestoreRecyclerOptions<attendance_pojo> options=new FirestoreRecyclerOptions.Builder<attendance_pojo>()
                .setQuery(query,attendance_pojo.class)
                .build();
        attendanceadapter=new FirestoreRecyclerAdapter<attendance_pojo, today_class_adapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull today_class_adapter holder, int position, @NonNull attendance_pojo model) {


                cardsList.add(holder.cardView);
                phone.put(model.getName(),model.getPhone());
                allNamesDisplayed.add(model.getName());
                holder.attendanceSpinner.setSelection(getPosition(model.getAttendance()));
                holder.nameTextView.setText(model.getName());
                holder.fromTime.setText(model.getFormtime());
                holder.toTime.setText(model.getTotime());
                flag=1;
                updateAttendance.setVisibility(View.VISIBLE);
            }

            @NonNull
            @Override
            public today_class_adapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.today_class_card, parent, false);
                return new today_class_adapter(v);
            }
        };

        updateAttendance.setText("UPDATE ATTENDANCE");

        attendanceadapter.startListening();


        recyclerView.setAdapter(attendanceadapter);


    }
    public int getPosition(String c){
        if(c.equals("P  ")){
            return 0;
        }
        if(c.equals("A  ")){
            return 1;
        }
        if(c.equals("C  ")){
            return 2;
        }
        return -1;
    }
    public String getDayName(int dayOfWeek){
        switch (dayOfWeek){
            case 1:return "Monday";
            case 2:return "Tuesday";
            case 3:return "Wednesday";
            case 4:return "Thursday";
            case 5:return "Friday";
            case 6:return "Saturday";

        }
        return "";
    }

}