package com.example.allowrepeatcallers;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

public class ringtones {
    public static Ringtone ringtone;
    public static boolean myRingerisplaying=false;
    public static Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    public static int Current_MediaVolume;

    public static void playRingtone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if ((ringtones.ringtone != null)&&(!ringtones.ringtone.isPlaying()) ){
                ringtones.ringtone.play();
                myRingerisplaying=true;
            }
        }
    }
    public static void stopPlayingRingtone() {
        if(ringtones.ringtone.isPlaying()) {
            ringtones.ringtone.stop();
            myRingerisplaying=false;
        }
    }
    public static void initRingtoneProperties(Context context){
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtones.ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
        ringtones.Current_MediaVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
    }
}
