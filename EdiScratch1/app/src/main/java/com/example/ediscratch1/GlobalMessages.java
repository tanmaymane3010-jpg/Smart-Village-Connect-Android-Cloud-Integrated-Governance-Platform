package com.example.ediscratch1;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GlobalMessages extends AppCompatActivity {

    EditText globalTitle, globalDesc;
    ImageView globalImageView;
    ImageButton addImageGlobal,viewfullimg,sendGlobalMsg;

    Calendar calendar;
    long timeMilli;

    private static final String CHANNEL_ID = "SakshamGramApp";

    private final static int REQUEST_CODE = 100;


    private Uri imageUri;

    ProgressDialog pd;

    UploadTask uploadTask1;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db;
    DocumentReference documentReference,docRef;
    FirebaseAuth fAuth;
    String Date_Time_Global;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_messages);

        globalTitle = findViewById(R.id.txt_GM_title);
        globalDesc = findViewById(R.id.txt_GM_discription);
        globalImageView = findViewById(R.id.global_message_image_imageview);
        addImageGlobal = findViewById(R.id.btn_add_image);
        viewfullimg = findViewById(R.id.btn_fullscreen);
        sendGlobalMsg = findViewById(R.id.btn_global_send);


        ActivityResultLauncher<String> galleryLauncher;

        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);



        documentReference = db.collection("admin_global_messages").document("AdminGlobal");
        //docRef = db.collection("user").document(user_id);

        storageReference = firebaseStorage.getInstance().getReference("admin_global_messages_photos");

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent()
                , new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        Picasso.get().load(uri).fit().into(globalImageView);
                        //photo.setImageURI(uri);
                        imageUri = uri;


                    }
                });

        addImageGlobal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLauncher.launch("image/*");
            }
        });

        viewfullimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sendGlobalMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadGlobalMsg();
            }
        });
        
    }

    private void uploadGlobalMsg() {

        calendar = Calendar.getInstance();
        timeMilli = calendar.getTimeInMillis();
        calendar.setTimeInMillis(timeMilli);

        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        Date_Time_Global = dateFormat.format(calendar.getTime());

        String Title = globalTitle.getText().toString().trim();
        String Description = globalDesc.getText().toString().trim();


        if(!TextUtils.isEmpty(Title) & !TextUtils.isEmpty(Description) & imageUri!=null)
        {
            pd.setMessage("Uploading Your Message");
            pd.show();

            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(imageUri));

            uploadTask1 = reference.putFile(imageUri);

            Task<Uri> urlTask2 = uploadTask1.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        Map<String, Object> globalMsgs = new HashMap<>();
                        globalMsgs.put("Title",Title);
                        globalMsgs.put("Description",Description);
                        globalMsgs.put("imageUrl",downloadUri.toString());
                        globalMsgs.put("Date_and_Time",Date_Time_Global);

                        documentReference.set(globalMsgs).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                pd.dismiss();
                                Toast.makeText(GlobalMessages.this, "Occasional Message Sent Successfully", Toast.LENGTH_SHORT).show();

                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                {
                                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"Saksham Gram", NotificationManager.IMPORTANCE_HIGH);
                                    channel.setDescription("Occasional Message Sent Successfully");
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
                                mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("Occasional Message Sent Successfully"));
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

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            globalTitle.setError("Required");
            globalDesc.setError("Required");
        }
    }

    private String getFileExt(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if(documentSnapshot.exists())
//                {
//                    String Title = documentSnapshot.getString("Title");
//                    String Description = documentSnapshot.getString("Description");
//                    String imageUrl = documentSnapshot.getString("imageUrl");
//
//                    Picasso.get().load(imageUrl).fit().into(globalImageView);
//                    globalTitle.setText(Title);
//                    globalDesc.setText(Description);
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


}