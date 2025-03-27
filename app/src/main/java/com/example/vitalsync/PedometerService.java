package com.example.vitalsync;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class PedometerService extends Service {

    private static final String CHANNEL_ID = "PedometerChannel";

    private int steps = 0;
    private double distance = 0.0;
    private double calories = 0.0;
    private long elapsedTime = 0L;

    private Handler handler = new Handler();
    private BroadcastReceiver pedometerDataReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        pedometerDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("com.example.dailybite.UPDATE_PEDOMETER".equals(intent.getAction())) {
                    steps = intent.getIntExtra("steps", 0);
                    distance = intent.getDoubleExtra("distance", 0.0);
                    calories = intent.getDoubleExtra("calories", 0.0);
                    elapsedTime = intent.getLongExtra("elapsedTime", 0L);

                    updateNotification();
                }
            }
        };
        registerReceiver(pedometerDataReceiver, new IntentFilter("com.example.dailybite.UPDATE_PEDOMETER"));

        startForeground(1, createNotification());
    }

    private Notification createNotification() {
        String timeFormatted = String.format("%02d:%02d:%02d",
                (elapsedTime / 3600000), (elapsedTime / 60000) % 60, (elapsedTime / 1000) % 60);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Pedometer Running")
                .setContentText("Steps: " + steps + " | Distance: " + String.format("%.2f", distance) + " km\nCalories: " + String.format("%.1f", calories) + " kcal | Time: " + timeFormatted)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void updateNotification() {
        Notification notification = createNotification();
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(1, notification);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pedometer Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(pedometerDataReceiver);
        handler.removeCallbacksAndMessages(null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
