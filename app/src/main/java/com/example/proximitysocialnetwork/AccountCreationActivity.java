package com.example.proximitysocialnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    private ProgressBar loading;

    //image view location on phone
    private Uri imageURI;
    // request code for image
    private static final int PICK_IMAGE = 100;

    Boolean correctForm = true;


    Context mContext;
    //
    String urlUpload = "http://89.87.13.28:8800/database/proximity_social_network/php-request/upload_image.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        mContext = getApplicationContext();

        // ***** fail connection : anim textView **** //
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        birthDate = (EditText) findViewById(R.id.birth_date);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        confirmAccount = (Button) findViewById(R.id.button_creation_account);
        profileImage = (ImageView) findViewById(R.id.profile_image);
        loading = findViewById(R.id.progressBar2);

        // ***** Backup initial color TextView **** //
        final ColorStateList oldColors =  email.getTextColors();
         // ****************************************************************

        confirmAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.lefttoright);

                // ***** Check if the two password are the sames **** //
                if(password.getText().toString().equals(confirmPassword.getText().toString())){

                    // ***** init color TextView and Bool **** //
                    correctForm = true;
                    password.setTextColor(oldColors);
                    password.setBackgroundResource(R.drawable.plaintextstyle);
                    confirmPassword.setTextColor(oldColors);
                    confirmPassword.setBackgroundResource(R.drawable.plaintextstyle);

                    // ***** Init Regex **** //
                    String regexEmail = "^[A-Za-z0-9+_.-]+@(.+)$";
                    String regexUsername = "^([a-zA-Z]{2,}\\s[a-zA-z]{1,}'?-?[a-zA-Z]{2,}\\s?([a-zA-Z]{1,})?)";
                    String regexPassword = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$";
                    /*   Password matching expression. Password must be at least 8 characters,
                    and must include at least one upper case letter, one lower case letter, and one numeric digit or special character.  */
                    Pattern pattern = Pattern.compile(regexEmail);
                    Pattern patternUsername = Pattern.compile(regexUsername);
                    Pattern patternPassword = Pattern.compile(regexPassword);
                    Matcher matcherEmail = pattern.matcher(email.getText().toString().trim());
                    Matcher matcherUsername = patternUsername.matcher(name.getText().toString().trim());
                    Matcher matcherPassword = patternPassword.matcher(password.getText().toString().trim());

                    // ***** Recup infi Picture profile **** //
                    Bitmap bmapimage = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
                    Drawable crossImg = getResources().getDrawable(R.drawable.add_profil_img);
                    Bitmap bmapcross = ((BitmapDrawable) crossImg).getBitmap();

                    // ***** Check if you chose a pic **** //
                    if(bmapimage.sameAs(bmapcross)){
                        correctForm = false;
                        profileImage.startAnimation(animation);
                    }

                    // ***** Check Email **** //
                    if(!matcherEmail.find()) {
                        correctForm = false;

                        email.setBackgroundResource(R.drawable.plaintextstylered);
                        email.setTextColor(getResources().getColor(R.color.ColorRed));
                        email.startAnimation(animation);

                    }
                    else{
                        email.setTextColor(oldColors);
                        email.setBackgroundResource(R.drawable.plaintextstyle);

                    }

                    // ***** Check Name **** //
                    if(!matcherUsername.find()) {
                        //Toast.makeText(getApplicationContext(),"Entrez un Nom Valide", Toast.LENGTH_SHORT).show();
                        correctForm = false;
                        name.setBackgroundResource(R.drawable.plaintextstylered);
                        name.setTextColor(getResources().getColor(R.color.ColorRed));
                        name.startAnimation(animation);
                    }
                    else{
                        name.setTextColor(oldColors);
                        name.setBackgroundResource(R.drawable.plaintextstyle);
                    }

                    // ***** Check Password **** //
                    if(!matcherPassword.find()) {
                        Toast.makeText(getApplicationContext(),"Password must be at least 8 characters, " +
                                "and must include at least one upper case letter, one lower case letter, and one numeric digit or special character.", Toast.LENGTH_LONG).show();
                        correctForm = false;
                        password.setBackgroundResource(R.drawable.plaintextstylered);
                        password.setTextColor(getResources().getColor(R.color.ColorRed));
                        password.startAnimation(animation);
                        confirmPassword.setBackgroundResource(R.drawable.plaintextstylered);
                        confirmPassword.setTextColor(getResources().getColor(R.color.ColorRed));
                        confirmPassword.startAnimation(animation);
                    }
                    else {
                        password.setTextColor(oldColors);
                        password.setBackgroundResource(R.drawable.plaintextstyle);
                        confirmPassword.setTextColor(oldColors);
                        confirmPassword.setBackgroundResource(R.drawable.plaintextstyle);
                    }


                    // ***** If all form is good **** //
                    if(correctForm) {
                        // **** add to the bdd ****** //
                        loading.setVisibility(View.VISIBLE);
                        create_account();
                    }

                }
                else{

                    // ***** Password don't match **** //
                    password.setBackgroundResource(R.drawable.plaintextstylered);
                    password.setTextColor(getResources().getColor(R.color.ColorRed));
                    password.startAnimation(animation);
                    confirmPassword.setBackgroundResource(R.drawable.plaintextstylered);
                    confirmPassword.setTextColor(getResources().getColor(R.color.ColorRed));
                    confirmPassword.startAnimation(animation);
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

    private void saveToServer(){
        confirmAccount.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                startActivity(new Intent(AccountCreationActivity.this, LoginActivity.class));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageURI = data.getData();
            profileImage.setImageURI(imageURI);
        }
    }

    private void create_account(){
        String url = "http://89.87.13.28:8800/database/proximity_social_network/php-request/register.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.trim().equals("error")){
                    Toast.makeText(getApplicationContext(), "Email déjà utilisé", Toast.LENGTH_SHORT).show();
                }
                else {
                    saveToServer();
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
                params.put("email", email.getText().toString().trim());
                params.put("username", name.getText().toString().trim());
                params.put("birthdate", birthDate.getText().toString().trim());
                params.put("password", password.getText().toString().trim());
                params.put("uri_picture", "profile_pic_"+ email.getText().toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
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
        try {
            FileOutputStream fos = openFileOutput("profil",Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            // write object to file
            //oos.writeObject(MainActivity.profil);
            // closing resources
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
