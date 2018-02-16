package com.example.hakeem.demo.utilities;

/**
 * Created by hakeem on 2/1/18.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import com.example.hakeem.demo.AudioPlayerActivity;
import com.example.hakeem.demo.MainActivity;
import com.example.hakeem.demo.R;

import java.io.IOException;


public class AudioFilePlayer implements
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener {
    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

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
    public void onPrepared(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }


//    /**
//     * <<<<<<<<<<<<<<<<<<<<User Defined Vars>>>>>>>>>>>>>>>>>>>>//
//     */
//    public MediaPlayer mediaPlayer;
//
//    //Used to pause/resume MediaPlayer
//    private int resumePosition;
//
//    public AudioManager audioManager;
//
//    //MediaSession
//    public MediaSessionManager mediaSessionManager;
//    public MediaSessionCompat mediaSession;
//    public MediaControllerCompat.TransportControls transportControls;
//
//    private Context context = AudioPlayerActivity.fa;
//
//
//    private MediaPlayerNotification mediaPlayerNotification = new MediaPlayerNotification();
//    /**
//     * <<<<<<<<<<<<<<<<<<<<User Defined Methods>>>>>>>>>>>>>>>>>>>>
//     */
//
//
//    /**
//     * if couldn't initialize media player will return false to stopSelf of service
//     */
//    public void initMediaPlayer() {
//
//
//        boolean isInitialized = false;
//        //mediaFile = "http://192.168.1.3/guidak_audio_files/arabic/Neferefre_arabic.mp3";
//        Log.e(" initMediaPlayer", "1 ");
//        mediaPlayer = new MediaPlayer();
//        Log.e(" initMediaPlayer", "2 ");
//        //Set up MediaPlayer event listeners
//        mediaPlayer.setOnCompletionListener(this);
//        Log.e(" initMediaPlayer", "3");
//        mediaPlayer.setOnErrorListener(this);
//        Log.e(" initMediaPlayer", "4 ");
//        mediaPlayer.setOnPreparedListener(this);
//        Log.e(" initMediaPlayer", "5 ");
//        mediaPlayer.setOnBufferingUpdateListener(this);
//        Log.e(" initMediaPlayer", "6 ");
//        mediaPlayer.setOnSeekCompleteListener(this);
//        Log.e(" initMediaPlayer", "7");
//        mediaPlayer.setOnInfoListener(this);
//        Log.e(" initMediaPlayer", "8");
//        //Reset so that the MediaPlayer is not pointing to another data source
//        mediaPlayer.reset();
//        Log.e(" initMediaPlayer", "9 and it is equal " + Variables.completeAudioFilePath + "*");
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//        try {
//
//            //mediaPlayer.setDataSource("http://192.168.1.3/guidak_audio_files/arabic/Neferefre_arabic.mp3");
//            mediaPlayer.setDataSource(Variables.completeAudioFilePath);
//            Log.e(" set data source", " to " + Variables.completeAudioFilePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e(" catch", "1");
//        }
//
//        mediaPlayer.prepareAsync();
//        //return isInitialized;
//    }
//
//    public void playMedia() {
//        if (!mediaPlayer.isPlaying()) {
//            mediaPlayer.start();
//        }
//    }
//
//    public void stopMedia() {
//        if (mediaPlayer == null) return;
//        if (mediaPlayer.isPlaying()) {
//            mediaPlayer.stop();
//        }
//    }
//
//    public void pauseMedia() {
//        if (mediaPlayer.isPlaying()) {
//            Log.e("pause", "pauseMedia");
//            mediaPlayer.pause();
//            resumePosition = mediaPlayer.getCurrentPosition();
//            Log.e("resumePosition ", "= " + resumePosition);
//        }
//    }
//
//    public void resumeMedia() {
//        if (!mediaPlayer.isPlaying()) {
//            Log.e("resume", "resumeMedia");
//            mediaPlayer.seekTo(resumePosition);
//            mediaPlayer.start();
//            Log.e("resumePosition ", "= " + resumePosition);
//        }
//    }
//
//    public void resetPlayer() {
//        if (mediaPlayer != null)
//            mediaPlayer.reset();
//    }
//
////
////    public void initMediaPlayerSession() throws RemoteException {
////
////       // if (mediaSessionManager != null) return; //mediaSessionManager exists
////
////        /////
////
////
////
//////       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//////            mediaSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
//////        }
////        // Create a new MediaSession
////        mediaSession = new MediaSessionCompat(context.getApplicationContext(), "AudioPlayer");
////        //Get MediaSessions transport controls
////        transportControls = mediaSession.getController().getTransportControls();
////        //set MediaSession -> ready to receive media commands
////        mediaSession.setActive(true);
////        //indicate that the MediaSession handles transport control commands
////        // through its MediaSessionCompat.Callback.
////        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
////
////        //Set mediaSession's MetaData
////        //updateMetaData();
////
////    }
////
////    public void updateMetaData() {
////        Bitmap albumArt = BitmapFactory.decodeResource(context.getResources(),
////                R.drawable.image); //replace with medias albumArt
////        // Update the current metadata
////        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
////                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
//////                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getArtist())
//////                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.getAlbum())
//////                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getTitle())
////                .build());
////    }
//
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
//
//
//    /**
//     * Invoked indicating buffering status of
//     * a media resource being streamed over the network.
//     */
//    @Override
//    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
//
//        Log.e("onBufferingUpdate", "player");
//    }
//
//    /**
//     * Invoked when playback of a media source has completed.
//     */
//    @Override
//    public void onCompletion(MediaPlayer mediaPlayer) {
//
//        MediaPlayerNotification.clearAllNotifications(context);
//        Log.e("onCompletion", "player");
//        stopMedia();
//        Intent intent = new Intent(context, MediaPlayerService.class);
//        context.stopService(intent);
//
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
//     * Invoked when the media source is ready for playback
//     */
//    @Override
//    public void onPrepared(MediaPlayer mediaPlayer) {
//        Log.e("onPrepared", "player");
//        playMedia();
//        mediaPlayerNotification.showAudioPlayerNotification(context, PlaybackStatus.PLAYING);
//
//    }
//
//    /**
//     * Invoked indicating the completion of a seek operation
//     */
//    @Override
//    public void onSeekComplete(MediaPlayer mediaPlayer) {
//
//    }
}
