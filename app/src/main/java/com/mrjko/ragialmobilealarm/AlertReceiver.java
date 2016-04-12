package com.mrjko.ragialmobilealarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by jko on 16-04-11.
 */
public class AlertReceiver extends BroadcastReceiver {

    public AlertReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context, "Ragial Notification", "An item has been sold!", "An item has been sold!");
    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert){
        PendingIntent notificIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.alerticon)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgText);

        mBuilder.setContentIntent(notificIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, mBuilder.build());
    }
}
