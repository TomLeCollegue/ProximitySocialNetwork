package com.example.proximitysocialnetwork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proximitysocialnetwork.adapters.AdapterNotif;
import com.example.proximitysocialnetwork.adapters.AdapterProfilesFriends;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListFriendsActivity extends AppCompatActivity {
    public static ArrayList<Profil> profilsFriends = new ArrayList<>();
    private RecyclerView rv;
    private AdapterProfilesFriends MyAdapter;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_friends);
        sessionManager = new SessionManager(this);
        //profilsFriends.add(new Profil("Tom Ami Bonus ", "tomkubasik74200@gmail.com", "profile_pic_tomkubasik74200@gmail.com"));
        rv = findViewById(R.id.rv_list_friends);

        getProfilFriends();

        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        MyAdapter = new AdapterProfilesFriends(profilsFriends, this);
        rv.setAdapter(MyAdapter);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public void getProfilFriends(){
        String url = "http://89.87.13.28:8800/database/proximity_social_network/php-request/getFriends.php";

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
                            String uriPicture = object.getString("uri_picture").trim();

                            Log.d("newEndPoint","decouvert " + email + " " + name + " " + uriPicture );

                            Profil profil = new Profil(name,email,uriPicture);
                            if(!containsProfil(profil)){
                                profilsFriends.add(profil);
                                MyAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                }
                catch (JSONException e){
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
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public static boolean containsProfil(Profil profil){
        for (int i = 0; i < profilsFriends.size() ; i++) {
            if(profilsFriends.get(i).getEmail().equals(profil.getEmail())){
                return true;
            }
        }
        return false;
    }
}
