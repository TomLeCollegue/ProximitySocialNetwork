package com.example.proximitysocialnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Bitmap.Config.RGB_565;

public class PersonDiscoveredActivity extends AppCompatActivity{

    private ImageView profilePicture;
    private ImageView acceptButton;
    private ImageView refuseButton;
    private String urlDownload;
    private TextView name;
    SessionManager sessionManager;
    private String decisionString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_discovered);


        // ******** Initialisation ********* //
        profilePicture = findViewById(R.id.profile_picture);
        acceptButton = findViewById(R.id.accept_button);
        refuseButton = findViewById(R.id.refuse_button);
        name = findViewById(R.id.name);
        sessionManager = new SessionManager(this);


        // ******* We are looking the oldest person discovered ******** //
        Profil profil = App.profilsDiscovered.get(0);
        urlDownload = "http://89.87.13.28:8800/database/proximity_social_network/images/" + profil.getProfileImage() +".jpg";
        name.setText(profil.getName());
        downloadProfileImage();

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptRejectProfiles(App.profilsDiscovered.get(0).getEmail(), 1);
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
                AcceptRejectProfiles(App.profilsDiscovered.get(0).getEmail(), 2);
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
        startActivity(new Intent(PersonDiscoveredActivity.this, MainActivity.class));
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


    public void AcceptRejectProfiles(final String email, int decision){

        decisionString = String.valueOf(decision);
        String url = "http://89.87.13.28:8800/database/proximity_social_network/php-request/acceptReject.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("login");

                    if (success.equals("1")){
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                        }
                    }
                    else{
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error :" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("decision", decisionString);
                params.put("personal_email", sessionManager.getUserDetail().get(sessionManager.EMAIL));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
