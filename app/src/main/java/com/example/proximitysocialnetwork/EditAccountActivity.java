package com.example.proximitysocialnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class EditAccountActivity extends AppCompatActivity {

    private EditText name;
    private EditText description;

    private String mName;
    private String mEmail;

    //image view location on phone
    private Uri imageURI;
    // request code for image
    private static final int PICK_IMAGE = 100;

    private ImageView editProfileImage;
    private ProgressBar progressEdit;
    private Button btnEditAccount;
    private static final String TAG = "test";

    String urlUpload = "http://89.87.13.28:8800/database/proximity_social_network/php-request/upload_image.php";

    String urlEdit = "http://89.87.13.28:8800/database/proximity_social_network/php-request/updateAccount.php";


    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        name = (EditText) findViewById(R.id.editName);
        description = (EditText) findViewById(R.id.editDesc);
        btnEditAccount = (Button) findViewById(R.id.btnEditAccount);
        progressEdit = (ProgressBar) findViewById(R.id.progressEditAcc);

        editProfileImage = (ImageView) findViewById(R.id.profile_image_edit);

        sessionManager = new SessionManager(this);
        sessionManager.checkLoggin();
        HashMap<String,String > user = sessionManager.getUserDetail();

        mName = user.get(sessionManager.NAME);
        mEmail = user.get(sessionManager.EMAIL);

        name.setHint(mName);
        progressEdit.setVisibility(View.GONE);

        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                editProfileImage.startAnimation(animation);
                openGallery();
            }
        });


        btnEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNameDescription();
                editProfilePicture();
                sessionManager.createSession(name.getText().toString(), mEmail);
                onBackPressed();
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

    public void editProfilePicture(){
            btnEditAccount.setVisibility(View.GONE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, response);
                    Toast.makeText(EditAccountActivity.this, "Profile Modified Successfully", Toast.LENGTH_SHORT).show();
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
