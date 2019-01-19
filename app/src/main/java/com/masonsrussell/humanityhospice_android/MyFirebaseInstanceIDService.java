package com.masonsrussell.humanityhospice_android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseInstanceIDSer";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "onNewToken: " + s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String title = notification.getTitle().toString();
        String body = notification.getBody().toString();


        showNotification(title, body);

    }

    private void showNotification(String title, String body) {

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String notificationChannelID = "rld.hh.notification";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notificationChannelID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notification Channel");
            channel.enableLights(true);
            channel.setLightColor(Color.MAGENTA);
            manager.createNotificationChannel(channel);

        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelID);
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.logo_small)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");


        manager.notify(new Random().nextInt(), builder.build());

    }


}
