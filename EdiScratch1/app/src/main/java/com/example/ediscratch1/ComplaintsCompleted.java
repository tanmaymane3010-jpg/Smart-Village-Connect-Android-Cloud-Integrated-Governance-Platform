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
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class ComplaintsCompleted extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView3;
    private FirestoreRecyclerAdapter adapter_completed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints_completed);

        recyclerView3 = findViewById(R.id.recycler_view_3);
        firebaseFirestore = FirebaseFirestore.getInstance();

        Query query2 = firebaseFirestore.collection("user_complaints").whereEqualTo("Resolved_Status","true");

        FirestoreRecyclerOptions<ProductsModel> options2 = new FirestoreRecyclerOptions.Builder<ProductsModel>().setQuery(query2,ProductsModel.class).build();

        adapter_completed = new FirestoreRecyclerAdapter<ProductsModel, ProductModelCompleteViewHolder>(options2) {
            @NonNull
            @Override
            public ProductModelCompleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.complaint_completed_card,parent,false);
                return new ProductModelCompleteViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductModelCompleteViewHolder holder, int position, @NonNull ProductsModel model) {

                holder.address.setText(model.getAddress());
                holder.date_and_time.setText(model.getDate_and_Time());
                holder.description.setText((model.getDescription()));
                holder.mobileNo.setText(model.getMobileNo());
                holder.name.setText(model.getName());
                holder.ward.setText(model.getWard());

                String url = model.getImageUrl();
                Picasso.get().load(url).fit().into(holder.imageView);

            }
        };
        recyclerView3.setHasFixedSize(true);
        recyclerView3.setLayoutManager(new LinearLayoutManager(this));
        recyclerView3.setAdapter(adapter_completed);

    }

    private class ProductModelCompleteViewHolder extends RecyclerView.ViewHolder {
        private TextView address;
        private TextView date_and_time;
        private TextView description;
        private TextView mobileNo;
        private TextView name;
        private TextView ward;
        private ImageView imageView;

        public ProductModelCompleteViewHolder(@NonNull View itemView) {
            super(itemView);

            address = itemView.findViewById(R.id.txt_location2);
            date_and_time = itemView.findViewById(R.id.txt_date_time2);
            description = itemView.findViewById(R.id.Complainant_description2);
            mobileNo = itemView.findViewById(R.id.input_phone2);
            name= itemView.findViewById(R.id.txt_name2);
            ward = itemView.findViewById(R.id.input_ward2);
            imageView = itemView.findViewById(R.id.complaints_image_imageview2);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter_completed.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter_completed.stopListening();
    }
}