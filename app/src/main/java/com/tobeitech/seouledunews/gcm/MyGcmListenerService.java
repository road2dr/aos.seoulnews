package com.tobeitech.seouledunews.gcm;

/**
 * Created by LocalUser1 on 2017-06-16.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.tobeitech.api.utils.Dlog;
import com.tobeitech.seouledunews.R;
import com.tobeitech.seouledunews.view.MainActivity;

/**
 * Created by jingyu on 16. 6. 25..
 */
public class MyGcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Dlog.d("From: " + from);
        Dlog.d("Message: " + message);

        String toUrl = data.getString("toUrl");
        Dlog.d("toUrl: " + toUrl);
/*
        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
*/
        sendNotification(message, toUrl);
    }

    private void sendNotification(String message, String url) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("SeoulEduNewsToUrl", url);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = null;
        int flag = (Build.VERSION.SDK_INT >= 30) ? PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;    // junseo api level 30 이상 대응
        pendingIntent = PendingIntent.getActivity(this, 0, intent, flag);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_noti)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}

