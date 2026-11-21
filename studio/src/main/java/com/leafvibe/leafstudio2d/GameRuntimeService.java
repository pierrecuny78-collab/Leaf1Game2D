package com.leafvibe.leafstudio2d;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

/**
 * User-started, visible background simulation controller.
 * Stores elapsed time and production ticks; it does not run a full render loop.
 */
public class GameRuntimeService extends Service {
    private static final String CHANNEL = "leaf_game_runtime";
    private static final int NOTIFICATION_ID = 2402;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long lastTick;
    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            long now = System.currentTimeMillis();
            long elapsed = Math.max(0L, Math.min(now - lastTick, 300000L));
            if (elapsed >= 1000L) {
                long seconds = elapsed / 1000L;
                getSharedPreferences("leaf_world", MODE_PRIVATE).edit()
                    .putLong("last_tick", now)
                    .putLong("simulated_seconds",
                        getSharedPreferences("leaf_world", MODE_PRIVATE)
                            .getLong("simulated_seconds", 0L) + seconds)
                    .putLong("gold", getSharedPreferences("leaf_world", MODE_PRIVATE)
                        .getLong("gold", 500L) + (seconds / 60L) * 2L)
                    .apply();
                lastTick = now;
            }
            handler.postDelayed(this, 15000L);
        }
    };

    @Override public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL, "Simulation Leaf", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Indique que le mode automatique du jeu est actif.");
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        Intent open = new Intent(this, MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(this, 0, open,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder builder = Build.VERSION.SDK_INT >= 26
            ? new Notification.Builder(this, CHANNEL)
            : new Notification.Builder(this);
        Notification notification = builder
            .setContentTitle("LeafStudio2D — mode automatique")
            .setContentText("Le monde sélectionné continue sa simulation.")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pending)
            .setOngoing(true)
            .build();
        startForeground(NOTIFICATION_ID, notification);
        lastTick = System.currentTimeMillis();
        handler.removeCallbacks(ticker);
        handler.post(ticker);
        return START_STICKY;
    }

    @Override public void onDestroy() {
        handler.removeCallbacks(ticker);
        getSharedPreferences("leaf_runtime", MODE_PRIVATE).edit()
            .putBoolean("auto_running", false).apply();
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}