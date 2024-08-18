package com.example.qldrl.FCM;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.qldrl.Homes.MainHome_Edited;
import com.example.qldrl.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class FCMService extends FirebaseMessagingService {
    private NotificationManager notificationManager;
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        updateNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Log.e("title", Objects.requireNonNull(message.getNotification().getTitle()));
        Log.e("bode", Objects.requireNonNull(message.getNotification().getBody()));
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Tạo hiệu ứng rung
                VibrationEffect effect = VibrationEffect.createWaveform(new long[]{0, 10, 100, 200}, -1);
                // Rung theo hiệu ứng
                vibrator.vibrate(effect);
            } else {
                // Sử dụng phương thức cũ cho các phiên bản Android cũ hơn
                long[] pattern = {0, 10, 100, 200};
                vibrator.vibrate(pattern, -1);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Notificaiton");
            long[] pattern = {0, 10, 100, 200};
            Intent resultIntent = new Intent(this, MainHome_Edited.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_IMMUTABLE);
            builder.setContentTitle(message.getNotification().getTitle());
            builder.setContentText(message.getNotification().getBody());
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message.getNotification().getBody()));
            builder.setAutoCancel(true);
            builder.setVibrate(pattern);
            builder.setSmallIcon(R.mipmap.ic_logo);
            builder.setPriority(NotificationCompat.PRIORITY_MAX);

            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                String channelId = "Notification";
                NotificationChannel channel = new NotificationChannel(
                        channelId, "App", NotificationManager.IMPORTANCE_HIGH
                );
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setVibrationPattern(pattern);
                channel.canBypassDnd();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    channel.canBubble();
                }

                notificationManager.createNotificationChannel(channel);
                builder.setChannelId(channelId);

            }

            notificationManager.notify(100,builder.build());
        }


    }

    private void updateNewToken(String token){

    }
}
