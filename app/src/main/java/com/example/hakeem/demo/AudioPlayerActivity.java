package com.example.hakeem.demo;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hakeem.demo.utilities.AudioPlayerService;
import com.example.hakeem.demo.utilities.Variables;

import java.io.InputStream;


public class AudioPlayerActivity extends AppCompatActivity {


    //status of the Service
    public static boolean serviceBound = false;
    private AudioPlayerService player;

    @SuppressLint("StaticFieldLeak")
    public static Button playPause;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            AudioPlayerService.LocalBinder binder = (AudioPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(AudioPlayerActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private void playAudio() {
        Log.e("playAudio ", "& serviceBound is " + serviceBound);

        Intent playerIntent = new Intent(this, AudioPlayerService.class);
        Log.e("playAudio", "before start");
        startService(playerIntent);
        Log.e("playAudio", "after start");
        bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.e("playAudio", "after bind");

    }

    @SuppressLint("StaticFieldLeak")
    public static SeekBar seekBar;
    @SuppressLint("StaticFieldLeak")
    public static TextView mPass;
    @SuppressLint("StaticFieldLeak")
    public static TextView mDue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        mPass = findViewById(R.id.tv_pass);

        mDue = findViewById(R.id.tv_due);

        seekBar = findViewById(R.id.seekBar);

        ImageView statueImage = findViewById(R.id.statue_image);

        playPause = findViewById(R.id.play_pause_btn);

        statueImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(AudioPlayerActivity.this, "clicked", Toast.LENGTH_LONG).show();

                new PhotoFullPopupWindow(AudioPlayerActivity.this, R.layout.popup_photo_full, v, Variables.imagePath, null);
            }
        });

        Log.e("onCreate from : ", "player activity");

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.playPause();
            }
        });
        playAudio();
        new DownloadImageTask(statueImage).execute(Variables.imagePath);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //serviceBound = savedInstanceState.getBoolean("ServiceState");
    }


    @SuppressLint("StaticFieldLeak")
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {

                InputStream in = new java.net.URL(urlDisplay).openStream();

                mIcon11 = BitmapFactory.decodeStream(in);

            } catch (Exception e) {

                Log.e("Error", e.getMessage());

                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);

            Variables.statueImage = result;
        }
        /**
         * source = https://stackoverflow.com/questions/5776851/load-image-from-url
         * */
    }


}
