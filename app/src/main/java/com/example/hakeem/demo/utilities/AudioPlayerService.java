package com.example.hakeem.demo.utilities;

//import android.support.v7.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.SeekBar;

import com.example.hakeem.demo.AudioPlayerActivity;
import com.example.hakeem.demo.R;

import java.io.IOException;

import wseemann.media.FFmpegMediaMetadataRetriever;

import static java.lang.String.format;
import static java.lang.String.valueOf;


/**
 * Created by hakeem on 2/13/18.
 */

public class AudioPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {

    /**
     * <<<<<<<<<<<<<<<<<<<<User Defined Vars>>>>>>>>>>>>>>>>>>>>//
     */
    private MediaPlayer mediaPlayer;

    // Binder given to clients
    private final IBinder iBinder = new LocalBinder();

    //Used to pause/resume MediaPlayer
    private int resumePosition;

    private AudioManager audioManager;

    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;


    /**
     * <<<<<<<<<<<<<<<<<<<<User Defined Methods>>>>>>>>>>>>>>>>>>>>
     */
    private void initMediaPlayer() {


        //mediaFile = "http://192.168.1.3/guidak_audio_files/arabic/Neferefre_arabic.mp3";
        Log.e(" initMediaPlayer", "1 ");
        mediaPlayer = new MediaPlayer();
        Log.e(" initMediaPlayer", "2 ");
        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        Log.e(" initMediaPlayer", "3");
        mediaPlayer.setOnErrorListener(this);
        Log.e(" initMediaPlayer", "4 ");
        mediaPlayer.setOnPreparedListener(this);
        Log.e(" initMediaPlayer", "5 ");
        mediaPlayer.setOnBufferingUpdateListener(this);
        Log.e(" initMediaPlayer", "6 ");
        mediaPlayer.setOnSeekCompleteListener(this);
        Log.e(" initMediaPlayer", "7");
        mediaPlayer.setOnInfoListener(this);
        Log.e(" initMediaPlayer", "8");
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();
        Log.e(" initMediaPlayer", "9 and it is equal " + Variables.completeAudioFilePath + "*");
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(Variables.completeAudioFilePath);
            Log.e(" set data source", " to " + Variables.completeAudioFilePath);
        } catch (IOException e) {
            Log.e(" catch", "1");
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();
    }


    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            Log.e("pause", "pauseMedia");
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
            Log.e("resumePosition ", "= " + resumePosition);
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            Log.e("resume", "resumeMedia");
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
            Log.e("resumePosition ", "= " + resumePosition);
        }
    }


    private void resetPlayer() {
        if (mediaPlayer != null)
            mediaPlayer.reset();
    }

    /**
     * Invoked when the audio focus of the system is updated
     * To ensure this good user experience the we will have to handle AudioFocus events
     * and these are handled in the coming method.
     * This method is a switch statement with the focus events as its case:s.
     * Keep in mind that this override method is called after a request for AudioFocus has been made from the system or another media app
     * ----------Cases we have-----------
     * AudioManager.AUDIOFOCUS_GAIN --> The service gained audio focus, so it needs to start playing.
     * AudioManager.AUDIOFOCUS_LOSS --> The service lost audio focus, the user probably moved to playing media on another app,
     * so release the media player.
     * AudioManager.AUDIOFOCUS_LOSS_TRANSIENT --> focus lost for a short time, pause the MediaPlayer.
     * AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK --> Lost focus for a short time, probably a notification arrived on the device,
     * lower the playback volume.
     */

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        assert audioManager != null;
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }


    /**
     * when the user unplug his headphone
     * it is normal to stop media player
     * ACTION_AUDIO_BECOMING_NOISY which means that the audio is about to become ‘noisy’
     * due to a change in audio outputs.
     */
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pauseMedia();
            showAudioPlayerNotification(PlaybackStatus.PAUSED);
        }
    };

    /**
     * The BroadcastReceiver instance will pause the MediaPlayer when the system makes
     * an ACTION_AUDIO_BECOMING_NOISY call.
     * To make the BroadcastReceiver available you must register it
     * the coming function handles this and specifies the intent action
     * BECOMING_NOISY which will trigger this BroadcastReceiver
     */
    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    /**
     *                                      {{{{{Handling Incoming Calls}}}}}
     **/

    /**
     * The callStateListener() function is an implementation of the PhoneStateListener
     * that listens to TelephonyManagers state changes.
     * TelephonyManager provides access to information about the telephony services on the device and listens for
     * changes to the device call state and reacts to these changes.
     */
    private void callStateListener() {

        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * When the AudioPlayerService is playing something and the user wants to play a new track,
     * you must notify the service that it needs to move to new audio.
     * You need a way for the Service to listen to these “play new Audio” calls and act on them
     */
    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Get the new media index form SharedPreferences
            stopSelf();
            //A PLAY_NEW_AUDIO action received
            //reset mediaPlayer to play the new Audio
            stopMedia();
            resetPlayer();
            mediaPlayer = null;

            initMediaPlayer();

            showAudioPlayerNotification(PlaybackStatus.PLAYING);
        }
    };

    private void RegisterPlayNewAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(Variables.Broadcast_PLAY_NEW_AUDIO);
        Log.e("RegisterPlayNewAudio", "form phone");
        registerReceiver(playNewAudio, filter);
    }
/**
 *                                  <<<<<<<User Interactions>>>>>>>>>>>>>
 * To have full control over media playback in the AudioPlayerService you need to create an instance of MediaSession.
 * MediaSession allows interaction with media controllers, volume keys, media buttons, and transport controls.
 * An app creates an instance of MediaSession when it wants to publish media playback information or handle media keys
 * Notification.MediaStyle allows you to add media buttons without having to create custom notifications.
 * here we will use the MediaStyles support library, NotificationCompat.MediaStyle to support older Android versions
 **/

    /**
     * The String variables are used to notify which action is triggered from the MediaSession callback listener.
     * The rest of the instances relate to the MediaSession and a notification ID to uniquely identify the MediaStyle notification
     */
    public static final String ACTION_PLAY = "com.example.hakeem.audioplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.hakeem.audioplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.example.hakeem.audioplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.example.hakeem.audioplayer.ACTION_NEXT";
    public static final String ACTION_STOP = "com.example.hakeem.audioplayer.ACTION_STOP";

    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    private void initMediaSession() throws RemoteException {

        if (mediaSessionManager != null) return; //mediaSessionManager exists

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        }
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
                showAudioPlayerNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
                showAudioPlayerNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
            }

            @Override
            public void onStop() {
                super.onStop();
                clearAllNotifications();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }


    /**
     * ----------notifications--------------
     */

/*
   * This notification ID can be used to access our notification after we've displayed it. This
   * can be handy when we need to cancel the notification, or perhaps update it. This number is
   * arbitrary and can be set to whatever you like. 1138 is in no way significant.
   */
    private static final int AUDIO_PLAYER_NOTIFICATION_ID = 101;
    /**
     * This notification channel id is used to link notifications to this channel
     */
    private static final String AUDIO_PLAYER_NOTIFICATION_CHANNEL_ID = "audio-player-notification-channel";

    private void clearAllNotifications() {
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancelAll();

        stopSelf();
    }

    public void showAudioPlayerNotification(PlaybackStatus playbackStatus) {

        //  Resources res = context1.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.image);

        PendingIntent play_pauseAction = null;

        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            //create the pause action
            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            //create the play action
            play_pauseAction = playbackAction(0);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            NotificationChannel mChannel = new NotificationChannel(
                    AUDIO_PLAYER_NOTIFICATION_CHANNEL_ID,
                    getString(R.string.audio_player_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), AUDIO_PLAYER_NOTIFICATION_CHANNEL_ID)

                .setShowWhen(false)

                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()

//                        // Attach our MediaSession token
                        .setMediaSession(mediaSession.getSessionToken())
//                        // Show our playback controls in the compact notification view.
                        .setShowActionsInCompactView(0, 1))

                .setColor(getResources().getColor(R.color.colorPrimary))

                .setLargeIcon(largeIcon)

                .setSmallIcon(android.R.drawable.stat_sys_headset)
////
                /**
                 * to force appearing player controls in notification if user enable hide content of notifications
                 * */
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                .setDeleteIntent(playbackAction(4))

                /**
                 * prevent user from swap left or right to delete notification
                 * it will disappear itself if track end or press cancel button on it
                 */
                .setAutoCancel(false)
                .setOngoing(true)
/////
                .setContentTitle(getString(R.string.app_name))
                .setContentText(Variables.statueName)

                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "exit", playbackAction(4))
                .addAction(notificationAction, "pause", play_pauseAction)


                .setContentIntent(contentIntent(this));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        assert notificationManager != null;

        notificationManager.notify(AUDIO_PLAYER_NOTIFICATION_ID, notificationBuilder.build());
        Log.e("11", "1");
    }


    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, AudioPlayerService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(Variables.ACTION_PLAY);
                AudioPlayerActivity.playPause.setText(getString(R.string.play));
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(Variables.ACTION_PAUSE);
                AudioPlayerActivity.playPause.setText(getResources().getString(R.string.pause));
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
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
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }


    private static PendingIntent contentIntent(Context context) {
        Log.e("content intent", "hello");
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

    @SuppressLint("SetTextI18n")
    public void handleIncomingActions(Intent playbackAction) {
//        Log.e("handleIncomingActions" , playbackAction.getAction().toString());
        if (playbackAction == null || playbackAction.getAction() == null) return;


        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {

            playPause();

            showAudioPlayerNotification(PlaybackStatus.PLAYING);

        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {

            playPause();

            showAudioPlayerNotification(PlaybackStatus.PAUSED);


        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {

            transportControls.skipToNext();

        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {

            transportControls.skipToPrevious();

        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {

            Log.e("handleIncomingActions", "stop");
            transportControls.stop();
            AudioPlayerActivity.seekBar.setProgress(0);
            AudioPlayerActivity.mDue.setText("00:00");
            AudioPlayerActivity.mPass.setText("00:00");

            //////////////////////////////////////////////////////////////////////////
            stopAll();
        }
    }


    /**
     * next method to control media player(resume & pause)
     * it's work affects on notification and vice versa
     **/
    public void playPause() {

        if (mediaPlayer.isPlaying()) {
            pauseMedia();
            showAudioPlayerNotification(PlaybackStatus.PAUSED);
            AudioPlayerActivity.playPause.setText(getString(R.string.play));
        } else if (!mediaPlayer.isPlaying()) {
            playMedia();
            showAudioPlayerNotification(PlaybackStatus.PLAYING);
            AudioPlayerActivity.playPause.setText(getString(R.string.pause));
        }

    }

    public void stopAll() {
        stopSelf();

        stopMedia();

        mediaPlayer.release();
        mediaPlayer = null;
        audioManager.abandonAudioFocus(this);
        mediaSessionManager = null;

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    /**
     * Invoked when the audio focus of the system is updated
     * To ensure this good user experience the we will have to handle AudioFocus events
     * and these are handled in the coming method.
     * This method is a switch statement with the focus events as its case:s.
     * Keep in mind that this override method is called after a request for AudioFocus has been made from the system or another media app
     * ----------Cases we have-----------
     * AudioManager.AUDIOFOCUS_GAIN --> The service gained audio focus, so it needs to start playing.
     * AudioManager.AUDIOFOCUS_LOSS --> The service lost audio focus, the user probably moved to playing media on another app,
     * so release the media player.
     * AudioManager.AUDIOFOCUS_LOSS_TRANSIENT --> focus lost for a short time, pause the MediaPlayer.
     * AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK --> Lost focus for a short time, probably a notification arrived on the device,
     * lower the playback volume.
     */

    @Override
    public void onAudioFocusChange(int focusState) {

        Log.e("onAudioFocusChange", "service");
        switch (focusState) {

            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null)
                    initMediaPlayer();

                else if (!mediaPlayer.isPlaying())
                    mediaPlayer.start();

                mediaPlayer.setVolume(1.0f, 1.0f);

                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();

                mediaPlayer.release();

                mediaPlayer = null;
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying())
                    mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    /**
     * Handle errors
     * Invoked when there has been an error during an asynchronous operation.
     */
    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.e("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.e("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.e("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    /**
     * Invoked to communicate some info
     */
    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.e("onPrepared", "player");
        playMedia();
        showAudioPlayerNotification(PlaybackStatus.PLAYING);
    }

    /**
     * Invoked indicating the completion of a seek operation
     */
    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }


    /**
     * used to handel and control seek bar movement
     */
    private Runnable mRunnable;
    private Handler mHandler = new Handler();
    private int duration;// = audioFilePlayer.mediaPlayer.getDuration() / 1000; // In milliseconds

    //@SuppressLint("ResourceAsColor")
    public void initializeSeekBar() {
        //  MainActivity.seekBar.setMax   (audioFilePlayer.mediaPlayer.getDuration() / 1000);
        AudioPlayerActivity.seekBar.setBackgroundColor(getResources().getColor(R.color.fifth));
        Variables.boolInitializeSeekBar = true;

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000; // In milliseconds
                    Variables.CurrentPosition = mCurrentPosition;
                    AudioPlayerActivity.seekBar.setProgress(mCurrentPosition);

                    getAudioStats();
                    Log.e("in initializeSeekBar", valueOf(mCurrentPosition));
                    Log.e("thread", "started");
                }
                mHandler.postDelayed(mRunnable, 1000);
            }
        };
        mHandler.postDelayed(mRunnable, 1000);
    }


    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    protected void getAudioStats() {

        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
        mmr.setDataSource(Variables.completeAudioFilePath);
        mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
        mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
        long duration = Long.parseLong(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
        duration = duration / 1000;
        Variables.trackDuration = duration;
        long minute = duration / (60);
        long second = duration - (minute * 60);
        mmr.release();


        @SuppressLint("DefaultLocale") String y = format("%02d:%02d", minute, second);

        // AudioPlayerActivity.mDue.setText(minute+":"+second);

        AudioPlayerActivity.mDue.setText(y);


        long timePassedSeconds = (mediaPlayer.getCurrentPosition() / 1000);


        @SuppressLint("DefaultLocale") String x = format("%02d:%02d", timePassedSeconds / 60, timePassedSeconds % 60);

        AudioPlayerActivity.mPass.setText(x);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*Log.e("onStartCommand", intent.getAction());
        if (intent.getAction() == null)*/
        //  Log.e("onStartCommand", intent.getAction());
        //Request audio focus
        if (!requestAudioFocus()) {
            //Could not gain focus
            stopSelf();
        }

        if (mediaSessionManager == null) {
            try {
                initMediaSession();

                showAudioPlayerNotification(PlaybackStatus.PLAYING);

                initMediaPlayer();

                Log.e("onStartCommand", "after initMediaPlayer");
            } catch (RemoteException e) {
                Log.e("onStart ", "catch ");
                e.printStackTrace();
                stopSelf();
            }
            // mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PLAYING);
        }
        getAudioStats();
        AudioPlayerActivity.seekBar.setMax((int) Variables.trackDuration);

        duration = mediaPlayer.getDuration() / 1000;
        Log.e("onStartDuration is ", valueOf((duration)));
        initializeSeekBar();

        AudioPlayerActivity.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer != null && b) {
                    mediaPlayer.seekTo(i * 1000);
                    Log.e("onProgressChanged", valueOf((i)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Handle Intent action from MediaSession.TransportControls
        handleIncomingActions(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * In this method the MediaPlayer resources must be released,
     * as this service is about to be destroyed and there is no need for the app to control the media resources
     * When the Service is destroyed it must stop listening to incoming calls
     * and release the TelephonyManager resources.
     * Another final thing the Service handles before it’s destroyed is
     * clearing the data stored in the SharedPreferences
     **/
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
            mediaPlayer = null;
            mediaSessionManager = null;
        }
        removeAudioFocus();
        //Disable the PhoneStateListener
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        clearAllNotifications();

        //unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewAudio);

        AudioPlayerActivity.seekBar.setProgress(0);
        Log.e("onDestroy", "service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("onCreate", "service");

        // Perform one-time setup procedures

        // Manage incoming phone calls during playback.
        // Pause MediaPlayer on incoming call,
        // Resume on hangup.
        callStateListener();
        //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
        registerBecomingNoisyReceiver();
        //Listen for new Audio to play -- BroadcastReceiver
        RegisterPlayNewAudio();
    }


    public class LocalBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }
}
