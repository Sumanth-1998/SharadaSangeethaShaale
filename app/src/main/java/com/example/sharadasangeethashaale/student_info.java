package com.example.sharadasangeethashaale;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharadasangeethashaale.recycleradapter.attendanceAdaper;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * A simple {@link Fragment} subclass.
 */
public class student_info extends Fragment {
    private static final long ROW_LIMITS = 15;
    TextView nameTextView,phoneTextView,daysOfWeek,rem_clas,nameLetter,credits;
    RecyclerView paymentRV,attRV;
    FirebaseFirestore db;
    FirestoreRecyclerAdapter<payments_pojo,student_payment_holder> paymentAdapter;
    RecyclerView.Adapter attendanceAdapter;
    String name;
    List<String> dates;
    ImageButton deleteButton;
    ImageButton paymentShare, attendanceShare;
    List<payments_pojo> paymentstList;


    List<attendance_pojo> attendanceList;
    public student_info() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        name=getArguments().getString("name");
        dates=new ArrayList<>();
        View root= inflater.inflate(R.layout.fragment_student_info, container, false);
        nameTextView=root.findViewById(R.id.nameTextView);
        phoneTextView=root.findViewById(R.id.phoneTextView);
        deleteButton=root.findViewById(R.id.deleteButton);
        daysOfWeek=root.findViewById(R.id.daysOfWeek);
        rem_clas=root.findViewById(R.id.rem_clas);
        nameLetter=root.findViewById(R.id.nameLetter);
        paymentShare = root.findViewById(R.id.paymentShare);
        attendanceShare = root.findViewById(R.id.attendanceShare);
        paymentstList = new ArrayList<>();
        db=FirebaseFirestore.getInstance();


        nameLetter.setText(getInitials(getArguments().getString("name")));
        nameTextView.setText(getArguments().getString("name"));
        phoneTextView.setText(getArguments().getString("phone"));

        phoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData( Uri.parse("tel:" + phoneTextView.getText().toString()) );
                callIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity( callIntent );
            }
        });


        daysOfWeek.setText(getArguments().getString("daysOfWeek"));
        rem_clas.setText(Integer.toString(getArguments().getInt("rem_class")));
        if(getArguments().getInt("rem_class")<=2){
            rem_clas.setTextColor(getResources().getColor(R.color.red));
        }
        credits=root.findViewById(R.id.credits);
        credits.setText(getArguments().getString("credits"));
        paymentRV=root.findViewById(R.id.studentPaymentRecyclerView);
        attRV=root.findViewById(R.id.studentAttendanceRecyclerView);
        paymentRV.setHasFixedSize(true);
        attRV.setHasFixedSize(true);
        paymentRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        attRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        Query paymentQuery=db.collection("payments").orderBy("date",Query.Direction.DESCENDING).whereEqualTo("name",getArguments().getString("name")).limit(ROW_LIMITS);
        FirestoreRecyclerOptions paymentOptions=new FirestoreRecyclerOptions.Builder<payments_pojo>()
                .setQuery(paymentQuery,payments_pojo.class)
                .build();
        paymentAdapter=new FirestoreRecyclerAdapter<payments_pojo, student_payment_holder>(paymentOptions) {
            @Override
            protected void onBindViewHolder(@NonNull student_payment_holder holder, int position, @NonNull payments_pojo model) {
                holder.paymentId.setText(model.getPayment_id());
                holder.amount.setText(model.getAmount());
                SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
                holder.date.setText(sdf.format(model.getDate()));
                paymentstList.add(model);
            }

            @NonNull
            @Override
            public student_payment_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.student_payment_layout,parent,false);
                return new student_payment_holder(v);
            }
        };
        paymentAdapter.startListening();
        paymentRV.setAdapter(paymentAdapter);
        Query attendanceQuery=db.collectionGroup("student").orderBy("classDate", Query.Direction.DESCENDING).whereEqualTo("name",name).whereEqualTo("attendance", "P  ").limit(ROW_LIMITS);
        Log.d("model",name);
        attendanceQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<DocumentSnapshot> documentSnapshots = null;
                if(!Objects.isNull(queryDocumentSnapshots)){
                    documentSnapshots = queryDocumentSnapshots.getDocuments();
                    attendanceList = documentSnapshots.stream()
                            .map(documentSnapshot -> documentSnapshot.toObject(attendance_pojo.class))
                            .collect(Collectors.toList());
                    attendanceAdapter = new attendanceAdaper(attendanceList);
                }

                attRV.setAdapter(attendanceAdapter);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure to delete the student?");
                builder.setTitle("Alert!");
                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                db.collection("students").document(getArguments().getString("phone")).update("status","inactive")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getActivity(), "Data deleted successfully", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.manageStudents);
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

        paymentShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String paymentDetails = formPaymentDetails(paymentstList);
                Intent sendPaymentIntent = new Intent(Intent.ACTION_SEND);
                sendPaymentIntent.putExtra(Intent.EXTRA_TEXT, paymentDetails);
                sendPaymentIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendPaymentIntent, "Send To....");
                startActivity(shareIntent);
            }
        });

        attendanceShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String attendanceDetails = formAttendanceDetails(paymentstList, attendanceList);
                Intent sendAttendanceDetailsIntent = new Intent(Intent.ACTION_SEND);
                sendAttendanceDetailsIntent.putExtra(Intent.EXTRA_TEXT, attendanceDetails);
                sendAttendanceDetailsIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendAttendanceDetailsIntent, "Send To....");
                startActivity(shareIntent);
            }
        });


        return root;
    }

    private String formAttendanceDetails(List<payments_pojo> paymentstList, List<attendance_pojo> attendanceList) {
        StringBuilder str = new StringBuilder("");
        payments_pojo payment = paymentstList.get(0);
        str.append("Last payment of *INR. "+payment.getAmount()
                  +"* received from *"+payment.getName()+"* on *"
                  +new SimpleDateFormat("dd/MM/yyyy").format(payment.getDate())).append("*\n\n");
        str.append("Please find below the dates when *"+payment.getName()+"* was *PRESENT*:\n");
        attendanceList.stream()
                      .forEach(attendance -> str.append(new SimpleDateFormat("dd/MM/yyyy").format(attendance.getClassDate())).append("\n"));
        return str.toString();
    }

    private String formPaymentDetails(List<payments_pojo> paymentstList) {
        StringBuilder str = new StringBuilder("");
        paymentstList.stream()
                        .forEach(payment -> str.append(payment.toString()).append("\n"));
        return str.toString();
    }

    public String getPresence(String attendance){
        switch (attendance){
            case "A  ":return "ABSENT";
            case "P  ":return  "PRESENT";
            case "C  ":return  "CANCELLED";
        }
        return "";
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
