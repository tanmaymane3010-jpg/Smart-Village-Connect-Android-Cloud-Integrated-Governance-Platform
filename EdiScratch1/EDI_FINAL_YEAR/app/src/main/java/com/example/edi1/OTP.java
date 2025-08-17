package com.example.edi1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTP extends AppCompatActivity {

    EditText phonenumber;
    EditText enterOTPfield;
    Button SendOTPBtn,verifyOTP,resendOTP;
    String userPhonenumber, verificationID;
    FirebaseAuth fAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    PhoneAuthProvider.ForceResendingToken token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        phonenumber = findViewById(R.id.txt_phone);
        SendOTPBtn = findViewById(R.id.txt_send_otp);

        enterOTPfield = findViewById(R.id.txt_otp);
        verifyOTP = findViewById(R.id.btn_Verify_OTP);
        resendOTP = findViewById(R.id.resendOTPBtn);

        fAuth=FirebaseAuth.getInstance();


        SendOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!phonenumber.getText().toString().isEmpty() && phonenumber.getText().toString().length()==10)
                {
                    userPhonenumber = "+91"+phonenumber.getText().toString();
                    verifyPhoneNumber(userPhonenumber);
                    Toast.makeText(OTP.this, "+91"+phonenumber.getText().toString(), Toast.LENGTH_SHORT).show();

                }
                else
                {
                    phonenumber.setError("Phone Number is Required");
                    return;
                }
            }
        });

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPhoneNumber(userPhonenumber);
                resendOTP.setEnabled(false);
            }
        });

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the OTP from user
                if(!enterOTPfield.getText().toString().isEmpty() && enterOTPfield.getText().toString().length()==6)
                {
                    PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificationID,enterOTPfield.getText().toString());
                    authenticateUser(credential);

                }
                else
                {
                    enterOTPfield.setError("Enter OTP First");
                    return;
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                authenticateUser(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(OTP.this, e.getMessage(), Toast.LENGTH_LONG).show();


            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                verificationID=s;
                token=forceResendingToken;

                SendOTPBtn.setVisibility(View.GONE);

                enterOTPfield.setVisibility(View.VISIBLE);
                verifyOTP.setVisibility(View.VISIBLE);
                resendOTP.setVisibility(View.VISIBLE);

                resendOTP.setEnabled(false);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                resendOTP.setEnabled(true);
            }
        };


    }

    public void verifyPhoneNumber(String Phonenum)
    {
        //send otp
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fAuth)
                .setActivity(this)
                .setPhoneNumber(Phonenum)
                .setTimeout(60L,TimeUnit.SECONDS)
                .setCallbacks(callbacks)
                .build();


        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    public void authenticateUser(PhoneAuthCredential credential)
    {
        fAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(OTP.this, "Successfully Authenticated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),SignUp.class));
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}