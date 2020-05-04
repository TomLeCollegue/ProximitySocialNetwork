package com.example.proximitysocialnetwork;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proximitysocialnetwork.adapters.AdapterNotif;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.graphics.Bitmap.Config.RGB_565;

public class MainActivity extends AppCompatActivity implements AdapterNotif.OnItemClickListener{


    private PopupWindow popUpNotif;
    public NetworkHelper netMain;
    private Button infoAccount;

    public static TextView clientCo;
    private ImageView editButton;
    private Button logout;
    private TextView name;
    private TextView email;
    private String mName;
    private String mEmail;
    private Switch switchNetwork;
    public static TextView textNotifNumber;

    SessionManager sessionManager;
    private ImageView profileImage;
    private String urlDownload;
    private ImageView notif_button;

    private AdapterNotif MyAdapter;
    private RecyclerView rc;

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
        notif_button = findViewById(R.id.notif);
        textNotifNumber = findViewById(R.id.text_notif_number);

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
                    clientCo.setText("• Visible •");
                    clientCo.setTextColor(getResources().getColor(R.color.ColorGreen));
                    // **** Start the service ***** //
                    startService();
                }
                else{
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
        loadDataOffLineDiscovery();

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
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                editButton.startAnimation(animation);
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


        notif_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopUp(v);
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


    public void loadDataOffLineDiscovery(){
        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("LIST_DISCOVERED_OFF_LINE", null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        App.profilsDiscoveredOffLine = gson.fromJson(json, type);
        if (App.profilsDiscoveredOffLine == null) {
            App.profilsDiscoveredOffLine = new ArrayList<>();
        }
        if (App.profilsDiscoveredOffLine.isEmpty()) {
            Toast.makeText(this, "Pas de nouveaux profils à decouvrir", Toast.LENGTH_LONG).show();
            Log.d("profils", "no profils");
        }
        else{
            ArrayList<String> profilesTocheck = (ArrayList<String>) App.profilsDiscoveredOffLine.clone();
            sessionManager.ClearNewPersonOffline();
            for (int i = 0; i < profilesTocheck.size(); i++) {
                newDiscovery(profilesTocheck.get(i));
            }
        }
        textNotifNumber.setText(App.profilsDiscovered.size()+"");
        if (App.profilsDiscovered.size() == 0){
            textNotifNumber.setVisibility(View.INVISIBLE);
        }
    }


    private void newDiscovery(final String newemailDiscovered){
        String url = "http://89.87.13.28:8800/database/proximity_social_network/php-request/newdiscovery.php";

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

                            App.profilsDiscovered.add(new Profil(name,email,uriPicture));
                            startActivity(new Intent(MainActivity.this, PersonDiscoveredActivity.class));
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
                sessionManager.AddNewPersonOffline(newemailDiscovered);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("personal_email", mEmail.trim());
                params.put("discovered_email", newemailDiscovered);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void displayPopUp(View v){
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.popup_notif, null);

        TextView noNotif = popUpView.findViewById(R.id.text_no_notif);
        LinearLayout mainLayout = popUpView.findViewById(R.id.layout_main);
        rc = popUpView.findViewById(R.id.recycler_view_notif);
        rc.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        if(App.profilsDiscovered.size() > 0){
            noNotif.setVisibility(View.INVISIBLE);
        }
        MyAdapter = new AdapterNotif(App.profilsDiscovered);
        rc.setAdapter(MyAdapter);
        MyAdapter.setonItemClickListener(MainActivity.this);


        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        popUpNotif = new PopupWindow(popUpView, width, height, focusable);

        popUpNotif.showAtLocation(v, Gravity.CENTER, 0, 0);

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpNotif.dismiss();
            }
        });


    }


    // ****** Listener RecyclerView **** //
    public void onItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, PersonDiscoveredActivity.class);
        startActivity(intent);
        finish();
    }

    public static void UpdateNotifNumber(){
        textNotifNumber.setText(App.profilsDiscovered.size()+"");
        if (App.profilsDiscovered.size() == 0){
            textNotifNumber.setVisibility(View.INVISIBLE);
        }
        else{
            textNotifNumber.setVisibility(View.VISIBLE);
        }
    }




}
