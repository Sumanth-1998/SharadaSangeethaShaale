package com.example.sharadasangeethashaale;


import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Add_student extends Fragment {
    final int VOCAL_PRICE=100;
    final int VEENA_PRICE=150;
    EditText nameEditText,phoneEditText;
    CheckBox mondaycb,tuesdaycb,wednesdaycb,thursdaycb,fridaycb,saturdaycb;
    Button addStudent;
    String name,phone;
    List<String> daysOfWeek;
    FirebaseFirestore db;
    LinearLayout verticalLinearLayout;
    CheckBox check_ids[];
    TimePickerDialog tpick;
    Calendar cal;
    int hour,min;
    TextView starttime;
    TextView timeTextView;
    String time="";
    View root;
    int flag=0;
    Map<String, ArrayList<String>> times = new HashMap<>();
    public Add_student() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root= inflater.inflate(R.layout.fragment_add_student, container, false);
        nameEditText=root.findViewById(R.id.name);
        phoneEditText=root.findViewById(R.id.phone);
        mondaycb=root.findViewById(R.id.monday);
        tuesdaycb=root.findViewById(R.id.tuesday);
        wednesdaycb=root.findViewById(R.id.wednesday);
        thursdaycb=root.findViewById(R.id.thursday);
        fridaycb=root.findViewById(R.id.friday);
        saturdaycb=root.findViewById(R.id.saturday);
        addStudent=root.findViewById(R.id.addStudent);
        verticalLinearLayout=root.findViewById(R.id.verticalLinearLayout);

        //dayLinearLayout=root.findViewById(R.id.daysLinearLayout);
        check_ids =new CheckBox[]{mondaycb,tuesdaycb,wednesdaycb,thursdaycb,fridaycb,saturdaycb};
        for (CheckBox c:check_ids) {
            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox clickedCheckbox=(CheckBox)v;
                    int viewId=v.getId();
                    Toast.makeText(getActivity(), ""+viewId, Toast.LENGTH_SHORT).show();
                    int id=0;
                    switch (viewId){
                        case R.id.monday:id=R.id.MondayLinearLayout;
                            break;
                        case R.id.tuesday:id=R.id.TuesdayLinearLayout;
                            break;
                        case R.id.wednesday:id=R.id.WednesdayLinearLayout;
                            break;
                        case R.id.thursday:id=R.id.ThursdayLinearLayout;
                            break;
                        case R.id.friday:id=R.id.FridayLinearLayout;
                            break;
                        case R.id.saturday:id=R.id.SaturdayLinearLayout;

                    }
                    if(clickedCheckbox.isChecked())
                        addView(clickedCheckbox.getText().toString(),id);
                    else {
                        verticalLinearLayout.removeView(root.findViewById(id));

                    }
                }
            });
        }
        daysOfWeek=new ArrayList<>();
        db=FirebaseFirestore.getInstance();

        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=0;
                name = nameEditText.getText().toString();
                phone = phoneEditText.getText().toString();

                if (name.equals("") || name == null) {
                    flag = 1;
                    nameEditText.setError("Please enter the name");
                }
                if (phone.equals("") || phone == null) {
                    flag = 1;
                    phoneEditText.setError("Please enter phone number");
                }
                if (!(mondaycb.isChecked() || tuesdaycb.isChecked() || wednesdaycb.isChecked() || thursdaycb.isChecked() || fridaycb.isChecked() || saturdaycb.isChecked())) {
                    /*mondaycb.setError("At least one day required");
                    tuesdaycb.setError("At least one day required");
                    wednesdaycb.setError("At least one day required");
                    thursdaycb.setError("At least one day required");
                    fridaycb.setError("At least one day required");
                    saturdaycb.setError("At least one day required");*/
                    flag = 1;
                    Snackbar.make(getView(), "Atleast One Day Needs To be Selected", Snackbar.LENGTH_LONG).show();
                }

                for (int i = 0; i < check_ids.length; i++) {
                    if (check_ids[i].isChecked()) {
                        daysOfWeek.add(check_ids[i].getText().toString());
                        String dayName = check_ids[i].getText().toString();

                        ArrayList<String> classtiming = new ArrayList<String>();
                        TextView startText = root.findViewById(getFromTimeId(dayName));
                        TextView endText = root.findViewById(getToTimeId(dayName));
                        String startTiming = startText.getText().toString();
                        String endTiming = endText.getText().toString();
                        if (startTiming.equals("Time") || startTiming == null) {
                            TextInputLayout parent = (TextInputLayout)startText.getParent();
                            parent.setError("Time Required");
                            flag = 1;
                        }
                        if (endTiming.equals("Time") || endTiming == null) {
                            flag = 1;
                            TextInputLayout parent = (TextInputLayout)endText.getParent();
                            parent.setError("Time Required");
                        }
                        classtiming.add(startTiming);
                        classtiming.add(endTiming);
                        times.put(dayName, classtiming);

                    }
                }
                if (flag == 0) {
                    Spinner typeSpinner=root.findViewById(R.id.typeSpinner);
                    String choice=typeSpinner.getSelectedItem().toString();
                    int price=-1;
                    switch(choice){
                        case "Vocals":price=VOCAL_PRICE;
                        break;
                        case "Veena":price=VEENA_PRICE;
                        break;
                    }
                    db.collection("students").document(phone).set(new Student_pojo(name, daysOfWeek,times,"active",0,0,choice,price))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.manageStudents);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(getView(),"Failed to add data to server",Snackbar.LENGTH_LONG).show();
                                }
                            });

                    db.collection("students").document(phone).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    Student_pojo student = documentSnapshot.toObject(Student_pojo.class);
                                    Toast.makeText(getActivity(), "" + student.getPhone(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });
        return root;
    }
    public void addView(String name,int id){
        //Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
        //verticalLinearLayout.removeAllViews();



                LinearLayout horizontalLinearLayout=getHorizontalLinearLayout(name,id);
                verticalLinearLayout.addView(horizontalLinearLayout);


    }
    public LinearLayout getHorizontalLinearLayout(String name,int id){
        LinearLayout horizontalLinearLayout=new LinearLayout(getContext());
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(50,20,20,20);
        horizontalLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLinearLayout.setId(id);

        horizontalLinearLayout.setLayoutParams(p);

        TextInputLayout timeInputLayout=new TextInputLayout(getContext());
        LinearLayout.LayoutParams r=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        timeInputLayout.setLayoutParams(r);
        TextView dayname=new TextView(getContext());
        dayname.setTextAppearance(R.style.dayNameFont);
        dayname.setText(name+": ");
        starttime=new TextView(getContext());
        starttime.setTextAppearance(R.style.TimeFont);
        starttime.setText("Time");
        starttime.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_time,0);
        timeInputLayout.addView(starttime);

        starttime.setId(getFromTimeId(name));
        cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);
        starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Plese", Toast.LENGTH_SHORT).show();
                getTime(v);
            }
        });

        TextView to=new TextView(getContext());
        to.setTextAppearance(R.style.dayNameFont);
        LinearLayout.LayoutParams q=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        q.setMargins(45,0,45,0);
        to.setLayoutParams(q);
        to.setText("TO");


        TextInputLayout endTimeInputLayout=new TextInputLayout(getContext());
        endTimeInputLayout.setLayoutParams(r);
        TextView endtime=new TextView(getContext());
        endtime.setTextAppearance(R.style.TimeFont);
        endtime.setText("Time");
        endtime.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_time,0);
        endtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getTime(v);
            }
        });
        endtime.setId(getToTimeId(name));
        endTimeInputLayout.addView(endtime);

        //dayname.setLayoutParams(p2);
        horizontalLinearLayout.addView(dayname);
        horizontalLinearLayout.addView(timeInputLayout);
        horizontalLinearLayout.addView(to);
        horizontalLinearLayout.addView(endTimeInputLayout);
        return horizontalLinearLayout;
    }
    public void getTime(View v){
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
                        time= prefix + hour + ":" + min + " " + ampm;
                        timeTextView.setText(time);
                        ((TextInputLayout)timeTextView.getParent()).setError(null);
                    }
                },hour,min,false);
        tpick.show();

        Toast.makeText(getContext(), "Returned"+time, Toast.LENGTH_SHORT).show();

    }
    public int getFromTimeId(String name){
        switch (name){
            case "Monday":return R.id.FromMonday;
            case "Tuesday":return R.id.FromTuesday;
            case "Wednesday":return R.id.FromWednesday;
            case "Thursday":return R.id.FromThursday;
            case "Friday":return R.id.FromFriday;
            case "Saturday":return R.id.FromSaturday;
        }
        return 0;
    }
    public int getToTimeId(String name){
        switch (name){
            case "Monday":return R.id.ToMonday;
            case "Tuesday":return R.id.ToTuesday;
            case "Wednesday":return R.id.ToWednesday;
            case "Thursday":return R.id.ToThursday;
            case "Friday":return R.id.ToFriday;
            case "Saturday":return R.id.ToSaturday;
        }
        return 0;
    }

}
