package com.mx.exfcm.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mx.exfcm.MainActivity;
import com.mx.exfcm.R;

import androidx.core.app.NotificationCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    /*FCM으로 받은 message handler*/
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "received From: " + remoteMessage.getFrom());
        Log.d(TAG, "received Data: " + remoteMessage.getData());

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "received: " + remoteMessage.getNotification().getBody() + "// "
                    + remoteMessage.getNotification().getTitle());

            sendNotification(remoteMessage.getNotification());
        }
    }

    /*토큰이 새로 생기면 호출*/
    @Override
    public void onNewToken(String token) {
        sendRegistrationToServer(token);
    }

    /*server로 토큰 전달*/
    private void sendRegistrationToServer(String token) {
        Log.d("send new token", token);
        // TODO: Implement this method to send token to your app server.
    }

    /*FCM 메시지 설정 함수*/
    private void sendNotification(RemoteMessage.Notification message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setColor(getColor(R.color.colorAccent))
                        .setContentTitle(message.getTitle())
                        .setContentText(message.getBody())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}