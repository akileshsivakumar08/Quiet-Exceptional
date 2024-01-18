package com.example.allowrepeatcallers;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Switch;
import android.widget.Toast;

public class ringtones {

    Ringtone ringtone;
    public void setMyRingerisplaying(boolean myRingerisplaying) {
        this.myRingerisplaying = myRingerisplaying;
    }

    private boolean myRingerisplaying=false;
    private Uri ringtoneUri;
    public static int Current_MediaVolume;
    public static MediaPlayer mp;

    public static int getCurrent_MediaVolume() {
        return Current_MediaVolume;
    }

    boolean isRingtonePlaying(){

        return myRingerisplaying;
    }
    public ringtones(Context context,int feat_ID) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(feat_ID==0){
            mp = MediaPlayer.create(context, R.raw.nokia_sms);

        }
        else if(feat_ID==1) {
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
            mp = MediaPlayer.create(context, ringtoneUri);
        }
        else if(feat_ID==2){
            mp = MediaPlayer.create(context, R.raw.nokia_sms);
        }
        Current_MediaVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    MediaPlayer playLongtune(Context context) {
        Toast.makeText(context,"Playing Ringtext",Toast.LENGTH_LONG).show();

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(!mp.isPlaying()){
            utilityHelpers.turnSpeakerON(am);
            mp.start();
        }
        return mp;
    }
    void stoptune(Context context,MediaPlayer local_mp){
        if(local_mp.isPlaying()) {
            local_mp.stop();
            local_mp.reset();
        }
    }

    public void playShorttune(Context context) {
        Toast.makeText(context,"Playing Ringtext",Toast.LENGTH_LONG).show();
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        //mp = MediaPlayer.create(context, R.raw.nokia_sms);
        if(!mp.isPlaying()){
            mp.start();
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp.stop();
                am.setStreamVolume(AudioManager.STREAM_MUSIC,Current_MediaVolume,0);
            }
        });
    }


}
