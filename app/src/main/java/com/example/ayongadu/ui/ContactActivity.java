package com.example.ayongadu.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.ayongadu.R;
import com.example.ayongadu.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {
    private ContactAdapter adapter;
    private ArrayList<Contact> contactList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ImageView btnBack = findViewById(R.id.img_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        contactList.add(new Contact("Pak Tono", "Sopir Ambulance", "0876654534111"));
        contactList.add(new Contact("Bu Bintu", "Help Desk Kantor Balai Desa", "099878665456"));
        contactList.add(new Contact("Pak Bor", "Keamanan Desa", "099878665456"));
        contactList.add(new Contact("Pak Bor", "Keamanan Desa", "099878665456"));
        RecyclerView recyclerView = findViewById(R.id.recycler_contact);
        adapter = new ContactAdapter(contactList);
        recyclerView.setAdapter(adapter);
    }
}