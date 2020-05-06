package com.example.proximitysocialnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button buttonLogin;
    private EditText email;
    private EditText password;
    private TextView create_account;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ****** Initialisation **********//
        sessionManager = new SessionManager(this);
        buttonLogin = findViewById(R.id.button_login);
        email = findViewById(R.id.mail_adress);
        password = findViewById(R.id.password);
        create_account = findViewById(R.id.signup);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        // ***** Listener on textView create account *** //
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, AccountCreationActivity.class));
            }
        });
    }

    private void Login(){
        String url = "http://89.87.13.28:8800/database/proximity_social_network/php-request/login.php";
        final Animation animation= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.lefttoright);

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

                            String name = object.getString("name").trim();
                            String email = object.getString("email").trim();

                            sessionManager.createSession(name,email);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));


                            //Toast.makeText(getApplicationContext(), "Connecté avec succès", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                        // ***** fail connection : anim textView **** //
                        email.startAnimation(animation);
                        password.startAnimation(animation);

                    }
                }
                catch (JSONException e){
                    e.printStackTrace();

                    // ***** fail connection : anim textView **** //
                    email.startAnimation(animation);
                    password.startAnimation(animation);
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
                params.put("password", password.getText().toString().trim());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}