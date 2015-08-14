package com.codecanyon.streamradio.v2;

import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by User on 2014.07.11..
 */
class PhoneCallListener extends PhoneStateListener {
    private boolean start=false;
    private boolean notRunWhenStart = true;

    public void onCallStateChanged(int state, String incomingNumber) {
        if (notRunWhenStart)
            notRunWhenStart = false;
        else {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    try {
                        if(start){
                            MusicPlayer.playRealMediaPlayer();
                            start = false;
                        }
                    }catch (Exception e){
                        e.getMessage();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    try {
                        if(MusicPlayer.getMediaPlayer().isPlaying()){
                            MusicPlayer.stopRealMediaPlayer();
                            start = true;
                        }
                    }catch (Exception e){
                        e.getMessage();
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    try {
                        if(MusicPlayer.getMediaPlayer().isPlaying()){
                            MusicPlayer.stopRealMediaPlayer();
                            start = true;
                        }
                    }catch (Exception e){
                        e.getMessage();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}