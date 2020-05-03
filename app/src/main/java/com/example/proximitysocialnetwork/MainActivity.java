package com.example.proximitysocialnetwork;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import static android.graphics.Bitmap.Config.RGB_565;

public class MainActivity extends AppCompatActivity {



    public NetworkHelper netMain;
    private Button infoAccount;

    public static TextView clientCo;
    private Button editButton;
    private Button logout;
    private TextView name;
    private TextView email;
    private String mName;
    private String mEmail;
    private Switch switchNetwork;

    SessionManager sessionManager;
    private ImageView profileImage;
    private String urlDownload;

    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getBaseContext(), OnclearFromRecentService.class));

        // ****** Initialisation **********//
        sessionManager = new SessionManager(this);
        notificationManager = NotificationManagerCompat.from(this);
        profileImage = findViewById(R.id.profilePic2);
        infoAccount = (Button) findViewById(R.id.info_compte);
        clientCo = (TextView) findViewById(R.id.client_co);
        logout = (Button) findViewById(R.id.logout);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        editButton = findViewById(R.id.editAccountButton);
        switchNetwork = findViewById(R.id.switch1);

        // ****** check if the network is running or not ******** //
        if(NetworkService.isInstanceCreated()){
            switchNetwork.setChecked(true);
            clientCo.setText("• Visible •");
            clientCo.setTextColor(getResources().getColor(R.color.ColorGreen));
        }

        // **** switch online Offline *** //
        switchNetwork.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    // ******** new Network Helper ********//
                    //net = new NetworkHelper(getApplicationContext(), mEmail);
                    //net.setCurrentMainActivity(MainActivity.this);
                    // ******* beginning Searching people *****//
                    //net.SeachPeople();
                    clientCo.setText("• Visible •");
                    clientCo.setTextColor(getResources().getColor(R.color.ColorGreen));

                    // **** Start the service ***** //
                    startService();
                }
                else{
                    // **** Stopping discovery ****** //
                    //net.StopAll();
                    clientCo.setText("• Invisible •");
                    clientCo.setTextColor(getResources().getColor(R.color.ColorRed));

                    // **** Stop the service ***** //
                    stopService();

                }
            }
        });

        // ****** check login for redirection to loginActivity **********//
        sessionManager.checkLoggin();

        // ****** Recup info from session and picture from server *********//
        HashMap<String,String > user = sessionManager.getUserDetail();
        mName = user.get(sessionManager.NAME);
        mEmail = user.get(sessionManager.EMAIL);
        urlDownload = "http://89.87.13.28:8800/database/proximity_social_network/images/profile_pic_"+ mEmail +".jpg";
        downloadProfileImage();
        name.setText(mName);
        email.setText(mEmail);


        // *******Listener Intent to activities*********//
        infoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InfoAccountActivity.class));
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditAccountActivity.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                stopService();
                NotificationManager nManager = ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));
                nManager.cancelAll();
            }
        });
    }



    // ****** Gestion Permission ****** //
    @Override
    protected void onStart()
    {
        super.onStart();
        if (!hasPermissions(this, NetworkHelper.getRequiredPermissions())) {
            requestPermissions(NetworkHelper.getRequiredPermissions(), netMain.getRequestCodeRequiredPermissions());
        }
    }
    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

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

    // ***** Download and display Image Profile **** //
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


    // ******** START AND STOP netWork Service ******** //
    public void startService() {
        Intent serviceIntent = new Intent(this, NetworkService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        NetworkService.instanceMainActivity = this;
    }


    public void stopService() {
        Intent serviceIntent = new Intent(this, NetworkService.class);
        stopService(serviceIntent);
    }


}
