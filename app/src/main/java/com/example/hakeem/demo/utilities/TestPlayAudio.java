package com.example.hakeem.demo.utilities;

import android.media.MediaPlayer;

/**
 * Created by hakeem on 1/25/18.
 */

public class TestPlayAudio implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
    private String audioURL;
    public MediaPlayer mediaPlayer;

    public TestPlayAudio(String audioURL) {
        this.audioURL = audioURL;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        playAudioFile();
    }


    private void playAudioFile() {
        try {
            mediaPlayer.setDataSource(this.audioURL); // setup song from https://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
            mediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
