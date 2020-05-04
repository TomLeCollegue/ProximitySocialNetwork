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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.graphics.Bitmap.Config.RGB_565;


public class EditAccountActivity extends AppCompatActivity {

    private EditText name;
    private EditText description;
    private EditText oldPassword;
    private EditText newPassword;
    private EditText confirmNewPassword;

    private String mName;
    private String mEmail;

    //image view location on phone
    private Uri imageURI;
    // request code for image
    private static final int PICK_IMAGE = 100;

    Boolean correctForm = true;

    Context mContext;

    private ImageView editProfileImage;
    private ProgressBar progressEdit;
    private Button btnEditAccount;

    private static final String TAG = "test";

    //URLs for Volley requests
    String urlDownload;
    String urlUpload = "http://89.87.13.28:8800/database/proximity_social_network/php-request/upload_image.php";
    String urlEdit = "http://89.87.13.28:8800/database/proximity_social_network/php-request/updateAccount.php";
    String urlGetDescription = "http://89.87.13.28:8800/database/proximity_social_network/php-request/getDescription.php";
    String urlGetPassword = "http://89.87.13.28:8800/database/proximity_social_network/php-request/getOldPassword.php";
    String urlEditPassword = "http://89.87.13.28:8800/database/proximity_social_network/php-request/editPassword.php";

    //"true" or "false" depending from the result from the server
    // when checking the password
    Boolean responsePassword;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        mContext = getApplicationContext();

        editProfileImage = (ImageView) findViewById(R.id.profile_image_edit);

        name = (EditText) findViewById(R.id.editName);
        description = (EditText) findViewById(R.id.editDesc);

        oldPassword = (EditText) findViewById(R.id.oldPassword);
        newPassword = (EditText) findViewById(R.id.newPassword);
        confirmNewPassword = (EditText) findViewById(R.id.confirmNewPassword);

        btnEditAccount = (Button) findViewById(R.id.btnEditAccount);
        progressEdit = (ProgressBar) findViewById(R.id.progressEditAcc);

        sessionManager = new SessionManager(this);
        sessionManager.checkLoggin();
        HashMap<String,String > user = sessionManager.getUserDetail();

        mName = user.get(sessionManager.NAME);
        mEmail = user.get(sessionManager.EMAIL);

        urlDownload = "http://89.87.13.28:8800/database/proximity_social_network/images/profile_pic_"+ mEmail +".jpg";

        name.setHint(mName);
        name.setText(mName);
        progressEdit.setVisibility(View.GONE);

        Log.d(TAG, "j arrive ici");
        getProfilePicture();
        getDescription();
        Log.d(TAG,"je passe par là");

        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                editProfileImage.startAnimation(animation);
                openGallery();
            }
        });

        final ColorStateList oldColors = name.getTextColors();

        btnEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.lefttoright);
                String regexUserName = "^([a-zA-Z]{2,}\\s[a-zA-z]{1,}'?-?[a-zA-Z]{2,}\\s?([a-zA-Z]{1,})?)";
                String regexPassword = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$";
                        /*   Password matching expression. Password must be at least 8 characters,
                        and must include at least one upper case letter, one lower case letter, and one numeric digit or special character.  */
                newPassword.setTextColor(oldColors);
                newPassword.setBackgroundResource(R.drawable.plaintextstyle);
                confirmNewPassword.setTextColor(oldColors);
                confirmNewPassword.setBackgroundResource(R.drawable.plaintextstyle);
                Pattern patternUserName = Pattern.compile(regexUserName);
                Pattern patternPassword = Pattern.compile(regexPassword);
                Matcher matcherUsername = patternUserName.matcher(name.getText().toString().trim());
                Matcher matcherPassword = patternPassword.matcher(newPassword.getText().toString().trim());

                getPassword();

                // Verif info Form
                if (newPassword.getText().toString().equals(confirmNewPassword.getText().toString()) && (responsePassword)) {

                    oldPassword.setBackgroundResource(R.drawable.plaintextstylegreen);
                    oldPassword.setTextColor(getResources().getColor(R.color.ColorGreen));


                    editNameDescription();
                    editProfilePicture();
                    editPassword();
                    sessionManager.createSession(name.getText().toString(), mEmail);
                    onBackPressed();
                    if(!matcherUsername.find()) {
                        //Toast.makeText(getApplicationContext(),"Entrez un Nom Valide", Toast.LENGTH_SHORT).show();
                        correctForm = false;
                        name.setBackgroundResource(R.drawable.plaintextstylered);
                        name.setTextColor(getResources().getColor(R.color.ColorRed));
                        name.startAnimation(animation);
                    }
                    else {
                        name.setTextColor(oldColors);
                        name.setBackgroundResource(R.drawable.plaintextstyle);
                    }
                }else{
                    editNameDescription();
                    editProfilePicture();
                    sessionManager.createSession(name.getText().toString(),mEmail);
                    onBackPressed();
                }
            }
        });
    }

    private void editNameDescription(){
        progressEdit.setVisibility(View.VISIBLE);
        btnEditAccount.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlEdit,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        progressEdit.setVisibility(View.GONE);
                        btnEditAccount.setVisibility(View.VISIBLE);
                        Log.d(TAG, "je suis dans la réponse");
                        Log.d(TAG, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")){
                                Log.d(TAG, "je suis dans la réponse si succes");
                                Toast.makeText(EditAccountActivity.this, "Nom modifié !", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "je suis dans l'error de la réponse");
                            Toast.makeText(EditAccountActivity.this, "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "je suis carrément dans l'erreur");
                progressEdit.setVisibility(View.GONE);
                btnEditAccount.setVisibility(View.VISIBLE);
                Toast.makeText(EditAccountActivity.this, "Error"+error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("name", name.getText().toString());
                params.put("description", description.getText().toString());
                params.put("email", mEmail);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void getPassword(){
        final Animation animation= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.lefttoright);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlGetPassword, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if (success.equals("1")) {
                        responsePassword = true;
                    } else {
                        oldPassword.startAnimation(animation);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    oldPassword.startAnimation(animation);
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
                params.put("email", mEmail);
                params.put("password", oldPassword.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void editPassword(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlEditPassword,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(EditAccountActivity.this, "success password édité", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error");
                Toast.makeText(EditAccountActivity.this, "Error"+error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("password", newPassword.getText().toString());
                params.put("email", mEmail);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    public void getDescription(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlGetDescription,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "je suis dans la réponse");
                        Log.d(TAG, response);
                        description.setText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "je suis carrément dans l'erreur");
                Toast.makeText(EditAccountActivity.this, "Error"+error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email", mEmail);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);

    }

    public void getProfilePicture(){
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        ImageRequest request = new ImageRequest(urlDownload, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                editProfileImage.setImageBitmap(response);
                //Toast.makeText(EditAccountActivity.this, "Profile Image Downloaded Successfully", Toast.LENGTH_SHORT).show();
            }
        }, 500, 500, ImageView.ScaleType.CENTER, RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditAccountActivity.this, "Error while downloading image", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue.add(request);
    }

    public void editProfilePicture(){
            btnEditAccount.setVisibility(View.GONE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, response);
                    Toast.makeText(EditAccountActivity.this, "Profile Picture Modified Successfully", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(AccountCreationActivity.this, response, Toast.LENGTH_SHORT).show();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(EditAccountActivity.this, "error: "+ error.toString(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map <String,String> params = new HashMap<>();

                    BitmapDrawable drawable = (BitmapDrawable) editProfileImage.getDrawable();
                    Bitmap myImageBitmap = new ImageGestion().getBitmapFromDrawable(drawable);

                    //encodes image to string from base 64
                    String imageEncoded = new ImageGestion().imageToString(myImageBitmap);
                    params.put("name_picture", "profile_pic_"+ mEmail);
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
            editProfileImage.setImageURI(imageURI);
        }
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
