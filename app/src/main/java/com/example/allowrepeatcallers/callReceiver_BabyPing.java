package com.example.allowrepeatcallers;

import static java.lang.Thread.sleep;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class callReceiver_BabyPing extends BroadcastReceiver {
    public static NotificationManager notificationManager;
    public static MediaPlayer mp;
    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        if (number != null) {
            int dndstatus = notificationManager.getCurrentInterruptionFilter();
            boolean overridednd_bool = utilityHelpers.loadBooleanFromMemory(context, "OVERRIDE_DND");
            boolean overriden = false;
            if ((overridednd_bool)) {
                overriden = true;
            } else if ((!overridednd_bool)) {
                if (dndstatus == NotificationManager.INTERRUPTION_FILTER_ALL) {
                    overriden = true;
                } else {
                    overriden = false;
                }
            }
            if (overriden) {
                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    feat_BabyPing.featureActivated = utilityHelpers.loadBooleanFromMemory(context, "IS_BABYPING_ACTIVATED");
                    if (feat_BabyPing.featureActivated) {
                        String logType = getLastCallLog(context);
                        if (logType.equals("3")) {
                                int Current_MediaVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                                utilityHelpers.turnSpeakerON(am);
                                int setVolume = utilityHelpers.loadIntFromMemory(context, "BABYPINGVOLUME", Current_MediaVolume);
                                am.setStreamVolume(AudioManager.STREAM_MUSIC, setVolume, 0);

                                playtune(context, Current_MediaVolume);
                                //am.setStreamVolume(AudioManager.STREAM_MUSIC,Current_MediaVolume,0);
                        }
                    }
                    //  Toast.makeText(context, "This a toast message", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private String getLastCallLog(Context context) {
        // Toast.makeText(context,"sleeping", Toast.LENGTH_SHORT).show();
        try {
            sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        }
        Uri uriCallLogs = Uri.parse("content://call_log/calls");
        Cursor cursorCallLogs = null;
        cursorCallLogs = context.getContentResolver().query(uriCallLogs, null, null, null);
        utilityHelpers.sortOrder=utilityHelpers.loadStringFromMemory(context,"UPTODOWN");
        if (utilityHelpers.sortOrder.equals("lastTime")) {
            cursorCallLogs.moveToLast();
        }
        else if(utilityHelpers.sortOrder.equals("firstTime")){
            cursorCallLogs.moveToFirst();
        }
        //cursorCallLogs.moveToLast();
        String stringType = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE));
        //Toast.makeText(context,logNumber+ stringType +" and "+ MISSED_TYPE, Toast.LENGTH_SHORT).show();
        return stringType;
    }
    private void playtune(Context context,int Current_MediaVolume) {
        Toast.makeText(context,"Playing Ringtext",Toast.LENGTH_LONG).show();
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        mp = MediaPlayer.create(context, R.raw.nokia_sms);
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
