package com.example.sharadasangeethashaale.ui.payments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharadasangeethashaale.R;
import com.example.sharadasangeethashaale.Student_holder;
import com.example.sharadasangeethashaale.Student_pojo;
import com.example.sharadasangeethashaale.payments_holder;
import com.example.sharadasangeethashaale.payments_pojo;
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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class paymentsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;
    int CLASS_FEES=-1;
    FloatingActionButton addPayment;
    RecyclerView recView;
    Dialog dialog;
    FirebaseFirestore db;
    List<Student_pojo> stu;
    FirestoreRecyclerAdapter<payments_pojo, payments_holder> adapter;
    private List<String> addstuspinneritems;
    Student_pojo student;
    String name,amt,payment_id;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_payments, container, false);
        db=FirebaseFirestore.getInstance();
        recView=root.findViewById(R.id.recentPaymentsRecyclerView);
        recView.setHasFixedSize(true);
        recView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dialog=new Dialog(getContext());
        addstuspinneritems=new ArrayList<String>();
        addPayment=root.findViewById(R.id.addPayment);
        addPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.add_payment);
                final Spinner studentSpinner=dialog.findViewById(R.id.nameSpinner);
                db.collection("students").whereEqualTo("status","active").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                stu=queryDocumentSnapshots.toObjects(Student_pojo.class);
                                addstuspinneritems.clear();
                                for(Student_pojo s:stu){
                                    {
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

                Button addPayment=dialog.findViewById(R.id.addPayment);
                final Spinner nameSpinner=dialog.findViewById(R.id.nameSpinner);
                final EditText amount=dialog.findViewById(R.id.amount);
                addPayment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        amt=amount.getText().toString();
                        String time = Long.toString(System.currentTimeMillis());
                        String msg=time+amt;
                        try {
                             payment_id=toHexString(getSHA(msg)).substring(0, 6).toUpperCase();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        if(amt.equals(null) || amt.equals("")){
                            amount.setError("Amount is required!");
                        }
                        else{
                            name=nameSpinner.getSelectedItem().toString();
                            db.collection("students").whereEqualTo("name",name).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            student=queryDocumentSnapshots.toObjects(Student_pojo.class).get(0);
                                            payments_pojo payment=new payments_pojo(student.getPhone(),name,new Date(),amt);
                                            db.collection("payments").document(payment_id).set(payment)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getActivity(),"Payment added successfully",Toast.LENGTH_SHORT);
                                                            CLASS_FEES=student.getClassFees();
                                                            int amt_int=Integer.parseInt(amt);
                                                            dialog.dismiss();
                                                            int credits=student.getCredits();
                                                            int no_of_classes=(credits+amt_int)/CLASS_FEES;
                                                            credits=(credits+amt_int)-(no_of_classes*CLASS_FEES);
                                                            student.setRem_classes(student.getRem_classes()+no_of_classes);
                                                            student.setCredits(credits);
                                                            db.collection("students").document(student.getPhone()).set(student)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
                                                                            dialog.dismiss();
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(getActivity(), "Data failed to update", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getActivity(), "Failed to add payment", Toast.LENGTH_SHORT).show();
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
                });
                dialog.show();
            }
        });

        Query query=db.collection("payments").orderBy("date");
        FirestoreRecyclerOptions<payments_pojo> options=new FirestoreRecyclerOptions.Builder<payments_pojo>()
                .setQuery(query,payments_pojo.class)
                .build();
        adapter=new FirestoreRecyclerAdapter<payments_pojo, payments_holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull payments_holder holder, int position, @NonNull payments_pojo model) {
                holder.name.setText(model.getName());
                SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
                holder.date.setText(sdf.format(model.getDate()));
                holder.amount.setText(model.getAmount());
                holder.paymentId.setText(model.getPayment_id());
            }

            @NonNull
            @Override
            public payments_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.payments_layout,parent,false);
                return new payments_holder(v);
            }
        };
        adapter.startListening();
        recView.setAdapter(adapter);

        return root;
    }
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }
}