package com.example.proximitysocialnetwork.jobschedulers;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proximitysocialnetwork.App;
import com.example.proximitysocialnetwork.LoginActivity;
import com.example.proximitysocialnetwork.MainActivity;
import com.example.proximitysocialnetwork.NetworkService;
import com.example.proximitysocialnetwork.PersonDiscoveredActivity;
import com.example.proximitysocialnetwork.Profil;
import com.example.proximitysocialnetwork.R;
import com.example.proximitysocialnetwork.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class JobSearchingDiscovery extends JobService {
    private NotificationManagerCompat notificationManager;

    private boolean jobCancelled = false;
    public SessionManager sessionManager;
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("job", "Job started");
        notificationManager = NotificationManagerCompat.from(this);
        sessionManager = new SessionManager(this);
        doBackgroundWork(params);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("job", "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
    private void doBackgroundWork(final JobParameters params) {
        checkOnserver();
        jobFinished(params, false);

    }



    private void checkOnserver(){
        String url = "http://89.87.13.28:8800/database/proximity_social_network/php-request/newDiscoveryFromOther.php";
        Log.d("job", "Check on server");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("login");
                    Log.d("job", "ReponseVolley");
                    if (success.equals("1")){
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);

                            String name = object.getString("name").trim();
                            String email = object.getString("email").trim();
                            String profilePicture = object.getString("uri_picture").trim();
                            Profil profilDiscovered = new Profil(name,email,profilePicture);
                            sendNotificationNewPerson(name);
                            App.profilsDiscovered.add(profilDiscovered);
                        }
                        if ((NetworkService.isInstanceMainActivityCreated()) && (!App.profilsDiscovered.isEmpty())) {
                            Intent intent = new Intent(getApplicationContext(), PersonDiscoveredActivity.class);
                            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                    }
                }
                catch (JSONException e){
                    Log.d("job", "errorJson");
                    e.printStackTrace();
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
                params.put("email", sessionManager.getUserDetail().get(sessionManager.EMAIL));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void sendNotificationNewPerson(String newPerson){
        Intent activityIntentMain = new Intent(this,PersonDiscoveredActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntentMain, 0);
        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_NEW_PERSON)
                .setSmallIcon(R.mipmap.logo_proximity_round_notif)
                .setContentTitle("Une nouvelle personne decouverte !")
                .setContentText(newPerson + " est à proximité")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setColor(getResources().getColor(R.color.ColorPrincipale3))
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .build();
        notificationManager.notify(2, notification);
    }
}
