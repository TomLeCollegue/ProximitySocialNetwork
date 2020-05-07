package com.example.proximitysocialnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MessagingActivity extends AppCompatActivity {

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        sessionManager = new SessionManager(this);
        



    }
}
