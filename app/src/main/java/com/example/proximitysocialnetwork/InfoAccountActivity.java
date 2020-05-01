package com.example.proximitysocialnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class InfoAccountActivity extends AppCompatActivity {

    private TextView name;
    private TextView email;
    private TextView date;
    private TextView password;
    private TextView profileImageText;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_account);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        date = findViewById(R.id.birthdate);
        password = findViewById(R.id.password);
        profileImageText = findViewById(R.id.textUriProfilePic);
        profileImage = findViewById(R.id.profilePic);

        name.setText(MainActivity.profil.getName());
        email.setText(MainActivity.profil.getEmail());
        date.setText(MainActivity.profil.getBirthDate());
        password.setText(MainActivity.profil.getPassword());

        profileImageText.setText(MainActivity.profil.getProfileImage());

        File imgFile = new File(MainActivity.profil.getProfileImage());
        if (imgFile.exists())
        {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.toString());
            profileImage.setImageBitmap(myBitmap);
        }

    }
}
