package com.vsevolod.carbontracker.Model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.vsevolod.carbontracker.R;

/**
 * Created by myharmonykitty on 2017-04-05.
 */

public class NotificationReceiver extends BroadcastReceiver {
    private Singleton singleton = Singleton.getInstance();
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(singleton.getNotifications() == null) {
            singleton.setNotifications(context);
        }
        Intent repeatIntent = singleton.getNotifications().makeNotificationIntent(singleton, context);
        repeatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, repeatIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.car2)
                .setContentTitle(context.getResources().getString(R.string.notification_title))
                .setContentText(singleton.getNotifications().getNotification(singleton))
                .setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }
}
