package com.heybuddy;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.heybuddy.constant.DbConstant;
import com.heybuddy.utility.AppHelper;
import com.google.firebase.firestore.FirebaseFirestore;

public class BoundService extends Service {
    private static String LOG_TAG = "BoundService";
    private IBinder mBinder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "in onBind");
        updateOnlineStatus("true",AppHelper.getInstance().getUid());
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        updateOnlineStatus("true",AppHelper.getInstance().getUid());
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        updateOnlineStatus("false",AppHelper.getInstance().getUid());
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "in onDestroy");
    }

    public class MyBinder extends Binder {
        public BoundService getService() {
            return BoundService.this;
        }
    }

    private void updateOnlineStatus(String isOnline, String uid) {
        try {
            if (uid == null || uid.isEmpty()) return;

            FirebaseFirestore.getInstance().collection(DbConstant.USERS)
                    .document(uid)
                    .update(DbConstant.ONLINE, isOnline);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}