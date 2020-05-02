package com.example.proximitysocialnetwork;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.example.proximitysocialnetwork.Profil;

public class MainActivity extends AppCompatActivity {

    public static Profil profil;

    public static NetworkHelper net;
    private Button infoAccount;
    private Button searchPeople;
    private Button sendProfil;
    public static TextView clientCo;

    private Button logout;
    private TextView name;
    private TextView email;
    private String mName;
    private String mEmail;

    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        sessionManager = new SessionManager(this);


        infoAccount = (Button) findViewById(R.id.info_compte);
        searchPeople = (Button) findViewById(R.id.search_people);
        sendProfil = (Button) findViewById(R.id.send_account);
        clientCo = (TextView) findViewById(R.id.client_co);
        logout = (Button) findViewById(R.id.logout);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);

        sessionManager.checkLoggin();
        HashMap<String,String > user = sessionManager.getUserDetail();
        mName = user.get(sessionManager.NAME);
        mEmail = user.get(sessionManager.EMAIL);

        if (net == null) {
            net = new NetworkHelper(this, mEmail);
        }

        name.setText(mName);
        email.setText(mEmail);



        // Intent to activities

        infoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InfoAccountActivity.class));
            }
        });


        searchPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                net.SeachPeople();
            }
        });

        sendProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    net.StopAll();
                    net = new NetworkHelper(getApplicationContext(), mEmail);
                    clientCo.setText("Non connecté");
                }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
            }
        });
    }



        @Override
        protected void onStart()
        {
            super.onStart();
            if (!hasPermissions(this, NetworkHelper.getRequiredPermissions())) {
                requestPermissions(NetworkHelper.getRequiredPermissions(), net.getRequestCodeRequiredPermissions());
            }
        }

        /**
         * Check permissions status for the application
         * @param context
         * @param permissions
         * @return boolean
         */
        private static boolean hasPermissions(Context context, String... permissions) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }

    /**
     * Request needed permissions to user through UI
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != NetworkHelper.getRequestCodeRequiredPermissions()) {
            return;
        }

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this,"Permissions manquantes", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
        recreate();
    }





    //Recup profil saved on launch
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
