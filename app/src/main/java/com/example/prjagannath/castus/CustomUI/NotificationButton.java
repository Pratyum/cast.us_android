package com.example.prjagannath.castus.CustomUI;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Grayson on 9/3/2016.
 */
public class NotificationButton extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //send to server of rejecting
        int notificationId = intent.getIntExtra("notificationId", 0);
        Log.d("Intent", "Canceled" + notificationId);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);

    }
}
