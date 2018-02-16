package com.example.hakeem.demo.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.hakeem.demo.AudioPlayerActivity;
import com.example.hakeem.demo.MainActivity;
import com.example.hakeem.demo.R;

/**
 * Created by hakeem on 2/1/18.
 */

public class MediaPlayerNotification {

    /*
   * This notification ID can be used to access our notification after we've displayed it. This
   * can be handy when we need to cancel the notification, or perhaps update it. This number is
   * arbitrary and can be set to whatever you like. 1138 is in no way significant.
   */
    private static final int WATER_REMINDER_NOTIFICATION_ID = 101;
    /**
     * This pending intent id is used to uniquely reference the pending intent
     */
    private static final int AUDIO_PLAYER_PENDING_INTENT_ID = 3417;
    /**
     * This notification channel id is used to link notifications to this channel
     */
    private static final String AUDIO_PLAYER_NOTIFICATION_CHANNEL_ID = "audio-player-notification-channel";



    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancelAll();

        Intent intent = new Intent(context, MediaPlayerService.class);
        context.stopService(intent);
    }



    public void showAudioPlayerNotification(Context context1, PlaybackStatus playbackStatus) {

        Resources res = context1.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.image);

        PendingIntent play_pauseAction = null;

        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            //create the pause action
            play_pauseAction = playbackAction(context1 ,1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            //create the play action
            play_pauseAction = playbackAction(context1, 0);
        }

        NotificationManager notificationManager = (NotificationManager) context1.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            NotificationChannel mChannel = new NotificationChannel(
                    AUDIO_PLAYER_NOTIFICATION_CHANNEL_ID,
                    context1.getString(R.string.audio_player_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context1,AUDIO_PLAYER_NOTIFICATION_CHANNEL_ID)

                .setShowWhen(false)

                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()

//                        // Attach our MediaSession token
                       // .setMediaSession(MediaPlayerService.mediaSession.getSessionToken())
//                        // Show our playback controls in the compact notification view.
                        .setShowActionsInCompactView(0, 1))

                .setColor(ContextCompat.getColor(context1, R.color.colorAccent))

                .setLargeIcon(largeIcon)

                .setSmallIcon(android.R.drawable.stat_sys_headset)
////
                /**
                 * to force appearing player controls in notification if user enable hide content of notifications
                 * */
     //           .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                .setDeleteIntent(playbackAction(context1, 4))

                /**
                 * prevent user from swap left or right to delete notification
                 * it will disappear itself if track end or press cancel button on it
                  */
                .setAutoCancel(false)
                .setOngoing(true)
/////
                .setContentTitle(context1.getString(R.string.app_name))
                .setContentText(Variables.statueName)

                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "exit",playbackAction(context1, 4))
                .addAction(notificationAction, "pause", play_pauseAction)


                .setContentIntent(contentIntent(context1));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        assert notificationManager != null;

        notificationManager.notify(WATER_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
        Log.e("11" , "1");
    }



    private PendingIntent playbackAction(Context context, int actionNumber) {
        Intent playbackAction = new Intent(context, MediaPlayerService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(Variables.ACTION_PLAY);
                AudioPlayerActivity.playPause.setText(context.getString(R.string.play));
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(Variables.ACTION_PAUSE);
                AudioPlayerActivity.playPause.setText(context.getString(R.string.pause));
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
//            case 2:
//                // Next track
//                playbackAction.setAction(ACTION_NEXT);
//                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
//            case 3:
//                // Previous track
//                playbackAction.setAction(ACTION_PREVIOUS);
//                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            case 4:
                // Previous track
                playbackAction.setAction(Variables.ACTION_STOP);
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
}

    private static PendingIntent contentIntentOld(Context context) {
        Log.e("content intent", "hello");

        Intent startActivityIntent = new Intent(context, AudioPlayerActivity.class);
        return PendingIntent.getActivity(
                context,
                AUDIO_PLAYER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent contentIntent(Context context) {
        Log.e("content intent", "hello" );
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, AudioPlayerActivity.class);
        /**
         * The stack builder object will contain an artificial back stack for the
         * started Activity.
         * This ensures that navigating backward from the Activity leads out of
         * your app to the Home screen.
         */
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        /**Adds the back stack for the Intent (but not the Intent itself)*/
        stackBuilder.addParentStack(AudioPlayerActivity.class);
        /**Adds the Intent that starts the Activity to the top of the stack*/
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        return resultPendingIntent;
    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.image);
        return largeIcon;
    }
}
