package com.example.proximitysocialnetwork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proximitysocialnetwork.adapters.AdapterMessages;
import com.example.proximitysocialnetwork.adapters.AdapterProfilesFriends;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Bitmap.Config.RGB_565;

public class MessagingActivity extends AppCompatActivity {

    SessionManager sessionManager;
    private TextView nameContact;
    private ImageView pictureContact;
    private ImageView sendMessage;
    private RecyclerView messages;
    private String urlDownload;
    private Profil profilContact;
    private ArrayList<Message> messagesArrays = new ArrayList<>();
    private AdapterMessages adapter;
    private EditText textMessage;
    private Handler mainHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        pictureContact = findViewById(R.id.profile_pic_contact);
        sendMessage = findViewById(R.id.button_send);
        textMessage = findViewById(R.id.editText_message);
        nameContact = findViewById(R.id.name_contact);
        messages = findViewById(R.id.rv_messages);

        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            int id =(int) b.get("id_profil");
            profilContact = ListFriendsActivity.profilsFriends.get(id);
        }


        nameContact.setText(profilContact.getName());

        sessionManager = new SessionManager(this);
        urlDownload = "http://89.87.13.28:8800/database/proximity_social_network/images/" + profilContact.getProfileImage() + ".jpg";
        downloadProfileImage();

        // get all the messages
        getMessage();

        // thread deal the http push when new message
        TreadMessages thread = new TreadMessages();
        thread.start();


        messages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        adapter = new AdapterMessages(this, messagesArrays);
        messages.setAdapter(adapter);


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


    }

    public void downloadProfileImage() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        ImageRequest request = new ImageRequest(urlDownload, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                pictureContact.setImageBitmap(response);
                pictureContact.setVisibility(View.VISIBLE);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(PersonDiscoveredActivity.this, "Error while downloading image", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue.add(request);
    }

    public void getMessage() {
        String url;
        url = "http://89.87.13.28:8800/database/proximity_social_network/php-request/getMessages.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("login");

                    if (success.equals("1")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String text = object.getString("text").trim();
                            String self = object.getString("self").trim();
                            String time = object.getString("time").trim();
                            Boolean selfBool = false;
                            if (self.equals("true")) {
                                selfBool = true;
                            }

                            Message message = new Message(text, profilContact.getName(), selfBool, time);
                            if (!containsMessage(message)) {
                                messagesArrays.add(message);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), " error " + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Erreur de connexion" + error, Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", sessionManager.getUserDetail().get(sessionManager.EMAIL));
                params.put("email_contact", profilContact.getEmail());
                params.put("nbMessages", messagesArrays.size() + "");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void sendMessage() {
        String url = "http://89.87.13.28:8800/database/proximity_social_network/php-request/sendMessage.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("login");

                    if (success.equals("1")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);


                        }
                        textMessage.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), " error " + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Erreur de connexion", Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", sessionManager.getUserDetail().get(sessionManager.EMAIL));
                params.put("email_contact", profilContact.getEmail());
                params.put("text", textMessage.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public boolean containsMessage(Message message) {
        for (int i = 0; i < messagesArrays.size(); i++) {
            if (messagesArrays.get(i).getTime().equals(message.getTime())) {
                return true;
            }
        }
        return false;
    }

    class TreadMessages extends Thread {
        @Override
        public void run() {
            getMessage();
        }

        public void getMessage() {
            String url;
            url = "http://89.87.13.28:8800/database/proximity_social_network/php-request/getNewMessages.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        JSONArray jsonArray = jsonObject.getJSONArray("login");

                        if (success.equals("1")) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String text = object.getString("text").trim();
                                String self = object.getString("self").trim();
                                String time = object.getString("time").trim();
                                Boolean selfBool = false;
                                if (self.equals("true")) {
                                    selfBool = true;
                                }

                                Message message = new Message(text, profilContact.getName(), selfBool, time);
                                if (!containsMessage(message)) {
                                    messagesArrays.add(message);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            getMessage();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), " error " + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Erreur de connexion" + error, Toast.LENGTH_LONG).show();

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", sessionManager.getUserDetail().get(sessionManager.EMAIL));
                    params.put("email_contact", profilContact.getEmail());
                    params.put("nbMessages", messagesArrays.size() + "");
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }


    }

}
