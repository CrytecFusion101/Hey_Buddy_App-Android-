package com.heybuddy.activity;


import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.heybuddy.Model.UserData;
import com.heybuddy.R;
import com.heybuddy.enumeration.BackStack;
import com.heybuddy.enumeration.FragmentState;
import com.heybuddy.utility.AppHelper;
import com.heybuddy.utility.PreferanceHelper;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {

    protected int networkConnectionCheckerCounter = 0;
    protected Boolean isNetworkPopupShowing = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    private ConnectivityManager connectivityManager;

    void fragmentChange(Fragment fragment, FragmentState fragmentState, BackStack backStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragmentState == FragmentState.ADD) {
            transaction.add(R.id.fragment_container, fragment);
        } else {
            transaction.replace(R.id.fragment_container, fragment);
        }
        if (backStack == BackStack.YES) {
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        transaction.commit();
    }

    public void showSnackBar(View v, String str) {
        Snackbar snackbar = Snackbar.make(v, str, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    public void showKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                view.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    public void sendPushNotification(UserData data, String msg) {

        String SERVER_KEY = getResources().getString(R.string.server_key);
        String token = data.getDevice_token();
//        String token = "fb_dtnJc5oM:APA91bGm9tOdHB6f1BwcgFzKWHEpgWNWvAwRE46xFWiB4oDOt8R3PCxBgFX3t-wXys1JW4FPpKdu1DBojQgo5J1CoPg34eLy8irthV4FL6iXcnjYhkBnWLLvUtcLBWlVDH_3E_Wib54V";

        JSONObject obj = null;
//        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {

            dataobjData = new JSONObject();
            obj = new JSONObject();

            dataobjData.put("title", PreferanceHelper.getInstance().getUserDetails().getUsername());
            dataobjData.put("body", msg);
            dataobjData.put("receiverDeviceToken", data.getDevice_token());
            dataobjData.put("sender_id", AppHelper.getInstance().getUid());


            obj.put("to", token);
            //android
            obj.put("data", dataobjData);
            /*//ios
            obj.put("notification", dataobjDataIos);*/


            Log.e("return here>>", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Constants.FCM_PUSH_URL, obj,
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("True", response + "");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("False", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

}
