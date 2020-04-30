package com.example.proximitysocialnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class AccountCreationActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText birthDate;
    private EditText password;
    private EditText confirmPassword;
    private Button confirmAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        // ************ link id to view **********************************
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        birthDate = (EditText) findViewById(R.id.birth_date);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        confirmAccount = (Button) findViewById(R.id.button_creation_account);
        // ****************************************************************

        confirmAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().equals(confirmPassword.getText().toString())){
                    MainActivity.profil.setName(name.getText().toString());
                    MainActivity.profil.setEmail(email.getText().toString());
                    MainActivity.profil.setBirthDate(birthDate.getText().toString());
                    MainActivity.profil.setPassword(password.getText().toString());

                    try {
                        FileOutputStream fos = openFileOutput("profil",Context.MODE_PRIVATE);
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        // write object to file
                        oos.writeObject(MainActivity.profil);
                        // closing resources
                        oos.close();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    startActivity(new Intent(AccountCreationActivity.this, MainActivity.class));
                }


            }
        });



    }
}
