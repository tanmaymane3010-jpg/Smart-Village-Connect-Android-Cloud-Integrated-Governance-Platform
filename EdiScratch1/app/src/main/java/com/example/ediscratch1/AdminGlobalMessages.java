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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class AdminGlobalMessages extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private Button new_GlobalMsg;
    private RecyclerView globalMsgRecyclerView;
    private FirestoreRecyclerAdapter globalMsgAdapter;

    FirebaseFirestore db;
    ProgressDialog pd;

    private static final String CHANNEL_ID = "SakshamGramApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_global_messages);

        firebaseFirestore = FirebaseFirestore.getInstance();
        new_GlobalMsg = findViewById(R.id.GlobalMsg);
        globalMsgRecyclerView = findViewById(R.id.recycler_view_GlobalMsg);

        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);

        Query globalMsg = firebaseFirestore.collection("admin_global_messages").orderBy("Date_and_Time");

        FirestoreRecyclerOptions<GlobalMessage> global_options = new FirestoreRecyclerOptions.Builder<GlobalMessage>()
                .setQuery(globalMsg,GlobalMessage.class)
                .build();

        globalMsgAdapter = new FirestoreRecyclerAdapter<GlobalMessage, GlobalMessageViewHolder>(global_options) {
            @NonNull
            @Override
            public GlobalMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.global_message_card,parent,false);
                return new GlobalMessageViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull GlobalMessageViewHolder holder, int position, @NonNull GlobalMessage model) {
                holder.title.setText(model.getTitle());
                holder.description.setText(model.getDescription());
                holder.date_time.setText(model.getDate_and_Time());

                String Uid = model.getId();
                String url = model.getImageUrl();

                Picasso.get().load(url).fit().into(holder.imageview);

                holder.deletePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd.setMessage("Deleting..");
                        pd.setCancelable(false);
                        pd.show();

                        db.collection("admin_global_messages").document(Uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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

        new_GlobalMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminGlobalMessages.this,GlobalMessagesNew.class);
                startActivity(intent);
            }
        });

        globalMsgRecyclerView.setHasFixedSize(true);
        globalMsgRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        globalMsgRecyclerView.setAdapter(globalMsgAdapter);

    }

    private class GlobalMessageViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView description;
        TextView date_time;
        ImageView imageview;
        Button deletePost;


        public GlobalMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.Admin_Global_Title1);
            description = itemView.findViewById(R.id.Admin_Global_Description1);
            date_time = itemView.findViewById(R.id.Admin_Global_DateTime);
            imageview = itemView.findViewById(R.id.Admin_Global_ImageView1);
            deletePost = itemView.findViewById(R.id.Admin_Global_DeletePost);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        globalMsgAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        globalMsgAdapter.stopListening();
    }
}