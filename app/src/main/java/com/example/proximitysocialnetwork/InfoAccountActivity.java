package com.example.proximitysocialnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class InfoAccountActivity extends AppCompatActivity {

    private TextView name;
    private TextView email;
    private TextView date;
    private TextView password;
    private TextView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_account);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        date = findViewById(R.id.birthdate);
        password = findViewById(R.id.password);
        profileImage = findViewById(R.id.textUriProfilePic);

        name.setText(MainActivity.profil.getName());
        email.setText(MainActivity.profil.getEmail());
        date.setText(MainActivity.profil.getBirthDate());
        password.setText(MainActivity.profil.getPassword());
        profileImage.setText(MainActivity.profil.getProfileImage());
    }
}
