package com.example.sharadasangeethashaale.ui.WeeklySchedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class weeklySchedule extends Fragment implements AdapterView.OnItemSelectedListener {

    FirebaseFirestore db;
    Spinner daySpinner;
    private FirestoreRecyclerAdapter<Student_pojo, Schedule_holder> adapter;
    RecyclerView recyclerView;
    String dayName;
    ArrayList<String> names;
    ArrayList<CheckBox> checkBoxes;
    FloatingActionButton deletefab;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_weekly_schedule, container, false);
        db=FirebaseFirestore.getInstance();
        daySpinner=root.findViewById(R.id.daySpinner);
        daySpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> spinneradapter = ArrayAdapter.createFromResource(getContext(),
                R.array.dayNames, android.R.layout.simple_spinner_item);
        recyclerView=root.findViewById(R.id.scheduleRecyclerView);
        recyclerView.setHasFixedSize(true);
        names=new ArrayList<>();
        checkBoxes=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        deletefab=root.findViewById(R.id.deletefab);

        spinneradapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

        daySpinner.setAdapter(spinneradapter);
        deletefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(CheckBox ch:checkBoxes){
                    if(ch.isChecked()){
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


        return root;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getContext(),"Hello",Toast.LENGTH_LONG).show();
        dayName=parent.getItemAtPosition(position).toString();

        Query query=db.collection("students").whereEqualTo("status","active").whereArrayContains("daysOfWeek",dayName);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Student_pojo> students=queryDocumentSnapshots.toObjects(Student_pojo.class);
                Toast.makeText(getActivity(), ""+students.get(0).getName(), Toast.LENGTH_SHORT).show();
            }
        });


        FirestoreRecyclerOptions<Student_pojo> options=new FirestoreRecyclerOptions.Builder<Student_pojo>()
                .setQuery(query,Student_pojo.class)
                .build();
        adapter=new FirestoreRecyclerAdapter<Student_pojo, Schedule_holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Schedule_holder holder, int position, @NonNull final Student_pojo model) {
                holder.nameCheckBox.setText(model.getName());
                checkBoxes.add(holder.nameCheckBox);
                ArrayList<String> setimes=model.getTimes().get(dayName);
                Toast.makeText(getActivity(), ""+setimes.get(0), Toast.LENGTH_SHORT).show();
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