package com.alexandershtanko.psychotests.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.alexandershtanko.psychotests.R;
import com.alexandershtanko.psychotests.activities.MainActivity;
import com.alexandershtanko.psychotests.helpers.AmplitudeHelper;
import com.alexandershtanko.psychotests.helpers.RemoteConfigHelper;

/**
 * Created by aleksandr on 07.08.16.
 */
public class TODAlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        sendNotification(context);
    }

    private void sendNotification(Context context) {

        boolean needShowTODNotification = RemoteConfigHelper.isNeedShowTODNotification();
        AmplitudeHelper.onShowTODNotification(needShowTODNotification);

        if (!needShowTODNotification)
            return;

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.tree)
                .setContentTitle(context.getString(R.string.title_test_of_day))
                .setContentText(context.getString(R.string.desc_test_of_day))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


    }
}
