package com.example.proximitysocialnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EditPasswordActivity extends AppCompatActivity {

    private EditText oldPassword;
    private EditText newPassword;
    private EditText confirmNewPassword;

    private Button btnEditPassword;

    SessionManager sessionManager;

    private static final String TAG = "test";

    String mEmail;

    String urlEditPassword = "http://89.87.13.28:8800/database/proximity_social_network/php-request/editPassword.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        oldPassword = (EditText) findViewById(R.id.oldPassword);
        newPassword = (EditText) findViewById(R.id.newPassword);
        confirmNewPassword = (EditText) findViewById(R.id.confirmNewPassword);

        btnEditPassword = (Button) findViewById(R.id.btnEditPassword);

        sessionManager = new SessionManager(this);
        sessionManager.checkLoggin();
        HashMap<String,String > user = sessionManager.getUserDetail();

        mEmail = user.get(sessionManager.EMAIL);

        btnEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String regexPassword = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$";
                if (newPassword.getText().toString().equals(confirmNewPassword.getText().toString())){

                    Pattern patternPassword = Pattern.compile(regexPassword);
                    Matcher matcherPassword = patternPassword.matcher(newPassword.getText().toString().trim());

                    oldPassword.setBackgroundResource(R.drawable.plaintextstylegreen);
                    oldPassword.setTextColor(getResources().getColor(R.color.ColorGreen));

                    if(matcherPassword.find()) {
                        editPassword();
                        startActivity(new Intent(EditPasswordActivity.this, EditAccountActivity.class));
                        finish();
                    }
                }
            }
        });

    }
    public void editPassword(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlEditPassword,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(EditPasswordActivity.this, "success password édité", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error");
                Toast.makeText(EditPasswordActivity.this, "Error"+error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("oldPassword", oldPassword.getText().toString().trim());
                params.put("password", newPassword.getText().toString().trim());
                params.put("email", mEmail);
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }
    @Override
    public void onBackPressed(){
        startActivity(new Intent(EditPasswordActivity.this, EditAccountActivity.class));
        finish();
    }
}
