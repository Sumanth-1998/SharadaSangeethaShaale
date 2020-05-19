package com.example.sharadasangeethashaale.ui.WeeklySchedule;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharadasangeethashaale.R;
import com.example.sharadasangeethashaale.Schedule_holder;
import com.example.sharadasangeethashaale.Student_holder;
import com.example.sharadasangeethashaale.Student_pojo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class weeklySchedule extends Fragment implements AdapterView.OnItemSelectedListener {

    FirebaseFirestore db;
    Spinner daySpinner,studentNameSpinner;
    private FirestoreRecyclerAdapter<Student_pojo, Schedule_holder> adapter;
    RecyclerView recyclerView;
    String dayName;
    ArrayList<String> names,checkBoxNames,addstuspinneritems;
    ArrayList<CheckBox> checkBoxes;
    FloatingActionButton deletefab,addStudent;
    Dialog dialog;
    private TextView timeTextView;
    private TimePickerDialog tpick;
    String fromTime,toTime;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_weekly_schedule, container, false);
        dialog=new Dialog(getContext());
        db=FirebaseFirestore.getInstance();
        daySpinner=root.findViewById(R.id.daySpinner);
        studentNameSpinner=root.findViewById(R.id.studentSpinner);
        daySpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> spinneradapter = ArrayAdapter.createFromResource(getContext(),
                R.array.dayNames, android.R.layout.simple_spinner_item);
        recyclerView=root.findViewById(R.id.scheduleRecyclerView);
        recyclerView.setHasFixedSize(true);
        names=new ArrayList<>();
        checkBoxes=new ArrayList<>();
        checkBoxNames=new ArrayList<>();
        addstuspinneritems=new ArrayList<>();
        addstuspinneritems.add("Select");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        deletefab=root.findViewById(R.id.deletefab);
        addStudent=root.findViewById(R.id.fab);
        spinneradapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

        daySpinner.setAdapter(spinneradapter);

        deletefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(CheckBox ch:checkBoxes){

                    if(ch.isChecked()){
                        Toast.makeText(getContext(), "Hit button"+ch.isChecked(), Toast.LENGTH_SHORT).show();
                        String stuName=ch.getText().toString();
                        db.collection("students").whereEqualTo("name",stuName).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        Student_pojo stu =queryDocumentSnapshots.toObjects(Student_pojo.class).get(0);
                                        List<String> daysOfWeek=stu.getDaysOfWeek();

                                        daysOfWeek.remove(dayName);
                                        Map<String,ArrayList<String>> times=stu.getTimes();
                                        times.remove(dayName);
                                        stu.setDaysOfWeek(daysOfWeek);
                                        stu.setTimes(times);

                                        db.collection("students").document(stu.getPhone()).set(stu)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getActivity(), "Data updated successfully", Toast.LENGTH_SHORT).show();
                                                        deletefab.setVisibility(View.INVISIBLE);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getActivity(), "Failed to update data", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });
        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.add_student_dialog);
                final Spinner studentSpinner=dialog.findViewById(R.id.studentSpinner);
                db.collection("students").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<Student_pojo> stu=queryDocumentSnapshots.toObjects(Student_pojo.class);
                                addstuspinneritems.clear();
                                for(Student_pojo s:stu){
                                    if(!checkBoxNames.contains(s.getName())){
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
                Button submitButton=dialog.findViewById(R.id.submitButton);
                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                            db.collection("students").whereEqualTo("name",entName).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            Student_pojo stu=queryDocumentSnapshots.toObjects(Student_pojo.class).get(0);
                                            stu.getDaysOfWeek().add(dayName);
                                            stu.getTimes().put(dayName,new ArrayList<String>(){
                                                {
                                                    add(fromTime);
                                                    add(toTime);
                                                }
                                            });
                                            db.collection("students").document(stu.getPhone()).set(stu)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getActivity(), "Student added successfully", Toast.LENGTH_SHORT).show();
                                                            Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.weeklySchedule);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getActivity(), "Failed to add student", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(),"Failed to fetch data",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                });
                dialog.show();
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(getContext(),"Hello",Toast.LENGTH_LONG).show();
        dayName=parent.getItemAtPosition(position).toString();
        checkBoxNames.clear();
        Query query=db.collection("students").whereEqualTo("status","active").whereArrayContains("daysOfWeek",dayName);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Student_pojo> students=queryDocumentSnapshots.toObjects(Student_pojo.class);
                //Toast.makeText(getActivity(), ""+students.get(0).getName(), Toast.LENGTH_SHORT).show();
            }
        });


        FirestoreRecyclerOptions<Student_pojo> options=new FirestoreRecyclerOptions.Builder<Student_pojo>()
                .setQuery(query,Student_pojo.class)
                .build();
        adapter=new FirestoreRecyclerAdapter<Student_pojo, Schedule_holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Schedule_holder holder, int position, @NonNull final Student_pojo model) {
                holder.nameCheckBox.setText(model.getName());
                //checkBoxes.clear();

                checkBoxes.add(holder.nameCheckBox);
                checkBoxNames.add(model.getName());
                //Toast.makeText(getActivity(), ""+checkBoxNames.toString(), Toast.LENGTH_SHORT).show();
                ArrayList<String> setimes=model.getTimes().get(dayName);
                //Toast.makeText(getActivity(), ""+setimes.get(0), Toast.LENGTH_SHORT).show();
                holder.fromTime.setText(setimes.get(0));
                holder.toTime.setText(setimes.get(1));
                holder.nameCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox c=(CheckBox)v;
                        if(c.isChecked()){
                            names.add(model.getName());
                            //enable fab
                            deletefab.setVisibility(View.VISIBLE);
                        }
                        else{
                            int flag=0;
                            for(CheckBox ch:checkBoxes){
                                if(ch.isChecked()){
                                    flag=1;
                                    break;
                                }
                            }
                            if(flag==0){
                                //disable fab
                                deletefab.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });
            }

            @NonNull
            @Override
            public Schedule_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_card,parent,false);
                return new Schedule_holder(v);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }




    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}