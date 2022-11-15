package com.example.todolist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

//this whole class sends the notification at the specified time.
public class NotificationSender extends BroadcastReceiver {

    public static String NotificationID = "notification-id";
    public static String NOTIFICATION = "Notification";
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {

            NotificationChannel channel = new NotificationChannel(NotificationID, NOTIFICATION, NotificationManager.IMPORTANCE_HIGH);
            assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
        }
        int id = intent.getIntExtra(NotificationID, 0);
        assert notificationManager != null;
        notificationManager.notify(id,notification);
    }
}
