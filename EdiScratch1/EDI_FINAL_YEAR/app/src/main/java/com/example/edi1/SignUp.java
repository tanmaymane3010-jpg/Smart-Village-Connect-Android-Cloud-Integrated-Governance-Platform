package com.example.edi1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    TextInputLayout txt_name,txt_email,txt_password,txt_confirm_password, ward_box, village_box, block_box;
    AutoCompleteTextView blocks,villages,wards;
    private ArrayAdapter<CharSequence> blockAdapter;
    private ArrayAdapter<CharSequence> villageAdapter;

    Button registerBtn,alreadylgnBtn;
    EditText txtName, txtEmail,txtpassword,txtconfirmpass;
    FirebaseAuth fAuth;
    DatabaseReference mRootRef;
    ProgressDialog pd;


    private String selectedBlock, selectedVillage;

    ArrayList<String> arrayList_ward, array_sangli_blocks, array_default_blocks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtName = findViewById(R.id.txt_name);
        txtEmail = findViewById(R.id.txt_email);
        txtpassword = findViewById(R.id.txt_password);
        txtconfirmpass = findViewById(R.id.txt_confirm_password);
        registerBtn = findViewById(R.id.register_button);
        alreadylgnBtn = findViewById(R.id.go_to_login);

        pd = new ProgressDialog(this);

        fAuth = FirebaseAuth.getInstance();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        ward_box = (TextInputLayout) findViewById(R.id.ward_box);
        AutoCompleteTextView wards = (AutoCompleteTextView) findViewById(R.id.txt_ward);
        arrayList_ward = new ArrayList<>();
        arrayList_ward.add("1");
        arrayList_ward.add("2");
        arrayList_ward.add("3");
        arrayList_ward.add("4");
        arrayList_ward.add("5");

        ArrayAdapter<String> arrayAdapter_ward = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_layout, arrayList_ward);
        wards.setAdapter(arrayAdapter_ward);

        wards.setThreshold(1);


        block_box = (TextInputLayout) findViewById(R.id.block_box);
        AutoCompleteTextView blocks = (AutoCompleteTextView) findViewById(R.id.txt_block);
        ArrayAdapter<CharSequence> blockAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_sangli_blocks, R.layout.spinner_layout);

        blockAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        blocks.setAdapter(blockAdapter);

        blocks.setThreshold(1);


        village_box = (TextInputLayout) findViewById(R.id.village_box);
        AutoCompleteTextView villages = (AutoCompleteTextView) findViewById(R.id.txt_village);
        ArrayAdapter<CharSequence> villageAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_All_villages, R.layout.spinner_layout_villages);

        villageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        villages.setAdapter(villageAdapter);

        villages.setThreshold(1);

        alreadylgnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this,SignIn.class));


            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=txtName.getText().toString();
                String email=txtEmail.getText().toString();
                String password=txtpassword.getText().toString();
                String confirmpassword=txtconfirmpass.getText().toString();
                String Ward= wards.getText().toString();
                String Block = blocks.getText().toString();
                String Village = villages.getText().toString();

                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmpassword)
                || TextUtils.isEmpty(Ward) || TextUtils.isEmpty(Block) || TextUtils.isEmpty(Village))
                {
                    Toast.makeText(SignUp.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<=6)
                {
                    txtpassword.setError("Password too short");
                }
                else if(!password.equals(confirmpassword))
                {
                    txtpassword.setError("Match Password Correctly");

                }
                else
                {
                    registerUser(name,email,password,Ward,Block,Village);

                }

            }
        });




    }

    private void registerUser(String Name, String Email, String Password, String ward, String block, String village) {
        pd.setMessage("Please Wait!");
        pd.show();

        fAuth.createUserWithEmailAndPassword(Email,Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String, Object>map = new HashMap<>();
                map.put("name",Name);
                map.put("email",Email);
                map.put("ward",ward);
                map.put("Block",block);
                map.put("Village",village);
                map.put("id",fAuth.getCurrentUser().getUid());

                mRootRef.child("Users").child(fAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            pd.dismiss();
                            Toast.makeText(SignUp.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent= new Intent(SignUp.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
}

