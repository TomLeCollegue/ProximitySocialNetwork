package com.example.proximitysocialnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {

    public static Profil profil;
    private Button createAccount;
    private Button infoAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadProfil();

        if (profil == null) {
            profil = new Profil();
        }

        createAccount = (Button) findViewById(R.id.create_account);
        infoAccount = (Button) findViewById(R.id.info_compte);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AccountCreationActivity.class));
            }
        });
        infoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InfoAccountActivity.class));
            }
        });

    }
    public void loadProfil(){
        try {
            FileInputStream is = openFileInput("profil");
            ObjectInputStream ois = new ObjectInputStream(is);
            profil = (Profil) ois.readObject();
            ois.close();
            is.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
