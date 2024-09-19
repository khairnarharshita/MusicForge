package com.example.music_artist_collab.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class OreoAndAboveNotification extends ContextWrapper {
 static  final String id="Some_Id";
    static  final String name="FirebaseApp";
NotificationManager notificationManager;

    public OreoAndAboveNotification(Context base) {
        super(base);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
        }

    }

    private void createChannel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(notificationChannel);
        }
    }
        public NotificationManager getManager(){
            if(notificationManager==null){
                notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            }
            return notificationManager;
        }



        @RequiresApi(api = Build.VERSION_CODES.O)
        public Notification.Builder getONotifications(String title, String body,
                                                      PendingIntent pIntent, Uri soundUri, String icon){
        return new Notification.Builder(getApplicationContext(),id)
                .setContentIntent(pIntent).setContentTitle(title).setContentText(body)
                .setSound(soundUri).setAutoCancel(true).setSmallIcon(Integer.parseInt(icon));
        }

    }

