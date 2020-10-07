package com.heybuddy.activity;

import android.os.Bundle;
import android.os.Handler;

import com.heybuddy.constant.SharedPrefConstant;
import com.heybuddy.utility.DebugLog;
import com.heybuddy.utility.PreferanceHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;


public class SplashActivity extends AppNavigationActivity implements Runnable {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int WAIT_TIME = 100;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void run() {

       /* try {
            boolean foregroud = new ForegroundCheckTask().execute(getApplicationContext()).get();
            DebugLog.d("foreground status :: " + foregroud);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        DebugLog.d("token:::" + FirebaseInstanceId.getInstance().getToken());

        // be a buddy
        if (PreferanceHelper.getInstance().getBoolean(SharedPrefConstant.IS_LOGIN)) {
            openHomeActivity();
        }
        //need a buddy
        else if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            openAuthActivity();
        } else {
            openHomeActivity();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(this, WAIT_TIME);
    }

    @Override
    protected void onPause() {
        if (handler != null) handler.removeCallbacksAndMessages(this);
        super.onPause();
    }
}
