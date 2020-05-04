package com.example.proximitysocialnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import static android.graphics.Bitmap.Config.RGB_565;

public class PersonDiscoveredActivity extends AppCompatActivity{

    private ImageView profilePicture;
    private ImageView acceptButton;
    private ImageView refuseButton;
    private String urlDownload;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_discovered);


        // ******** Initialisation ********* //
        profilePicture = findViewById(R.id.profile_picture);
        acceptButton = findViewById(R.id.accept_button);
        refuseButton = findViewById(R.id.refuse_button);
        name = findViewById(R.id.name);


        // ******* We are looking the oldest person discovered ******** //
        Profil profil = App.profilsDiscovered.get(0);
        urlDownload = "http://89.87.13.28:8800/database/proximity_social_network/images/" + profil.getProfileImage() +".jpg";
        name.setText(profil.getName());
        downloadProfileImage();

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.removeFirstProfilDiscovered(0);
                // ***** if there is no more profile to discover ***** //
                if(App.profilsDiscovered.isEmpty()){
                    startActivity(new Intent(PersonDiscoveredActivity.this, MainActivity.class));
                    MainActivity.UpdateNotifNumber();
                    finish();
                    NotificationManager nManager = ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));
                    nManager.cancel(2);
                }
                else{
                    nextProfil();
                }
            }
        });
        refuseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.removeFirstProfilDiscovered(0);


                // ***** if there is no more profile to discover ***** //
                if(App.profilsDiscovered.isEmpty()){
                    startActivity(new Intent(PersonDiscoveredActivity.this, MainActivity.class));
                    MainActivity.UpdateNotifNumber();
                    finish();
                    NotificationManager nManager = ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));
                    nManager.cancel(2);
                }
                else{
                   nextProfil();
                }
            }
        });




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.UpdateNotifNumber();
    }

    public void downloadProfileImage(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        ImageRequest request = new ImageRequest(urlDownload, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                profilePicture.setImageBitmap(response);
                profilePicture.setVisibility(View.VISIBLE);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PersonDiscoveredActivity.this, "Error while downloading image", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue.add(request);
    }

    public void nextProfil(){
        profilePicture.setVisibility(View.INVISIBLE);
        name.setText(App.profilsDiscovered.get(0).getName());
        urlDownload = "http://89.87.13.28:8800/database/proximity_social_network/images/" + App.profilsDiscovered.get(0).getProfileImage() +".jpg";
        downloadProfileImage();

    }

}
