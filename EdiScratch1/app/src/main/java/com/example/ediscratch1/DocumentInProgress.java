package com.example.ediscratch1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class DocumentInProgress extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView doc_progress_recyclerView;
    private FirestoreRecyclerAdapter doc_progress_adapter;

    FirebaseFirestore db;
    DocumentReference documentReference;
    ProgressDialog pd;

    private static final String CHANNEL_ID = "SakshamGramApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_in_progress);

        firebaseFirestore = FirebaseFirestore.getInstance();
        doc_progress_recyclerView = findViewById(R.id.doc_progress_recycler_view);

        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);

        Query doc_query = firebaseFirestore.collection("user_document_requests").whereEqualTo("Resolved_Status", "false");

        FirestoreRecyclerOptions<Helper> options_doc = new FirestoreRecyclerOptions.Builder<Helper>()
                .setQuery(doc_query, Helper.class)
                .build();

        doc_progress_adapter = new FirestoreRecyclerAdapter<Helper, HelperViewModel>(options_doc) {
            @NonNull
            @Override
            public HelperViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requested_documents_card, parent, false);
                return new HelperViewModel(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull HelperViewModel holder, int position, @NonNull Helper model) {

                holder.name.setText(model.getName());
                holder.ward.setText(model.getWard());
                holder.mobileNo.setText(model.getMobile_Number());
                holder.date_and_time.setText(model.getDate_and_Time());
                holder.requested_doc.setText(model.getDocument_Requested());

                String user_id = model.getId();

                documentReference = db.collection("user").document(user_id);

                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.exists()) {
                            String Address = value.getString("Address");
                            if (!TextUtils.isEmpty(Address)) {
                                holder.address.setText(Address);
                            } else {
                                holder.address.setText("No Data Found");
                            }
                        } else {
                            holder.address.setText("No DATA Found");
                        }
                    }
                });

//                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if (documentSnapshot.exists()) {
//                            String Address = documentSnapshot.getString("Address");
//                            holder.address.setText(Address);
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });

                holder.doc_progress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd.setMessage("Updating the Status...");
                        pd.setCancelable(false);
                        pd.show();

                        documentReference = db.collection("user_document_requests").document(user_id);
                        documentReference.update("Resolved_Status", "true").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Saksham Gram", NotificationManager.IMPORTANCE_HIGH);
                                    channel.setDescription("Status Updated Successfully");
                                    channel.enableLights(true);
                                    channel.enableVibration(true);


                                    NotificationManager manager = getSystemService(NotificationManager.class);
                                    manager.createNotificationChannel(channel);

                                }
                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
                                mBuilder.setSmallIcon(R.drawable.notification_icon);
                                mBuilder.setContentTitle("Saksham Gram");
                                mBuilder.setContentText("Welcome");
                                mBuilder.setAutoCancel(true);
                                mBuilder.setColor(Color.BLUE);
                                mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("Status Updated Successfully"));
                                mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                                notificationManagerCompat.notify(999, mBuilder.build());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

        };

        doc_progress_recyclerView.setHasFixedSize(true);
        doc_progress_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        doc_progress_recyclerView.setAdapter(doc_progress_adapter);

    }

    private class HelperViewModel extends RecyclerView.ViewHolder {

        private TextView address;
        private TextView date_and_time;
        private TextView requested_doc;
        private TextView mobileNo;
        private TextView name;
        private TextView ward;
        private Button doc_progress;

        public HelperViewModel(@NonNull View itemView) {
            super(itemView);

            address = itemView.findViewById(R.id.doc_prog_txt_address);
            date_and_time = itemView.findViewById(R.id.doc_prog_txt_date_time);
            requested_doc = itemView.findViewById(R.id.doc_prog_txt_requested_doc);
            mobileNo = itemView.findViewById(R.id.doc_prog_txt_phone);
            name = itemView.findViewById(R.id.doc_prog_txt_name);
            ward = itemView.findViewById(R.id.doc_prog_txt_ward);
            doc_progress = itemView.findViewById(R.id.doc_prog_btn_completed);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        doc_progress_adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        doc_progress_adapter.stopListening();
    }
}