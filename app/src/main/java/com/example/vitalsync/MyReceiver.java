package com.example.vitalsync;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "MyReceiver";
    private static final String CHANNEL_ID = "MyReceiverChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.example.dailybite.MY_ACTION".equals(intent.getAction())) {
            Log.d(TAG, "Received broadcast: " + intent.getAction());

            String data = intent.getStringExtra("extra_data");
            if (data != null) {
                Log.d(TAG, "Received data: " + data);
            }

            startSomeService(context);
            showNotification(context, "Broadcast Received", "Action: " + intent.getAction());
        }
    }

    private void startSomeService(Context context) {
        Intent serviceIntent = new Intent(context, PedometerService.class);
        context.startService(serviceIntent);
    }

    private void showNotification(Context context, String title, String message) {
        // Create notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Receiver Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }
    }
}
