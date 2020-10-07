package com.heybuddy.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import com.heybuddy.BoundService;
import com.heybuddy.R;
import com.heybuddy.constant.DbConstant;
import com.heybuddy.enumeration.BackStack;
import com.heybuddy.enumeration.FragmentState;
import com.heybuddy.listener.BackPressListener;
import com.heybuddy.utility.AppHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HomeActivity extends AppNavigationActivity {
    BackPressListener backPressListener;
    private String TAG = "keyhash";
    private FirebaseAuth mauth;
//    qUgoKFeZTWPiZ9KJUkU06N/MAf0=
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mauth = FirebaseAuth.getInstance();
        printHashKey(getApplicationContext());
        openRecentChatListFragment(FragmentState.REPLACE, BackStack.NO);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(DbConstant.USER_STATUS);
    }

    public void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }

    public void setBackPressListener(BackPressListener backPressListener) {
        this.backPressListener = backPressListener;
    }

    @Override
    public void onBackPressed() {
        hideKeyBoard();
        if (backPressListener != null && !backPressListener.onBackPressed()) return;
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

/*
        Intent intent = new Intent(this, BoundService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);*/

        String uid = AppHelper.getInstance().getUid();
        if (uid == null) {
            afterLogoutAction();
        } else {
            //---IF LOGIN , ADD ONLINE VALUE TO TRUE---
            mDatabaseReference.child(uid).child(DbConstant.ONLINE).setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
       /* if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }*/
    }

    BoundService mBoundService;
    boolean mServiceBound = false;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BoundService.MyBinder myBinder = (BoundService.MyBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;
        }
    };

}
