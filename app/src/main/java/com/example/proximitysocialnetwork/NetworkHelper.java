package com.example.proximitysocialnetwork;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class NetworkHelper implements Serializable {

    private Context appContext;
    private boolean advertising = false;
    private boolean discovering = false;
    private final ConnectionsClient connectionsClient;
    private String endpointIdClient;
    private MainActivity currentMainActivity;

    private String infoConnection;
    private String emailDiscovered;

    public void setCurrentMainActivity(MainActivity currentMainActivity) {
        this.currentMainActivity = currentMainActivity;
    }

    private static final String[] REQUIRED_PERMISSIONS =
            new String[] {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET
            };

    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    private static final Strategy STRATEGY = Strategy.P2P_CLUSTER; // Nearby connection strategy for data sending

    private final PayloadCallback payloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
                    /* Deserialization of incoming payload */
                    Object dataReceived = new Object();
                    try{
                        dataReceived = SerializationHelper.deserialize(payload.asBytes());
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    /*if ( dataReceived instanceof Profil){
                        MainActivity.profil = (Profil) dataReceived;
                    }*/

                }

                @Override
                public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
                    Log.i(TAG, String.format(
                            "onPayloadTransferUpdate(endpointId=%s, update=%s)", s, payloadTransferUpdate));
                }
            };





    public NetworkHelper(Context appContext, String infoConnection) {
        this.appContext = appContext;
        this.connectionsClient = Nearby.getConnectionsClient(appContext);
        this.infoConnection = infoConnection;
    }

    public static int getRequestCodeRequiredPermissions() {
        return REQUEST_CODE_REQUIRED_PERMISSIONS;
    }

    public static String[] getRequiredPermissions() {
        return REQUIRED_PERMISSIONS;
    }




    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {

                    Toast.makeText(appContext, "Detecté a proximité :" + info.getEndpointName() , Toast.LENGTH_SHORT).show();
                    Log.w("newEndPoint", info.getEndpointName());


                    currentMainActivity.sendOnChannelNewPerson(info.getEndpointName());
                    newDiscovery(info.getEndpointName());

                }

                @Override
                public void onEndpointLost(String endpointId) {
                    // A previously discovered endpoint has gone away.
                }
            };


    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo info) {
                    Log.i(TAG, "onConnectionInitiated: rejecting connection");
                    connectionsClient
                            .acceptConnection(endpointId, payloadCallback)
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "acceptConnection() failed.", e);
                                        }
                                    });
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    switch (result.getStatus().getStatusCode()) {
                        case ConnectionsStatusCodes.STATUS_OK:
                            // We're connected! Can now start sending and receiving data.
                            endpointIdClient = endpointId;
                            MainActivity.clientCo.setText("Connecté");
                            break;
                        case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                            // The connection was rejected by one or both sides.
                            break;
                        case ConnectionsStatusCodes.STATUS_ERROR:
                            // The connection broke before it was able to be accepted.
                            break;
                        default:
                            // Unknown status code
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    // We've been disconnected from this endpoint. No more data can be
                    // sent or received.
                }
            };


    public void StopAll(){
        connectionsClient.stopAllEndpoints();

    }


    // find a phone nearby
    public void SeachPeople() {
        if (!discovering && !advertising) {
            startAdvertising();
            startDiscovery();
            MainActivity.clientCo.setText("Cherche...");
        }
    }


    private void startDiscovery() {
        if(!discovering) {
            connectionsClient
                    .startDiscovery(
                            appContext.getPackageName(), endpointDiscoveryCallback,
                            new DiscoveryOptions.Builder().setStrategy(STRATEGY).build())
                    .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unusedResult) {
                                    discovering = true;
                                    Log.d(TAG, "Now discovering endpoint " + infoConnection);

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    discovering = false;
                                    Log.w(TAG, "startDiscovery() failed.", e);

                                }
                            });
        }
    }

    /** Broadcasts our presence using Nearby Connections so other players can find us. */
    private void startAdvertising() {
        if(!advertising) {
            connectionsClient
                    .startAdvertising(
                            infoConnection, appContext.getPackageName(), connectionLifecycleCallback,
                            new AdvertisingOptions.Builder().setStrategy(STRATEGY).build())
                    .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unusedResult) {
                                    advertising = true;
                                    Log.d(TAG, "Now advertising endpoint " + infoConnection);
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    advertising = false;
                                    Log.w(TAG, "startAdvertising() failed.", e);
                                }
                            });
        }
    }


    public void sendToClient(final Object o) throws IOException {
        Payload data = Payload.fromBytes(SerializationHelper.serialize(o));
        connectionsClient
                .sendPayload(endpointIdClient, data)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(TAG, "SendToClient (" + endpointIdClient + ") Payload : " + o.toString());
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG,"sendPayload() failed.", e);
                            }
                        });
    }


    private void newDiscovery(String newemailDiscovered){
        emailDiscovered = newemailDiscovered;
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

                            Toast.makeText(appContext,"decouvert " + email + " " + name + " " + uriPicture, Toast.LENGTH_LONG).show();
                            //lancer la vue
                        }

                    }
                    else{

                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(appContext, " error " + e.toString(), Toast.LENGTH_LONG).show();
                }
                /* if(response.trim().equals("success")){
                    Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                } */
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(appContext, "error :" + error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("personal_email", infoConnection.trim());
                params.put("discovered_email", emailDiscovered);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(appContext);
        requestQueue.add(stringRequest);
    }
}


