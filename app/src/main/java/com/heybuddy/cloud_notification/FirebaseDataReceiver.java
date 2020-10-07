package com.heybuddy.cloud_notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.heybuddy.Controller;
import com.heybuddy.Model.NotificationModel;
import com.heybuddy.R;
import com.heybuddy.activity.HomeActivity;
import com.heybuddy.constant.AppConstant;
import com.heybuddy.constant.SharedPrefConstant;
import com.heybuddy.utility.PreferanceHelper;

import java.util.Random;

public class FirebaseDataReceiver extends WakefulBroadcastReceiver {

    private final String TAG = "FirebaseDataReceiver";

  /*  @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("BroadcastReceiver::", "BroadcastReceiver");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(intent.getExtras().getString("title"))
                .setContentText(intent.getExtras().getString("message"));
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

                String link=remoteMessage.getData().get("link");

    }*/

    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "I'm in!!!");
        Bundle dataBundle = intent.getBundleExtra("data");

        if (intent != null) {

            Bundle dataBundle1 = intent.getExtras();

            if (dataBundle1.get("sender_id").equals(Controller.getInstance().getCurrentChatUser()))
                return;

            NotificationModel model = new NotificationModel();
            model.setTitle(dataBundle1.get("title") + "");
            model.setBody(dataBundle1.get("body") + "");
            model.setReceiverDeviceToken(dataBundle1.get("receiverDeviceToken") + "");

            if (PreferanceHelper.getInstance().getBoolean(SharedPrefConstant.IS_LOGIN)) {
                showSmallNotification(model);
            }
        }
    }

    private static int getRequestCode() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(900000);
    }

    private void showSmallNotification(NotificationModel model) {

        NotificationManager mNotificationManager = (NotificationManager) Controller.getInstance().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("default", "YOUR_CHANNEL_NAME", NotificationManager.IMPORTANCE_DEFAULT);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
//            channel.setSound(NotificationSound, audioAttributes);

            if (mNotificationManager.areNotificationsEnabled() && channel.getImportance() != NotificationManager.IMPORTANCE_NONE) {
                try {
//                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), NotificationSound);
//                    r.play();
                } catch (Exception e) {
                }
            }

            mNotificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Controller.getInstance().getApplicationContext(), "default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(Controller.getInstance().getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setContentTitle(model.getTitle())
                .setContentText(model.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        Intent intent = new Intent(Controller.getInstance().getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(AppConstant.NOTIFICATIONMODEL, model);

        PendingIntent pi = PendingIntent.getActivity(Controller.getInstance().getApplicationContext(), getRequestCode(), intent, 0);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(getRequestCode(), mBuilder.build());
    }

}