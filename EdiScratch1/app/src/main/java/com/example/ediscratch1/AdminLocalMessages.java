package com.example.ediscratch1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AdminLocalMessages extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private Button new_localMsg;
    private RecyclerView localMsgRecyclerView;
    private FirestoreRecyclerAdapter localMsgAdapter;

    FirebaseFirestore db;
    ProgressDialog pd;

    private static final String CHANNEL_ID = "SakshamGramApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_local_messages);

        localMsgRecyclerView = findViewById(R.id.recycler_view_localMsg);
        firebaseFirestore = FirebaseFirestore.getInstance();
        new_localMsg = findViewById(R.id.LocalMsg);

        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);

        Query localMsg = firebaseFirestore.collection("admin_local_messages").orderBy("Date_and_Time");

        FirestoreRecyclerOptions<LocalMessage> localMsg_options = new FirestoreRecyclerOptions.Builder<LocalMessage>()
                .setQuery(localMsg,LocalMessage.class)
                .build();

        localMsgAdapter = new FirestoreRecyclerAdapter<LocalMessage, LocalMsgViewHolder>(localMsg_options) {
            @NonNull
            @Override
            public LocalMsgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.local_message_card,parent,false);
                return new LocalMsgViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull LocalMsgViewHolder holder, int position, @NonNull LocalMessage model) {

                holder.title.setText(model.getTitle());
                holder.description.setText(model.getDescription());
                holder.Date_Time.setText(model.getDate_and_Time());

                String Uid = model.getId();

                holder.deleteLocalMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd.setMessage("Deleting..");
                        pd.setCancelable(false);
                        pd.show();

                        db.collection("admin_local_messages").document(Uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_LONG).show();
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                {
                                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"Saksham Gram", NotificationManager.IMPORTANCE_HIGH);
                                    channel.setDescription("Post Deleted Successfully");
                                    channel.enableLights(true);
                                    channel.enableVibration(true);


                                    NotificationManager manager = getSystemService(NotificationManager.class);
                                    manager.createNotificationChannel(channel);

                                }
                                NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
                                mBuilder.setSmallIcon(R.drawable.notification_icon);
                                mBuilder.setContentTitle("Saksham Gram");
                                mBuilder.setContentText("Welcome");
                                mBuilder.setAutoCancel(true);
                                mBuilder.setColor(Color.BLUE);
                                mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("Post Deleted Successfully"));
                                mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                                notificationManagerCompat.notify(999,mBuilder.build());
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


        new_localMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminLocalMessages.this,LocalMessagesNew.class);
                startActivity(intent);
            }
        });

        localMsgRecyclerView.setHasFixedSize(true);
        localMsgRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        localMsgRecyclerView.setAdapter(localMsgAdapter);
    }

    private class LocalMsgViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView description;
        TextView Date_Time;
        Button deleteLocalMsg;

        public LocalMsgViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.Admin_Local_title1);
            description = itemView.findViewById(R.id.Admin_Local_Description1);
            Date_Time = itemView.findViewById(R.id.local_date_time);
            deleteLocalMsg = itemView.findViewById(R.id.btn_local_delete_post1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        localMsgAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        localMsgAdapter.stopListening();
    }
}