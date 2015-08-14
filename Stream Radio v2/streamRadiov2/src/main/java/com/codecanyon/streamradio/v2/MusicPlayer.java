package com.codecanyon.streamradio.v2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.flashlight.R;

import java.io.IOException;

import io.vov.vitamio.MediaPlayer;

/**
 * Created by User on 2014.07.03..
 */
public class MusicPlayer {
    private static MediaPlayer mediaPlayer;
    private ConnectivityManager  cm;
    private NetworkInfo netInfo;
    private RadioListElement radioListElement;
    private static String urlUpdater="default";
    private static String location;

    public static String getUrl() {
        return urlUpdater;
    }

    public MusicPlayer(final Context context) {
        mediaPlayer = new MediaPlayer(context);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
            if(i2!=-107){
                cm =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    MainActivity.stopBufferingAnimation();
                    MainActivity.getRadioListLocation().setText("The radio is probably offline.");
                    location = "The radio is probably offline.";
                    MainActivity.getStartOrStopBtn().setImageResource(R.drawable.play);
                }else{
                    MainActivity.stopBufferingAnimation();
                    MainActivity.getRadioListLocation().setText("Internet connection error.");
                    location = "Internet connection error.";
                    MainActivity.getStartOrStopBtn().setImageResource(R.drawable.play);
                }
            }
                return false;
            }
        });
    }

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }


    public static void stopRealMediaPlayer(){
        mediaPlayer.stop();
    }

    public static void playRealMediaPlayer(){
        mediaPlayer.start();
    }

    public void play(RadioListElement rle) {
        radioListElement=rle;
        playMusic(radioListElement.getUrl());
    }

    public static String getLocation() {
        return location;
    }

    public void playMusic(final String url){

        location = radioListElement.getFrequency();
        MainActivity.setViewPagerSwitch();
        MainActivity.startBufferingAnimation();
        MainActivity.getStartOrStopBtn().setImageResource(R.drawable.pause);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            urlUpdater = url;
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    MainActivity.stopBufferingAnimation();
                    //MainActivity.getRadioListLocation().setText(radioListElement.getFrequency());
                    //urlUpdater = url;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
