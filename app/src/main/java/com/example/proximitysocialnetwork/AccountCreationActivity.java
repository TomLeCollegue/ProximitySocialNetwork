package com.example.proximitysocialnetwork;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AccountCreationActivity extends AppCompatActivity {

    //debug log
    private static final String TAG = "test";

    //View variable names
    private EditText name;
    private EditText email;
    private EditText birthDate;
    private EditText password;
    private EditText confirmPassword;
    private Button confirmAccount;
    private ImageView profileImage;

    //image view location on phone
    private Uri imageURI;
    // request code for image
    private static final int PICK_IMAGE = 100;

    Context mContext;
    //
    String urlUpload = "http://89.87.13.28:8800/database/proximity_social_network/php-request/upload_image.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        mContext = getApplicationContext();

        // ************ link id to view **********************************
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        birthDate = (EditText) findViewById(R.id.birth_date);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        confirmAccount = (Button) findViewById(R.id.button_creation_account);
        profileImage = (ImageView) findViewById(R.id.profile_image);
         // ****************************************************************

        confirmAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().equals(confirmPassword.getText().toString())){

                    MainActivity.profil.setName(name.getText().toString());
                    MainActivity.profil.setEmail(email.getText().toString());
                    MainActivity.profil.setBirthDate(birthDate.getText().toString());
                    MainActivity.profil.setPassword(password.getText().toString());

                    MainActivity.profil.setProfileImage(imageURI.toString());

                    //saveToInternalStorage();
                    saveToServer();
                    startActivity(new Intent(AccountCreationActivity.this, MainActivity.class));

                }


            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                profileImage.startAnimation(animation);

                openGallery();
            }
        });

    }
    //if you want to save your profile_image to your internal storage
    private void saveToInternalStorage(){
        OutputStream outputStream = null;
        BitmapDrawable drawable = (BitmapDrawable) profileImage.getDrawable();
        Bitmap bitmap = new ImageGestion().getBitmapFromDrawable(drawable);

        String filepath = mContext.getExternalFilesDir(null).getAbsolutePath();
        File dir = new File (filepath.replace("/files", "") + "/ProfileImages/");
        Log.d(TAG, filepath.replace("/files", ""));
        dir.mkdir();
        File file = new File (dir, "profile_pic_"+ name.getText().toString().replace(" ","") + ".jpg");
        Log.d("test", file.toString());
        try{
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        new ImageGestion().compressImageToJpeg(bitmap,35,outputStream);

        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MainActivity.profil.setProfileImage(file.toString());

        //Intent intent = new Intent(AccountCreationActivity.this, MainActivity.class);
        //startActivity(intent);


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
    }

    private void saveToServer(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(AccountCreationActivity.this, response, Toast.LENGTH_SHORT).show();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AccountCreationActivity.this, "error: "+ error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params = new HashMap<>();

                BitmapDrawable drawable = (BitmapDrawable) profileImage.getDrawable();

                Bitmap myImageBitmap = new ImageGestion().getBitmapFromDrawable(drawable);

                //encodes image to string from base 64
                String imageEncoded = new ImageGestion().imageToString(myImageBitmap);
                params.put("name_picture", "profile_pic_"+ email.getText().toString());
                params.put("profile_picture_test", imageEncoded);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    public void onBackPressed(){
        Intent intent = new Intent(AccountCreationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageURI = data.getData();
            profileImage.setImageURI(imageURI);
        }
    }
}
