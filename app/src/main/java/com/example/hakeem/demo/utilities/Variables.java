package com.example.hakeem.demo.utilities;

import android.graphics.Bitmap;

/**
 * Created by hakeem on 1/25/18.
 */

public final class Variables {

    /**
     * operation type for AsyncTask to fetch an audio file path
     */
    public static String selectAudioFilePathOperation = "selectCorrectAudioFilePath";

    /**
     * other variables
     * */
    public static String audioFileLanguage = "";
    public static String statueName = "";


    /**
     * variables for connection
     * */
    public static String audioFilePath = "";
    private static final String AUTHORITY = "192.168.1.3";         //my IP on my network
    private static final String BASE_SCHEME_URI = "http://" + AUTHORITY;//https://192.168.1.3

    public static String serverUrl = BASE_SCHEME_URI + "/";
    public static String completeAudioFilePath = "";

    /**
     * The String variables are used to notify which action is triggered from the MediaSession callback listener.
     * The rest of the instances relate to the MediaSession and a notification ID to uniquely identify the MediaStyle notification
     */
    public static final String ACTION_PLAY = "com.example.hakeem.audioplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.hakeem.audioplayer.ACTION_PAUSE";
    public static final String ACTION_STOP = "com.example.hakeem.audioplayer.ACTION_STOP";

    /**
     * This string sends broadcast intents to the MediaPlayerService that the user wants to play
     * new audio and has updated the cached index of the audio they want to play.
     * */
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.hakeem.audioplayer.PlayNewAudio";



    public static boolean boolInitializeSeekBar = false;

    /**
     * current position played from track to update seek bar
     * */
    public static int CurrentPosition;

    /**
     * whole track duration to be setMax for seek bar
     * */
    public static long trackDuration;

    /**
     * used to be displayed in view of statue image
     * */
    public static Bitmap statueImage = null;


    public final static String imagePath = "http://" + AUTHORITY + "/images/1.jpg";


    /**
     * {{{{{{{{{{{{{{{Variables new version}}}}}}}}}}}}}}}
     */

    //all variables needed for one object
    public static String audioFilePathX;
    public static String imageFilePathx;
    public static String statueNameX;
    public static String audioFilePathCOl;
    public static String statueNameCol;




}
