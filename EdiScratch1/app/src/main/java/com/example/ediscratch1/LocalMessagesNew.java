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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class LocalMessagesNew extends AppCompatActivity {

    EditText LMtitle,LMdescription;
    Button sendLocalMsg;

    FirebaseFirestore db;
    DocumentReference documentReference;


    Calendar calendar;
    long timeMilli;

    ProgressDialog pd;

    private static final String CHANNEL_ID = "SakshamGramApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_messages_new);

        LMtitle = findViewById(R.id.Admin_Local_title);
        LMdescription = findViewById(R.id.Admin_Local_Description);
        sendLocalMsg = findViewById(R.id.btn_local_send_post);


        db = FirebaseFirestore.getInstance();


        pd = new ProgressDialog(this);

        documentReference = db.collection("admin_local_messages").document();

        sendLocalMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadLocalMessage();
            }
        });

    }

    private void uploadLocalMessage() {
        calendar = Calendar.getInstance();
        timeMilli = calendar.getTimeInMillis();
        calendar.setTimeInMillis(timeMilli);

        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        String Date_Time = dateFormat.format(calendar.getTime());

        String LocalTitle = LMtitle.getText().toString().trim();
        String LocalDescription = LMdescription.getText().toString().trim();
        if (!TextUtils.isEmpty(LocalTitle) & !TextUtils.isEmpty(LocalDescription))
        {
            pd.setMessage("Uploading...");
            pd.setCancelable(false);
            pd.show();

            HashMap<String, Object> localMsg = new HashMap<>();
            localMsg.put("Title",LocalTitle);
            localMsg.put("Description",LocalDescription);
            localMsg.put("Date_and_Time",Date_Time);
            localMsg.put("Id",documentReference.getId());

            documentReference.set(localMsg).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    pd.dismiss();
                    Toast.makeText(LocalMessagesNew.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
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

                    Intent intent = new Intent(LocalMessagesNew.this,AdminLocalMessages.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Empty Fields", Toast.LENGTH_SHORT).show();
            LMtitle.setError("Required");
            LMdescription.setError("Required");
        }
    }
}