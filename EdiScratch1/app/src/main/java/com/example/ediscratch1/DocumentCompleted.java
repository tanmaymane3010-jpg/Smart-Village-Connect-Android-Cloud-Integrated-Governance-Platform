package com.example.ediscratch1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DocumentCompleted extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView doc_completed_recyclerView;
    private FirestoreRecyclerAdapter doc_completed_adapter;

    FirebaseFirestore db;
    DocumentReference documentReference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_completed);

        firebaseFirestore = FirebaseFirestore.getInstance();
        doc_completed_recyclerView = findViewById(R.id.doc_completed_recycler_view);

        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);

        Query doc_query1 = firebaseFirestore.collection("user_document_requests").whereEqualTo("Resolved_Status","true");

        FirestoreRecyclerOptions<Helper> options_doc1 = new FirestoreRecyclerOptions.Builder<Helper>()
                .setQuery(doc_query1,Helper.class)
                .build();

        doc_completed_adapter = new FirestoreRecyclerAdapter<Helper, HelperCompleteViewHolder>(options_doc1) {
            @NonNull
            @Override
            public HelperCompleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requested_history_card,parent,false);
                return new HelperCompleteViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull HelperCompleteViewHolder holder, int position, @NonNull Helper model) {

                holder.name.setText(model.getName());
                holder.ward.setText(model.getWard());
                holder.mobileNo.setText(model.getMobile_Number());
                holder.date_and_time.setText(model.getDate_and_Time());
                holder.requested_doc.setText(model.getDocument_Requested());

                String user_id = model.getId();;

                documentReference = db.collection("user").document(user_id);

                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            String Address = documentSnapshot.getString("Address");

                            holder.address.setText(Address);
                        }
                        else
                        {
                            holder.address.setText("No Entered Address");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        doc_completed_recyclerView.setHasFixedSize(true);
        doc_completed_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        doc_completed_recyclerView.setAdapter(doc_completed_adapter);
    }

    private class HelperCompleteViewHolder extends RecyclerView.ViewHolder{

        private TextView address;
        private TextView date_and_time;
        private TextView requested_doc;
        private TextView mobileNo;
        private TextView name;
        private TextView ward;

        public HelperCompleteViewHolder(@NonNull View itemView) {
            super(itemView);

            address = itemView.findViewById(R.id.input_address1);
            date_and_time = itemView.findViewById(R.id.txt_date_time1);
            requested_doc = itemView.findViewById(R.id.input_documents1);
            mobileNo = itemView.findViewById(R.id.input_phone1);
            name= itemView.findViewById(R.id.txt_name1);
            ward = itemView.findViewById(R.id.input_ward1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        doc_completed_adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        doc_completed_adapter.stopListening();
    }
}