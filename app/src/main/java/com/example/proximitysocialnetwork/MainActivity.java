package com.example.proximitysocialnetwork;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import static android.graphics.Bitmap.Config.RGB_565;

public class MainActivity extends AppCompatActivity {



    public static NetworkHelper net;
    private Button infoAccount;
    private Button searchPeople;
    private Button sendProfil;
    public static TextView clientCo;

    private Button logout;
    private TextView name;
    private TextView email;
    private String mName;
    private String mEmail;
    private ImageView editAccountWheel;

    SessionManager sessionManager;
    private ImageView profileImage;
    private String urlDownload;

    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        sessionManager = new SessionManager(this);

        profileImage = findViewById(R.id.profilePic2);
        infoAccount = (Button) findViewById(R.id.info_compte);
        searchPeople = (Button) findViewById(R.id.search_people);
        sendProfil = (Button) findViewById(R.id.send_account);
        clientCo = (TextView) findViewById(R.id.client_co);
        logout = (Button) findViewById(R.id.logout);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        editAccountWheel = (ImageView) findViewById(R.id.editAccountWheel);

        sessionManager.checkLoggin();
        HashMap<String,String > user = sessionManager.getUserDetail();
        mName = user.get(sessionManager.NAME);
        mEmail = user.get(sessionManager.EMAIL);



        if(sessionManager.isLoggin()) {
            if (net == null) {
             net = new NetworkHelper(this, mEmail);
             net.setCurrentMainActivity(this);
            }
        }

        notificationManager = NotificationManagerCompat.from(this);

        urlDownload = "http://89.87.13.28:8800/database/proximity_social_network/images/profile_pic_"+ mEmail +".jpg";
        downloadProfileImage();

        name.setText(mName);
        email.setText(mEmail);

        editAccountWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setPaintFlags(name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                Intent intent = new Intent(MainActivity.this, EditAccountActivity.class);
                startActivity(intent);
                finish();

            }
        });

        // Intent to activities
        infoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InfoAccountActivity.class));
            }
        });


        searchPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                net.SeachPeople();
                clientCo.setText("• Visible •");
                clientCo.setTextColor(getResources().getColor(R.color.ColorGreen));
            }
        });

        sendProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    net.StopAll();
                    net = new NetworkHelper(getApplicationContext(), mEmail);
                    net.setCurrentMainActivity(MainActivity.this);
                    clientCo.setText("• Invisible •");
                    clientCo.setTextColor(getResources().getColor(R.color.ColorRed));
                }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
            }
        });
    }
        // try with that to resume MainActivity
        @Override
        public void onResume(){
            super.onResume();
            name.setText(mName);
        }

        @Override
        protected void onStart()
        {
            super.onStart();
            if (!hasPermissions(this, NetworkHelper.getRequiredPermissions())) {
                requestPermissions(NetworkHelper.getRequiredPermissions(), net.getRequestCodeRequiredPermissions());
            }
        }

        /**
         * Check permissions status for the application
         * @param context
         * @param permissions
         * @return boolean
         */
        private static boolean hasPermissions(Context context, String... permissions) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }

    /**
     * Request needed permissions to user through UI
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != NetworkHelper.getRequestCodeRequiredPermissions()) {
            return;
        }

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this,"Permissions manquantes", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
        recreate();
    }


    public void downloadProfileImage(){

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        ImageRequest request = new ImageRequest(urlDownload, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                profileImage.setImageBitmap(response);
                profileImage.setVisibility(View.VISIBLE);
                //progressDownload.setVisibility(View.GONE);
                //Toast.makeText(MainActivity.this, "Profile Image Downloaded Successfully", Toast.LENGTH_LONG).show();
            }
        }, 0, 0, ImageView.ScaleType.CENTER, RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressDownload.setVisibility(View.GONE);
                //Toast.makeText(MainActivity.this, "Error while downloading image", Toast.LENGTH_LONG).show();
            }
        }
        );
        requestQueue.add(request);
    }

    public void sendOnChannelNewPerson(String NewPerson){

        Intent activityIntentMain = new Intent(this,PersonDiscoveredActivity.class);
        Intent activityIntentDiscover = new Intent(this,PersonDiscoveredActivity.class);
        Intent activityIntent;
        if(net.getProfilsDiscovered().isEmpty()){
            activityIntent = activityIntentMain;
        }
        else{
            activityIntent = activityIntentDiscover;
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_NEW_PERSON)
                .setSmallIcon(R.drawable.add_profil_img)
                .setContentTitle("Une nouvelle personne decouverte !")
                .setContentText( NewPerson + " est à proximité")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setContentIntent(contentIntent)
                .setColor(getResources().getColor(R.color.ColorPrincipale1))
                .setAutoCancel(true)
                .build();
        notificationManager.notify(1, notification);
    }

}
