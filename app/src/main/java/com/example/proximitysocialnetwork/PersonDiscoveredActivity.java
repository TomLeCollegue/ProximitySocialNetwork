package com.example.proximitysocialnetwork;

import androidx.appcompat.app.AppCompatActivity;

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

        profilePicture = findViewById(R.id.profile_picture);
        acceptButton = findViewById(R.id.accept_button);
        refuseButton = findViewById(R.id.refuse_button);
        name = findViewById(R.id.name);

        MainActivity.net.setOnViewDiscovering(true);
        Profil profil = MainActivity.net.getProfilsDiscovered().get(0);

        urlDownload = "http://89.87.13.28:8800/database/proximity_social_network/images/" + profil.getProfileImage() +".jpg";
        name.setText(profil.getName());

        downloadProfileImage();




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.net.setOnViewDiscovering(false);
    }

    public void downloadProfileImage(){

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        ImageRequest request = new ImageRequest(urlDownload, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                profilePicture.setImageBitmap(response);
                profilePicture.setVisibility(View.VISIBLE);
                //Toast.makeText(MainActivity.this, "Profile Image Downloaded Successfully", Toast.LENGTH_LONG).show();
            }
        }, 0, 0, ImageView.ScaleType.CENTER, RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressDownload.setVisibility(View.GONE);
                //Toast.makeText(PersonDiscoveredActivity.this, "Error while downloading image", Toast.LENGTH_LONG).show();
            }
        }
        );
        requestQueue.add(request);
    }

}