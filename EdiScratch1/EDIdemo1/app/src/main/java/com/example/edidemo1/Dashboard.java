package com.example.edidemo1;

import androidx.appcompat.app.AppCompatActivity;

import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    Button button;
    TextView textView;
    LocationManager locationManager;
    TextInputLayout textInputLayout;
    AutoCompleteTextView autoCompleteTextView_admin;
    ArrayList<String> arrayList_administrative_sector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        textInputLayout = (TextInputLayout) findViewById(R.id.administrative_sector_box);
        AutoCompleteTextView autoCompleteTextView_admin = (AutoCompleteTextView) findViewById(R.id.txt_administrative_sector);

        arrayList_administrative_sector = new ArrayList<>();
        arrayList_administrative_sector.add("village");
        arrayList_administrative_sector.add("Agriculture");
        arrayList_administrative_sector.add("Electricity");
        arrayList_administrative_sector.add("Public Interest");


        ArrayAdapter arrayAdapter_adminstrativesector = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_layout, arrayList_administrative_sector);
        autoCompleteTextView_admin.setAdapter(arrayAdapter_adminstrativesector);

        autoCompleteTextView_admin.setThreshold(1);



    }
}
