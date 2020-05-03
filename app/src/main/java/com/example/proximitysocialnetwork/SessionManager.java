package com.example.proximitysocialnetwork;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static String NAME = "NAME";
    public static String EMAIL = "EMAIL";

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("LOGIN", PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String name, String email){
        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.apply();
    }

    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLoggin(){
        if(!this.isLoggin()){
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((MainActivity) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail(){
        HashMap<String, String> user = new HashMap<>();

        user.put(NAME, sharedPreferences.getString(NAME,null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        return user;
    }

    public void logout(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        ((MainActivity) context).finish();
    }
}
