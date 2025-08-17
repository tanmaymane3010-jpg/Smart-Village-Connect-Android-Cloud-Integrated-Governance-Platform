package com.example.ediscratch1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView1;
    private FirestoreRecyclerAdapter adapter_new;

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String CHANNEL_ID = "SakshamGramApp";

    FirebaseFirestore db;
    DocumentReference documentReference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView1 = findViewById(R.id.recycler_view_1);
        firebaseFirestore = FirebaseFirestore.getInstance();

        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);

        Query query = firebaseFirestore.collection("user_complaints").whereEqualTo("Resolved_Status","false");

        FirestoreRecyclerOptions<ProductsModel> options = new FirestoreRecyclerOptions.Builder<ProductsModel>()
                .setQuery(query,ProductsModel.class)
                .build();

        adapter_new = new FirestoreRecyclerAdapter<ProductsModel, ProductViewHolder>(options) {
            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.complaint_card_new,parent,false);
                return new ProductViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull ProductsModel model) {

                holder.address.setText(model.getAddress());
                holder.date_and_time.setText(model.getDate_and_Time());
                holder.description.setText((model.getDescription()));
                holder.mobileNo.setText(model.getMobileNo());
                holder.name.setText(model.getName());
                holder.ward.setText(model.getWard());

                String url = model.getImageUrl();
                String videoUrl =model.getVideoUrl();

                Picasso.get().load(url).fit().into(holder.imageView);

                String User_Id = model.getId();

                holder.imageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (result == PackageManager.PERMISSION_GRANTED)
                        {
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            String title= URLUtil.guessFileName(url,null,null);
                            request.setTitle(title);
                            request.setDescription("Saksham Gram Complaints");
                            String cookie = CookieManager.getInstance().getCookie(url);
                            request.addRequestHeader("cookie",cookie);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,title);

                            DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                            downloadManager.enqueue(request);

                            Toast.makeText(getApplicationContext(), "Downloading Started...", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            requestPermission();
                        }
                    }
                });

                holder.working.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (result == PackageManager.PERMISSION_GRANTED)
                        {
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(videoUrl));
                            String title= URLUtil.guessFileName(videoUrl,null,null);
                            request.setTitle(title);
                            request.setDescription("Downloading Please Wait....");
                            String cookie = CookieManager.getInstance().getCookie(videoUrl);
                            request.addRequestHeader("cookie",cookie);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES,title);

                            DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                            downloadManager.enqueue(request);

                            Toast.makeText(getApplicationContext(), "Downloading Started...", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            requestPermission();
                        }
                    }
                });

                holder.complete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd.setMessage("Updating the Status...");
                        pd.show();

                        documentReference = db.collection("user_complaints").document(User_Id);
                        documentReference.update("Resolved_Status","scrutinized").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "Status Updated Successfully", Toast.LENGTH_SHORT).show();
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                {
                                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"Saksham Gram", NotificationManager.IMPORTANCE_HIGH);
                                    channel.setDescription("Status Updated Successfully");
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
                                mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("Status Updated Successfully"));
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

        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.setAdapter(adapter_new);

    }

    private class ProductViewHolder extends RecyclerView.ViewHolder {

        private TextView address;
        private TextView date_and_time;
        private TextView description;
        private TextView mobileNo;
        private TextView name;
        private TextView ward;
        private ImageView imageView;
        private Button imageBtn;
        private Button working;
        private Button complete;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            address = itemView.findViewById(R.id.txt_location);
            date_and_time = itemView.findViewById(R.id.txt_date_time);
            description = itemView.findViewById(R.id.Complainant_description);
            mobileNo = itemView.findViewById(R.id.input_phone);
            name= itemView.findViewById(R.id.txt_name);
            ward = itemView.findViewById(R.id.input_ward);
            imageView = itemView.findViewById(R.id.complaints_image_imageview);
            imageBtn = itemView.findViewById(R.id.video_download_btn);
            working = itemView.findViewById(R.id.btn_inprocess);
            complete = itemView.findViewById(R.id.btn_complete);


        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter_new.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter_new.stopListening();
    }
}