package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class callReceiver_Exceptionally extends BroadcastReceiver {
    ringtones exceptionally_Ringtone;
    public static MediaPlayer local_mp;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            if (!utilityHelpers.ispermissionpending(context, feat_silentExceptions.permissions)) {
                feat_silentExceptions obj_silentExceptions=new feat_silentExceptions(context);
            if (obj_silentExceptions.isFeatureActivated()) {
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                //if (!state.equals(mLastState)) {
                if (number != null) {
                    exceptionally_Ringtone = new ringtones(context, obj_silentExceptions.getFeat_ID());

                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        Toast.makeText(context, "Ringing", Toast.LENGTH_SHORT).show();
                        if ((AudioManager.RINGER_MODE_NORMAL != am.getRingerMode())) {
                            getCurrentSettings(context);
                            Boolean MatchfoundinList = feat_silentExceptions.loadlistAndSearch(context, number);
                            if (MatchfoundinList) {
                                if (!(exceptionally_Ringtone.isRingtonePlaying())) {

                                    int seekbarVolume = utilityHelpers.loadIntFromMemory(context, "SILEXCEPT_VOLUME", ringtones.getCurrent_MediaVolume());
                                    am.setStreamVolume(AudioManager.STREAM_MUSIC, seekbarVolume, 0);

                                    local_mp = exceptionally_Ringtone.playLongtune(context);
                                    obj_silentExceptions.setSilentExceptionRingActivated(true);
                                } else {
                                }
                                feat_silentExceptions.silentExceptionActivated = true;
                            }
                        }
                    } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        handleReset(context, obj_silentExceptions);
                    } else if ((state.equals(TelephonyManager.EXTRA_STATE_IDLE))) {
                        handleReset(context, obj_silentExceptions);
                    }
                }
                }
            }
        }catch (Exception e) {
            Log.e(TAG, " Exception on receive CALL  " + e);
            Toast.makeText(context, " Exception on receive CALL ", Toast.LENGTH_SHORT).show();
        }
    }


    private void handleReset(Context context,feat_silentExceptions obj_silentExceptions) {
        exceptionally_Ringtone.setMyRingerisplaying(false);
        if(obj_silentExceptions.isSilentExceptionRingActivated()) {
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, ringtones.getCurrent_MediaVolume(), 0);
            exceptionally_Ringtone.stoptune(context,local_mp);
            obj_silentExceptions.setSilentExceptionRingActivated(false);
        }
    }

    private void resetRingerSettings(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(feat_silentExceptions.ringerMode);
        am.setStreamVolume(AudioManager.STREAM_RING, feat_silentExceptions.Current_RingVolume, 0);
        if (feat_silentExceptions.ringerMode == AudioManager.RINGER_MODE_SILENT) {
            NotificationManager Notimanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notimanager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            am.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
    }


    private void getCurrentSettings(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        feat_silentExceptions.ringerMode = am.getRingerMode();
        feat_silentExceptions.Current_RingVolume = am.getStreamVolume(AudioManager.STREAM_RING);
    }
}






