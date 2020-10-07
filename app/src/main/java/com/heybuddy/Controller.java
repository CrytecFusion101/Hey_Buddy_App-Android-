package com.heybuddy;

import android.app.Application;

import androidx.annotation.NonNull;

import com.heybuddy.constant.DbConstant;
import com.heybuddy.utility.AppHelper;
import com.heybuddy.utility.DebugLog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.OnDisconnect;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

public class Controller extends Application {
    private static Controller instance;
    public static final int FACEBOOK_REQUEST_CODE = 1111;

    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    private String currentChatUser = "";

    public static Controller getInstance() {
        return instance;
    }


    public void setCurrentChatUser(String currentChatUser) {
        this.currentChatUser = currentChatUser;
    }

    public String getCurrentChatUser() {
        return currentChatUser;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        EmojiManager.install(new GoogleEmojiProvider());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        if (mUser != null) {
            setUpOfflineListener(mUser.getUid());
        }

    }


    public void setUpOfflineListener(String uid) {
        if (uid != null) {
            DebugLog.e("setUpOfflineListener : " + uid);

            mUserDatabase = FirebaseDatabase.getInstance().
                    getReference().child(DbConstant.USER_STATUS).child(AppHelper.getInstance().getUid());
            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        updateOnlineStatus(ServerValue.TIMESTAMP);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private OnDisconnect disconnect = null;

    public void updateOnlineStatus(Object status) {
        String uid = AppHelper.getInstance().getUid();
        if (uid == null || uid.isEmpty()) return;

        disconnect = FirebaseDatabase.getInstance().
                getReference()
                .child(DbConstant.USER_STATUS)
                .child(uid)
                .child(DbConstant.ONLINE).onDisconnect();

        disconnect.setValue(status);
    }

    public void cancelDisconnectListner() {
        if (disconnect != null)
            disconnect.cancel();
    }

}
