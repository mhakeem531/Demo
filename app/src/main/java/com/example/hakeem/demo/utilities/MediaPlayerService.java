package com.example.hakeem.demo.utilities;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.SeekBar;

import com.example.hakeem.demo.AudioPlayerActivity;
import com.example.hakeem.demo.MainActivity;
import com.example.hakeem.demo.R;

import wseemann.media.FFmpegMediaMetadataRetriever;

import static java.lang.String.format;
import static java.lang.String.valueOf;


/**
 * Created by hakeem on 2/1/18.
 */

public class MediaPlayerService extends Service implements
        /*MediaPlayer.OnCompletionListener,*/
        /*MediaPlayer.OnPreparedListener,*/
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener,
        /*MediaPlayer.OnBufferingUpdateListener,*/
        AudioManager.OnAudioFocusChangeListener {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onAudioFocusChange(int i) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }


//    /**
//     * <<<<<<<<<<<<<<<<<<<<User Defined Vars>>>>>>>>>>>>>>>>>>>>//
//     */
//
//    //to have service media player
//    public static AudioFilePlayer audioFilePlayer = new AudioFilePlayer();
//
//    // Binder given to clients
//    private final IBinder iBinder = new LocalBinder();
//
//    //manage appearance of notification
//    MediaPlayerNotification mediaPlayerNotification = new MediaPlayerNotification();
//
//    //context of AudioPlayerActivity
//    public Context context = AudioPlayerActivity.fa;
//
//    public MediaSessionManager mediaSessionManager;
//
//    public static MediaSessionCompat mediaSession;
//    public MediaControllerCompat.TransportControls transportControls;
//
//
//
//    //public Context context = MainActivity.fa;
//
//    //Handle incoming phone calls
//    private boolean ongoingCall = false;
//    private PhoneStateListener phoneStateListener;
//    private TelephonyManager telephonyManager;
//
//    /**
//     * The String variables are used to notify which action is triggered from the MediaSession callback listener.
//     * The rest of the instances relate to the MediaSession and a notification ID to uniquely identify the MediaStyle notification
//     */
//    public static final String ACTION_PLAY = "com.example.hakeem.audioplayer.ACTION_PLAY";
//    public static final String ACTION_PAUSE = "com.example.hakeem.audioplayer.ACTION_PAUSE";
//    public static final String ACTION_PREVIOUS = "com.example.hakeem.audioplayer.ACTION_PREVIOUS";
//    public static final String ACTION_NEXT = "com.example.hakeem.audioplayer.ACTION_NEXT";
//    public static final String ACTION_STOP = "com.example.hakeem.audioplayer.ACTION_STOP";
//
//
//    /**
//     * used to handel and control seek bar movement
//     */
//    public SeekBar seekBar;
//    private Runnable mRunnable;
//    private Handler mHandler = new Handler();
//    private int duration;// = audioFilePlayer.mediaPlayer.getDuration() / 1000; // In milliseconds
//
//    /**
//     * <<<<<<<<<<<<<<<<<<<<Constructors>>>>>>>>>>>>>>>>>>>>
//     */
//
//
//    /**
//     * <<<<<<<<<<<<<<<<<<<<User Defined Methods>>>>>>>>>>>>>>>>>>>>
//     */
//
//    public void stopAll() {
//        Intent intent = new Intent(context, MediaPlayerService.class);
//        context.stopService(intent);
//
//        audioFilePlayer.stopMedia();
//
//        audioFilePlayer.mediaPlayer.release();
//        audioFilePlayer.mediaPlayer = null;
//        audioFilePlayer.audioManager.abandonAudioFocus(this);
//
//
//        mediaSessionManager = null;
//
//    }
//
//
//    /**
//     * the coming two methods will still in service class
//     **/
//    private boolean requestAudioFocus() {
//        audioFilePlayer.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        int result = audioFilePlayer.audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
//        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//            //Focus gained
//            return true;
//        }
//        //Could not gain focus
//        return false;
//    }
//
//    private boolean removeAudioFocus() {
//        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
//                audioFilePlayer.audioManager.abandonAudioFocus(this);
//    }
//
//    /**
//     * when the user unplug his headphone
//     * it is normal to stop media player
//     * ACTION_AUDIO_BECOMING_NOISY which means that the audio is about to become ‘noisy’
//     * due to a change in audio outputs.
//     */
//    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //pause audio on ACTION_AUDIO_BECOMING_NOISY
//            audioFilePlayer.pauseMedia();
//            mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PAUSED);
//        }
//    };
//
//    /**
//     * The BroadcastReceiver instance will pause the MediaPlayer when the system makes
//     * an ACTION_AUDIO_BECOMING_NOISY call.
//     * To make the BroadcastReceiver available you must register it
//     * the coming function handles this and specifies the intent action
//     * BECOMING_NOISY which will trigger this BroadcastReceiver
//     */
//    private void registerBecomingNoisyReceiver() {
//        //register after getting audio focus
//        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
//        registerReceiver(becomingNoisyReceiver, intentFilter);
//    }
//
//    /**
//     *                                      {{{{{Handling Incoming Calls}}}}}
//     **/
//
//    /**
//     * The callStateListener() function is an implementation of the PhoneStateListener
//     * that listens to TelephonyManagers state changes.
//     * TelephonyManager provides access to information about the telephony services on the device and listens for
//     * changes to the device call state and reacts to these changes.
//     */
//    private void callStateListener() {
//
//        // Get the telephony manager
//        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        //Starting listening for PhoneState changes
//        phoneStateListener = new PhoneStateListener() {
//
//            @Override
//            public void onCallStateChanged(int state, String incomingNumber) {
//                switch (state) {
//                    //if at least one call exists or the phone is ringing
//                    //pause the MediaPlayer
//                    case TelephonyManager.CALL_STATE_OFFHOOK:
//                    case TelephonyManager.CALL_STATE_RINGING:
//                        if (audioFilePlayer.mediaPlayer != null) {
//                            audioFilePlayer.pauseMedia();
//                            ongoingCall = true;
//                        }
//                        break;
//                    case TelephonyManager.CALL_STATE_IDLE:
//                        // Phone idle. Start playing.
//                        if (audioFilePlayer.mediaPlayer != null) {
//                            if (ongoingCall) {
//                                ongoingCall = false;
//                                audioFilePlayer.resumeMedia();
//                            }
//                        }
//                        break;
//                }
//            }
//        };
//        // Register the listener with the telephony manager
//        // Listen for changes to the device call state.
//        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
//    }
//
//
//    /**
//     * When the MediaPlayerService is playing something and the user wants to play a new track,
//     * you must notify the service that it needs to move to new audio.
//     * You need a way for the Service to listen to these “play new Audio” calls and act on them
//     */
//    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            //Get the new media index form SharedPreferences
//            stopSelf();
//            //A PLAY_NEW_AUDIO action received
//            //reset mediaPlayer to play the new Audio
//            audioFilePlayer.stopMedia();
//            audioFilePlayer.resetPlayer();
//            audioFilePlayer.mediaPlayer = null;
//
//            audioFilePlayer.initMediaPlayer();
//
//            mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PLAYING);
//        }
//    };
//
//    private void RegisterPlayNewAudio() {
//        //Register playNewMedia receiver
//        IntentFilter filter = new IntentFilter(Variables.Broadcast_PLAY_NEW_AUDIO);
//        Log.e("RegisterPlayNewAudio", "form phone");
//        registerReceiver(playNewAudio, filter);
//    }
//
//    /**
//     *                       <<<<<<<User Interactions>>>>>>>>>>>>>
//     */
//
//    /**
//     * next method to control media player(resume & pause)
//     * it's work affects on notification and vice versa
//     **/
//    public void playPause() {
//
//        if (audioFilePlayer.mediaPlayer.isPlaying()) {
//            audioFilePlayer.pauseMedia();
//            mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PAUSED);
//            AudioPlayerActivity.playPause.setText(context.getString(R.string.play));
//        } else if (!audioFilePlayer.mediaPlayer.isPlaying()) {
//            audioFilePlayer.playMedia();
//            mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PLAYING);
//            AudioPlayerActivity.playPause.setText(context.getString(R.string.pause));
//        }
//
//    }
//
//    /**
//     * To have full control over media playback in the MediaPlayerService you need to create an instance of MediaSession.
//     * MediaSession allows interaction with media controllers, volume keys, media buttons, and transport controls.
//     * An app creates an instance of MediaSession when it wants to publish media playback information or handle media keys
//     * Notification.MediaStyle allows you to add media buttons without having to create custom notifications.
//     * here we will use the MediaStyles support library, NotificationCompat.MediaStyle to support older Android versions
//     **/
//
//
//    private void initMediaSession() throws RemoteException {
//
//        if (mediaSessionManager != null) return;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
//        }
//      //  audioFilePlayer.initMediaPlayerSession();
//
//        mediaSession = new MediaSessionCompat(context.getApplicationContext(), "AudioPlayer");
//        //Get MediaSessions transport controls
//        transportControls = mediaSession.getController().getTransportControls();
//        //set MediaSession -> ready to receive media commands
//        mediaSession.setActive(true);
//        //indicate that the MediaSession handles transport control commands
//        // through its MediaSessionCompat.Callback.
//        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
//
//
//        // Attach Callback to receive MediaSession updates
//        mediaSession.setCallback(new MediaSessionCompat.Callback() {
//            // Implement callbacks
//            @Override
//            public void onPlay() {
//                super.onPlay();
//                audioFilePlayer.resumeMedia();
//                mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PLAYING);
//            }
//
//            @Override
//            public void onPause() {
//                super.onPause();
//                audioFilePlayer.pauseMedia();
//                mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PAUSED);
//            }
//
//            @Override
//            public void onSkipToNext() {
//                super.onSkipToNext();
////                skipToNext();
////                updateMetaData();
////                buildNotification(PlaybackStatus.PLAYING);
//            }
//
//            @Override
//            public void onSkipToPrevious() {
//                super.onSkipToPrevious();
////                skipToPrevious();
////                updateMetaData();
////                buildNotification(PlaybackStatus.PLAYING);
//            }
//
//            @Override
//            public void onStop() {
//                super.onStop();
//                MediaPlayerNotification.clearAllNotifications(context);
//                //Stop the service
//                stopSelf();
//            }
//
//            @Override
//            public void onSeekTo(long position) {
//                super.onSeekTo(position);
//            }
//        });
//    }
//
//    public void handleIncomingActions(Intent playbackAction) {
////        Log.e("handleIncomingActions" , playbackAction.getAction().toString());
//        if (playbackAction == null || playbackAction.getAction() == null) return;
//
//
//        String actionString = playbackAction.getAction();
//        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
//
//            // audioFilePlayer.transportControls.play();
////            int x = audioFilePlayer.mediaPlayer.getCurrentPosition();
////            audioFilePlayer.mediaPlayer.seekTo(x);
////            audioFilePlayer.playMedia();
////            mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PLAYING);
////           // audioFilePlayer.playMedia();
////            Log.e("handleIncomingActions", "play");
////            Log.e("seek= ", " " + x);
//
//
////            audioFilePlayer.resumeMedia();
////            mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PLAYING);
//
//            playPause();
//
//            mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PLAYING);
//
//        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
//
////            audioFilePlayer.pauseMedia();
////            mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PAUSED);
////           // audioFilePlayer.transportControls.pause();
////            Log.e("handleIncomingActions", "pause");
//
//
////            audioFilePlayer.pauseMedia();
////            mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PAUSED);
//
//
//            playPause();
//
//            mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PAUSED);
//
//
//        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
//
//            audioFilePlayer.transportControls.skipToNext();
//
//        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
//
//            audioFilePlayer.transportControls.skipToPrevious();
//
//        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
//
//            Log.e("handleIncomingActions", "stop");
//            audioFilePlayer.transportControls.stop();
//            AudioPlayerActivity.seekBar.setProgress(0);
//
//            //////////////////////////////////////////////////////////////////////////
//            stopAll();
//        }
//    }
//
//
//    /**
//     * <<<<<<<<<<<<<<<<<<<<Overrides Methods>>>>>>>>>>>>>>>>>>>>
//     */
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return iBinder;
//    }
//
//    /**
//     * Invoked when the audio focus of the system is updated
//     * To ensure this good user experience the we will have to handle AudioFocus events
//     * and these are handled in the coming method.
//     * This method is a switch statement with the focus events as its case:s.
//     * Keep in mind that this override method is called after a request for AudioFocus has been made from the system or another media app
//     * ----------Cases we have-----------
//     * AudioManager.AUDIOFOCUS_GAIN --> The service gained audio focus, so it needs to start playing.
//     * AudioManager.AUDIOFOCUS_LOSS --> The service lost audio focus, the user probably moved to playing media on another app,
//     * so release the media player.
//     * AudioManager.AUDIOFOCUS_LOSS_TRANSIENT --> focus lost for a short time, pause the MediaPlayer.
//     * AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK --> Lost focus for a short time, probably a notification arrived on the device,
//     * lower the playback volume.
//     */
//    @Override
//    public void onAudioFocusChange(int focusState) {
//
//        Log.e("onAudioFocusChange", "service");
//        switch (focusState) {
//
//            case AudioManager.AUDIOFOCUS_GAIN:
//                // resume playback
//                if (audioFilePlayer.mediaPlayer == null)
//                    audioFilePlayer.initMediaPlayer();
//
//                else if (!audioFilePlayer.mediaPlayer.isPlaying())
//                    audioFilePlayer.mediaPlayer.start();
//
//                audioFilePlayer.mediaPlayer.setVolume(1.0f, 1.0f);
//
//                break;
//
//            case AudioManager.AUDIOFOCUS_LOSS:
//                // Lost focus for an unbounded amount of time: stop playback and release media player
//                if (audioFilePlayer.mediaPlayer.isPlaying())
//                    audioFilePlayer.mediaPlayer.stop();
//
//                audioFilePlayer.mediaPlayer.release();
//
//                audioFilePlayer.mediaPlayer = null;
//                break;
//
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                // Lost focus for a short time, but we have to stop
//                // playback. We don't release the media player because playback
//                // is likely to resume
//                if (audioFilePlayer.mediaPlayer.isPlaying())
//                    audioFilePlayer.mediaPlayer.pause();
//                break;
//
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                // Lost focus for a short time, but it's ok to keep playing
//                // at an attenuated level
//                if (audioFilePlayer.mediaPlayer.isPlaying())
//                    audioFilePlayer.mediaPlayer.setVolume(0.1f, 0.1f);
//                break;
//        }
//    }
//
//    /**
//     * Handle errors
//     * Invoked when there has been an error during an asynchronous operation.
//     */
//    @Override
//    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
//        switch (what) {
//            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
//                Log.e("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
//                break;
//            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
//                Log.e("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
//                break;
//            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
//                Log.e("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
//                break;
//        }
//        return false;
//    }
//
//    /**
//     * Invoked to communicate some info
//     */
//    @Override
//    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
//        return false;
//    }
//
//    /**
//     * Invoked indicating the completion of a seek operation
//     */
//    @Override
//    public void onSeekComplete(MediaPlayer mediaPlayer) {
//
//    }
//
//    public class LocalBinder extends Binder {
//        public MediaPlayerService getService() {
//            return MediaPlayerService.this;
//        }
//    }
//
//
//    @SuppressLint("ResourceAsColor")
//    public void initializeSeekBar() {
//        //  MainActivity.seekBar.setMax   (audioFilePlayer.mediaPlayer.getDuration() / 1000);
//        AudioPlayerActivity.seekBar.setBackgroundColor(R.color.fifth);
//        Variables.boolInitializeSeekBar = true;
//
//        mRunnable = new Runnable() {
//            @Override
//            public void run() {
//                if (audioFilePlayer.mediaPlayer != null) {
//                    int mCurrentPosition = audioFilePlayer.mediaPlayer.getCurrentPosition() / 1000; // In milliseconds
//                    Variables.CurrentPosition = mCurrentPosition;
//                    AudioPlayerActivity.seekBar.setProgress(mCurrentPosition);
//
//                    getAudioStats();
//                    Log.e("in initializeSeekBar", valueOf(mCurrentPosition));
//                    Log.e("thread", "started");
//                }
//                mHandler.postDelayed(mRunnable, 1000);
//            }
//        };
//        mHandler.postDelayed(mRunnable, 1000);
////        Thread thread = new Thread(mRunnable);
////        thread.setPriority(Thread.MAX_PRIORITY);
////        thread.start();
//
//    }
//
//
//    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
//    protected void getAudioStats() {
//
//        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
//        mmr.setDataSource(Variables.completeAudioFilePath);
//        mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
//        mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
//        long duration = Long.parseLong(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
//        duration = duration / 1000;
//        Variables.trackDuration = duration;
//        long minute = duration / (60);
//        long second = duration - (minute * 60);
//        mmr.release();
//
//
//        @SuppressLint("DefaultLocale") String y = format("%02d:%02d", minute, second);
//
//        // AudioPlayerActivity.mDue.setText(minute+":"+second);
//
//        AudioPlayerActivity.mDue.setText(y);
//
//
//        long timePassedSeconds = (audioFilePlayer.mediaPlayer.getCurrentPosition() / 1000);
//
//
//        @SuppressLint("DefaultLocale") String x = format("%02d:%02d", timePassedSeconds / 60, timePassedSeconds % 60);
//
//        AudioPlayerActivity.mPass.setText(x);
//
//    }
//
//
//    /**
//     *                 <<<<<<<<<<<<<<<<<Service lifecycle>>>>>>>>>>>>>>>>>>>>>>>>
//     */
//
//    /**
//     * The onStartCommand() handles the initialization of the MediaPlayer and the focus request to make sure there are no other apps playing media.
//     * an extra try-catch block is added  to make sure the getExtras() method doesn’t throw a NullPointerException.
//     **/
//    //The system calls this method when an activity, requests the service be started
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        Log.e("onStartCommand", "service");
//        if (intent.getAction() == null)
//            //  Log.e("onStartCommand", intent.getAction());
//            //Request audio focus
//            if (!requestAudioFocus()) {
//                //Could not gain focus
//                stopSelf();
//            }
//
//        if (mediaSessionManager == null) {
//            try {
//                initMediaSession();
//
//                mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PLAYING);
//
//                audioFilePlayer.initMediaPlayer();
//                getAudioStats();
//                AudioPlayerActivity.seekBar.setMax((int) Variables.trackDuration);
//
//                duration = audioFilePlayer.mediaPlayer.getDuration() / 1000;
//                Log.e("onStartDuration is ", valueOf((duration)));
//                // Get the current audio stats
//                // getAudioStats();
//                // Initialize the seek bar
//                initializeSeekBar();
//
//                //TODO come here if notification has bug
//                // mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PLAYING);
//
//
//                AudioPlayerActivity.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                        if (MediaPlayerService.audioFilePlayer.mediaPlayer != null && b) {
//                            MediaPlayerService.audioFilePlayer.mediaPlayer.seekTo(i * 1000);
//                            Log.e("onProgressChanged", valueOf((i)));
//                        }
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//
//                    }
//                });
//
//
//                Log.e("onStartCommand", "after initMediaPlayer");
//            } catch (RemoteException e) {
//                Log.e("onStart ", "catch ");
//                e.printStackTrace();
//                stopSelf();
//            }
//            // mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PLAYING);
//        }
//
//        //Handle Intent action from MediaSession.TransportControls
//        handleIncomingActions(intent);
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//
//    /**
//     * In this method the MediaPlayer resources must be released,
//     * as this service is about to be destroyed and there is no need for the app to control the media resources
//     * When the Service is destroyed it must stop listening to incoming calls
//     * and release the TelephonyManager resources.
//     * Another final thing the Service handles before it’s destroyed is
//     * clearing the data stored in the SharedPreferences
//     **/
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (audioFilePlayer.mediaPlayer != null) {
//            audioFilePlayer.stopMedia();
//            audioFilePlayer.mediaPlayer.release();
//            audioFilePlayer.mediaPlayer = null;
//            mediaSessionManager = null;
//        }
//        removeAudioFocus();
//        //Disable the PhoneStateListener
//        if (phoneStateListener != null) {
//            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
//        }
//
//        MediaPlayerNotification.clearAllNotifications(context);
//
//
//        //unregister BroadcastReceivers
//        unregisterReceiver(becomingNoisyReceiver);
//        unregisterReceiver(playNewAudio);
//
//        AudioPlayerActivity.seekBar.setProgress(0);
//        Log.e("onDestroy", "service");
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Log.e("onCreate", "service");
//
//        // Perform one-time setup procedures
//
//        // Manage incoming phone calls during playback.
//        // Pause MediaPlayer on incoming call,
//        // Resume on hangup.
//        callStateListener();
//        //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
//        registerBecomingNoisyReceiver();
//        //Listen for new Audio to play -- BroadcastReceiver
//        RegisterPlayNewAudio();
//    }
}

