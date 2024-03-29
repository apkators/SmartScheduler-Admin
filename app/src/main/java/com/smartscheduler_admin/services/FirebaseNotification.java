package com.smartscheduler_admin.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.smartscheduler_admin.R;
import com.smartscheduler_admin.activities.SplashActivity;

/**
 * Helper class for showing and canceling location
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */

public class FirebaseNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "FxSignalsNotification";
    private static Notification notification;
    private static Notification.Builder builder;
    @SuppressLint("StaticFieldLeak")
    private static NotificationCompat.Builder builderCompact;
    //private static Intent iStopService;
    //private static PendingIntent resultPendingIntent;
    private static boolean isFirst = true;

    public static void notify(final Context context,
                              final String title, final String text, FirebaseMessagingService activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifyO(context, title, text, activity);
        } else {
            notifyPre(context, title, text, activity);
        }
    }

    private static String createOrderChannel(Context ctx) {
        // Create a channel.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        String channelName = ctx.getString(R.string.channel_id);
        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_DEFAULT;
        }

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        NotificationChannel notificationChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(
                    ctx.getString(R.string.channel_id), channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        return channelName;
    }

    public static void notifyO(Context context, final String title, final String text, FirebaseMessagingService activity) {
        if (isFirst) {
            String channelId = createOrderChannel(context);

            // Create an Intent for the activity you want to start
            Intent resultIntent = new Intent(activity, SplashActivity.class);
            // Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            // Get the PendingIntent containing the entire back stack
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new Notification.Builder(activity, App.channel);

                builder = new Notification.Builder(context, channelId)
                        .setSmallIcon(R.drawable.logo_cropped)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setOnlyAlertOnce(true)
                        .setOngoing(false)
                        .setAutoCancel(true)
                        .setWhen(System.currentTimeMillis())
                        .setContentText(text)
                        .setContentIntent(resultPendingIntent);
            }

            notification = builder.build();
            //notification.flags = Notification.FLAG_NO_CLEAR ; // & Notification.FLAG_ONLY_ALERT_ONCE & Notification.FLAG_ONGOING_EVENT;
            notify(context, notification);
            isFirst = false;

        } else {
            builder.setContentText(text);
            notify(context, notification = builder.build());
        }
    }

    public static void notifyPre(final Context context,
                                 final String title, final String text, FirebaseMessagingService activity) {

        if (isFirst) {
            //String channelId = createOrderChannel(context);
            // Create an Intent for the activity you want to start
            Intent resultIntent = new Intent(activity, SplashActivity.class);
            // Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            // Get the PendingIntent containing the entire back stack
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            builderCompact = new NotificationCompat.Builder(context, context.getString(R.string.channel_id));

            builderCompact.setSmallIcon(R.drawable.logo_cropped)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setOnlyAlertOnce(true)
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setContentText(text)
                    .setContentIntent(resultPendingIntent);


            notification = builderCompact.build();
            //notification.flags = Notification.FLAG_NO_CLEAR ; // & Notification.FLAG_ONLY_ALERT_ONCE & Notification.FLAG_ONGOING_EVENT;
            notify(context, notification);
            isFirst = false;
        } else {
            builderCompact.setContentText(text);
            notify(context, notification = builderCompact.build());
        }
    }

    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_TAG, 0, notification);
    }
/*
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_TAG, 0);
    }*/
}
