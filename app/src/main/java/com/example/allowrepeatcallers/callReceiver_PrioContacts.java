package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.quietexceptional.R;


public class callReceiver_PrioContacts extends BroadcastReceiver {
    ringtones exceptionally_Ringtone;
    public static MediaPlayer local_mp;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            if (!utilityHelpers.ispermissionpending(context, feat_PrioContacts.permissions)) {
                feat_PrioContacts obj_silentExceptions=new feat_PrioContacts(context);
            if (obj_silentExceptions.isFeatureActivated(context)) {
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                //if (!state.equals(mLastState)) {
                Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                if (number != null) {
                    exceptionally_Ringtone = new ringtones(context, obj_silentExceptions.getFeat_ID(),ringtoneUri.toString());

                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        if ((AudioManager.RINGER_MODE_NORMAL != am.getRingerMode())) {
                            getCurrentSettings(context);
                            Boolean MatchfoundinList = obj_silentExceptions.isnumberinList( number);
                            if (MatchfoundinList) {
                                if (!(exceptionally_Ringtone.isRingtonePlaying())) {
                                    int seekbarVolume=obj_silentExceptions.getPrioContactsVolume(context);
                                    am.setStreamVolume(AudioManager.STREAM_MUSIC, seekbarVolume, 0);

                                    local_mp = exceptionally_Ringtone.playLongtune(context);
                                    obj_silentExceptions.setSilentExceptionRingActivated(true);
                                } else {
                                }
                                feat_PrioContacts.silentExceptionActivated = true;
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
            Toast.makeText(context, R.string.exception_on_receive_call, Toast.LENGTH_SHORT).show();
        }
    }


    private void handleReset(Context context, feat_PrioContacts obj_silentExceptions) {
        exceptionally_Ringtone.setMyRingerisplaying(false);
        if(obj_silentExceptions.isSilentExceptionRingActivated()) {
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, ringtones.getCurrent_MediaVolume(context), 0);
            exceptionally_Ringtone.stoptune(context,local_mp);
            obj_silentExceptions.setSilentExceptionRingActivated(false);
        }
    }

    private void resetRingerSettings(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(feat_PrioContacts.ringerMode);
        am.setStreamVolume(AudioManager.STREAM_RING, feat_PrioContacts.Current_RingVolume, 0);
        if (feat_PrioContacts.ringerMode == AudioManager.RINGER_MODE_SILENT) {
            NotificationManager Notimanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notimanager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            am.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
    }


    private void getCurrentSettings(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        feat_PrioContacts.ringerMode = am.getRingerMode();
        feat_PrioContacts.Current_RingVolume = am.getStreamVolume(AudioManager.STREAM_RING);
    }
}






