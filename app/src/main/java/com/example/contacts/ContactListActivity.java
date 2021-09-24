package com.example.contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.contacts.databinding.ActivityContactListBinding;


public class ContactListActivity extends AppCompatActivity {

    ActivityContactListBinding activityContactListBinding;

    //Adapter
    private ContactAdapter contactAdapter;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityContactListBinding = ActivityContactListBinding.inflate(getLayoutInflater());
        setContentView(activityContactListBinding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        if(databaseHelper.getAllContact().isEmpty()){
        activityContactListBinding.tvNoList.setVisibility(View.VISIBLE);
        activityContactListBinding.recyclerView.setVisibility(View.GONE);
        }else {
            activityContactListBinding.tvNoList.setVisibility(View.GONE);
            activityContactListBinding.recyclerView.setVisibility(View.VISIBLE);
        }

        //set adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        activityContactListBinding.recyclerView.setLayoutManager(linearLayoutManager);
        contactAdapter = new ContactAdapter(this, databaseHelper.getAllContact());
        activityContactListBinding.recyclerView.setAdapter(contactAdapter);

        activityContactListBinding.ivAdd.setOnClickListener(view -> {
            addClick();
        });
    }

    //AddFunction
    public void addClick() {
        Constants.EDIT_CLICKED = true;
        Intent intent = new Intent(this, AddContactActivity.class);
        startActivity(intent);
    }
}