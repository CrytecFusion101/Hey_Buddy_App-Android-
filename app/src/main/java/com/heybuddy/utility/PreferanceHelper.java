package com.heybuddy.utility;


import android.content.Context;
import android.content.SharedPreferences;

import com.heybuddy.Controller;
import com.heybuddy.Model.UserData;
import com.heybuddy.constant.SharedPrefConstant;
import com.google.gson.Gson;

public class PreferanceHelper {

    Context context;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPrefToken;
    SharedPreferences.Editor editorToken;

    private static final PreferanceHelper ourInstance = new PreferanceHelper();

    public static PreferanceHelper getInstance() {
        return ourInstance;
    }

    private PreferanceHelper() {
        this.context = Controller.getInstance().getApplicationContext();
        sharedPref = context.getSharedPreferences(SharedPrefConstant.prefName, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        sharedPrefToken = Controller.getInstance().getApplicationContext().getSharedPreferences(SharedPrefConstant.prefNameToken, Context.MODE_PRIVATE);
        editorToken = sharedPrefToken.edit();
    }

    public void putString(String key, String value) {

        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        String str = sharedPref.getString(key, "");
        return str;
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        return sharedPref.getBoolean(key, false);
    }

    public void clearPreference() {
        editor.clear();
        editor.commit();
    }

    public void setUserDetails(UserData userData) {
        Gson gson = new Gson();
        putString(SharedPrefConstant.USER_DETAILS, gson.toJson(userData));
    }

    public UserData getUserDetails() {
        Gson gson = new Gson();
        return gson.fromJson(getString(SharedPrefConstant.USER_DETAILS), UserData.class);
    }

    public void putDeviceToken(String value) {

        editorToken.putString(SharedPrefConstant.DEVICE_TOKEN, value);
        editorToken.commit();
    }

    public String getDeviceToken() {
        String str = sharedPrefToken.getString(SharedPrefConstant.DEVICE_TOKEN, "");
        return str;
    }


}
