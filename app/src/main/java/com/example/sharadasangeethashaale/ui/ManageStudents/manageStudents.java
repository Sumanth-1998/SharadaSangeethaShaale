package com.example.sharadasangeethashaale.ui.ManageStudents;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.Navigator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharadasangeethashaale.R;
import com.example.sharadasangeethashaale.Student_holder;
import com.example.sharadasangeethashaale.Student_pojo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class manageStudents extends Fragment {

    FloatingActionButton fab;
    private FirestoreRecyclerAdapter<Student_pojo, Student_holder> adapter;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    List<String> daysOfWeek;
    public manageStudents(){

    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_manage_students, container, false);
        recyclerView=root.findViewById(R.id.studentRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        db=FirebaseFirestore.getInstance();
        Query query=db.collection("students").whereEqualTo("status","active");
        FirestoreRecyclerOptions<Student_pojo> options=new FirestoreRecyclerOptions.Builder<Student_pojo>()
                .setQuery(query,Student_pojo.class)
                .build();
        adapter=new FirestoreRecyclerAdapter<Student_pojo, Student_holder>(options) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected void onBindViewHolder(@NonNull final Student_holder holder, int position, @NonNull final Student_pojo model) {
                holder.nameTextView.setText(model.getName());
                holder.phoneTextView.setText(model.getPhone());
                holder.rem_clas.setText(Integer.toString(model.getRem_classes()));
                if(model.getRem_classes()<=2){
                    holder.rem_clas.setTextColor(getResources().getColor(R.color.red));
                }
                daysOfWeek=model.getDaysOfWeek();
                for(int i=0;i<daysOfWeek.size();i++){
                    if(daysOfWeek.get(i).equals("Tuesday")){
                        daysOfWeek.remove(i);
                        daysOfWeek.add(i,"Tu");
                    }
                    else if(daysOfWeek.get(i).equals("Thursday")){
                        daysOfWeek.remove(i);
                        daysOfWeek.add(i,"Th");
                    }
                    else{
                        char c=daysOfWeek.get(i).charAt(0);
                        daysOfWeek.remove(i);
                        daysOfWeek.add(i,""+c);
                    }
                }
                String str="";
                for(String s:daysOfWeek){
                    str+=s+" | ";

                }
                str=str.substring(0,str.lastIndexOf(" | "));
                holder.daysOfWeek.setText(str);
                holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                        builder.setMessage("Are you sure to delete the student?");
                        builder.setTitle("Alert!");
                        builder.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.collection("students").document(model.getPhone()).update("status","inactive")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getActivity(), "Data deleted successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getActivity(), "Failed to delete data", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                        );
                        AlertDialog dialog=builder.create();
                        dialog.show();
                    }
                });
                holder.nameLetter.setText(getInitials(model.getName()));
                holder.studentCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle=new Bundle();
                        bundle.putString("name",model.getName());
                        bundle.putString("phone",model.getPhone());
                        String str=holder.daysOfWeek.getText().toString();
                        bundle.putString("credits",Integer.toString(model.getCredits()));
                        bundle.putString("daysOfWeek",str);
                        bundle.putInt("rem_class",model.getRem_classes());
                        Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.student_info,bundle);
                    }
                });
            }

            @NonNull
            @Override
            public Student_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.student_card,parent,false);
                return new Student_holder(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        fab=root.findViewById(R.id.manageStudentsFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Pressed", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.add_student);
            }
        });
        return root;
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    public String getInitials(String name){
        String name_arr[]=name.split(" ");
        String letter=""+name_arr[0].charAt(0);
        if(name_arr.length>1){
            letter=letter+name_arr[name_arr.length-1].charAt(0);
        }
        return letter;
    }
}