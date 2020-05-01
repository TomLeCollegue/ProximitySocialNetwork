package com.example.proximitysocialnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Bitmap.Config.RGB_565;

public class InfoAccountActivity extends AppCompatActivity {

    private static final String TAG = "test" ;
    private TextView name;
    private TextView email;
    private TextView date;
    private TextView password;
    private ImageView profileImage;
    private ProgressBar progressDownload;
    Context mContext;
    String urlDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_account);

        mContext = getApplicationContext();

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        date = findViewById(R.id.birthdate);
        password = findViewById(R.id.password);
        profileImage = findViewById(R.id.profilePic);
        progressDownload = findViewById(R.id.progressDownload);


        name.setText(MainActivity.profil.getName());
        email.setText(MainActivity.profil.getEmail());
        date.setText(MainActivity.profil.getBirthDate());
        password.setText(MainActivity.profil.getPassword());



        profileImageText.setText(MainActivity.profil.getProfileImage());



        profileImage.setVisibility(View.GONE);
        progressDownload.setVisibility(View.VISIBLE);

        urlDownload = "http://89.87.13.28:8800/database/proximity_social_network/images/profile_pic_"+MainActivity.profil.getEmail()+".jpg";
        Log.d(TAG, MainActivity.profil.getEmail());
        downloadProfileImage();
        /*File imgFile = new File(MainActivity.profil.getProfileImage());
        if (imgFile.exists())
        {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.toString());
            profileImage.setImageBitmap(myBitmap);
        }*/


    }

    public void downloadProfileImage(){
        Log.d(TAG, MainActivity.profil.getEmail());
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        ImageRequest request = new ImageRequest(urlDownload, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                profileImage.setImageBitmap(response);
                profileImage.setVisibility(View.VISIBLE);
                progressDownload.setVisibility(View.GONE);
                Toast.makeText(InfoAccountActivity.this, "Profile Image Downloaded Successfully", Toast.LENGTH_LONG).show();
            }
        }, 0, 0, ImageView.ScaleType.CENTER, RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDownload.setVisibility(View.GONE);
                Toast.makeText(InfoAccountActivity.this, "Error while downloading image", Toast.LENGTH_LONG).show();
            }
        }
        );
        requestQueue.add(request);
    }

    public void onBackPressed(){
        Intent intent = new Intent(InfoAccountActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    }
}
