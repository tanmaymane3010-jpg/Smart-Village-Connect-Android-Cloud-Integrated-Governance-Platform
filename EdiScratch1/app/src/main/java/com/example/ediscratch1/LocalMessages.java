package com.example.ediscratch1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class LocalMessages extends AppCompatActivity {


    EditText lm_title1, lm_text1,lm_title2,lm_text2;
    ImageButton sendMsg;
    LinearLayout linearLayout;

    Button editMsg,deleteMsg;
    FloatingActionButton addMsg;

    FirebaseFirestore db;
    DocumentReference documentReference,docRef;
    FirebaseAuth fAuth;

    String Date_Time;
    Calendar calendar;
    long timeMilli;

    ProgressDialog pd;

    private static final String CHANNEL_ID = "SakshamGramApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_messages);


        lm_title1 = findViewById(R.id.txt_LM_1_title);
        lm_text1 = findViewById(R.id.txt_LM_1_discription);
        lm_title2 = findViewById(R.id.txt_LM_2_title);
        lm_text2 = findViewById(R.id.txt_LM_2_discription);
        sendMsg = findViewById(R.id.send_btn_lm);
        linearLayout=findViewById(R.id.dailypost_layout_2);
        addMsg = findViewById(R.id.fab_add_new_post);
        editMsg = findViewById(R.id.btn_edit_post);
        deleteMsg = findViewById(R.id.btn_delete_post);



        fAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();


        pd = new ProgressDialog(this);

        documentReference = db.collection("admin_local_messages").document("Admin1");
        docRef = db.collection("admin_local_messages").document("Admin2");

        editMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadLocalMsg();
                editMsg.setVisibility(View.GONE);
                lm_title1.setVisibility(View.GONE);
                lm_text1.setVisibility(View.GONE);

            }
        });

        addMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.VISIBLE);
                sendMsg.setVisibility(View.VISIBLE);
            }
        });

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadNewLocalMsg();
                lm_title1.setVisibility(View.VISIBLE);
                lm_text1.setVisibility(View.VISIBLE);
                sendMsg.setVisibility(View.VISIBLE);
                editMsg.setVisibility(View.VISIBLE);
                addMsg.setVisibility(View.GONE);
            }
        });

        deleteMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Deleting..");
                db.collection("admin_local_messages").document("Admin1").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        db.collection("admin_local_messages").document("Admin2").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
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

                                Intent intent = new Intent(LocalMessages.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });

    }

    private void uploadNewLocalMsg() {

        calendar = Calendar.getInstance();
        timeMilli = calendar.getTimeInMillis();
        calendar.setTimeInMillis(timeMilli);

        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        String New_Date_Time = dateFormat.format(calendar.getTime());

        String title1 = lm_title2.getText().toString().trim();
        String description1 = lm_text2.getText().toString().trim();

        if (!TextUtils.isEmpty(description1) & !TextUtils.isEmpty(title1))
        {
            pd.setMessage("Uploading...");
            pd.show();

            HashMap<String, Object> localMsgs = new HashMap<>();
            localMsgs.put("New_Title",title1);
            localMsgs.put("New_Description",description1);
            localMsgs.put("New_Date_and_Time",New_Date_Time);

            docRef.set(localMsgs).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    pd.dismiss();
                    Toast.makeText(LocalMessages.this, "Message Added Successfully", Toast.LENGTH_SHORT).show();

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"Saksham Gram", NotificationManager.IMPORTANCE_HIGH);
                        channel.setDescription("Message Added Successfully");
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
                    mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("Message Added Successfully"));
                    mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                    notificationManagerCompat.notify(999,mBuilder.build());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            lm_title1.setError("Cannot be Empty");
            lm_text1.setError("Cannot be Empty");
        }

    }

    private void uploadLocalMsg() {

        calendar = Calendar.getInstance();
        timeMilli = calendar.getTimeInMillis();
        calendar.setTimeInMillis(timeMilli);

        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        String Date_Time = dateFormat.format(calendar.getTime());

        String title = lm_title1.getText().toString().trim();
        String description = lm_text1.getText().toString().trim();

        if (!TextUtils.isEmpty(description) & !TextUtils.isEmpty(title))
        {
            pd.setMessage("Uploading...");
            pd.show();

            HashMap<String, Object> localMsgs = new HashMap<>();
            localMsgs.put("Title",title);
            localMsgs.put("Description",description);
            localMsgs.put("Date_and_Time",Date_Time);

            documentReference.set(localMsgs).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    pd.dismiss();
                    Toast.makeText(LocalMessages.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                    addMsg.setVisibility(View.VISIBLE);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"Saksham Gram", NotificationManager.IMPORTANCE_HIGH);
                        channel.setDescription("Message sent successfully");
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
                    mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("Message sent successfully"));
                    mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                    notificationManagerCompat.notify(999,mBuilder.build());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
        else {
            lm_title1.setError("Cannot be Empty");
            lm_text1.setError("Cannot be Empty");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
//                    addMsg.setVisibility(View.VISIBLE);
                    String title1 = documentSnapshot.getString("Title");
                    String description1 = documentSnapshot.getString("Description");

                    lm_title1.setText(title1);
                    lm_text1.setText(description1);
                    addMsg.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    addMsg.setVisibility(View.GONE);
                    String title1 = documentSnapshot.getString("New_Title");
                    String description1 = documentSnapshot.getString("New_Description");

                    lm_title2.setText(title1);
                    lm_text2.setText(description1);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}