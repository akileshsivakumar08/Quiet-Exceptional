package com.example.allowrepeatcallers;

import static android.provider.CallLog.Calls.MISSED_TYPE;
import static java.lang.Thread.sleep;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.CallLog;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;

public class callReceiver_BabyPing extends BroadcastReceiver {
    public static NotificationManager notificationManager;
    public static MediaPlayer mp;

    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        if (!utilityHelpers.ispermissionpending(context, feat_AlertMissedCalls.permissions)) {
        feat_AlertMissedCalls obj_AMC=new feat_AlertMissedCalls(context);
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        if (number != null) {
            boolean ringDevice=utilityHelpers.isDNDOverriden(context);
            if (ringDevice) {
                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    if (obj_AMC.isFeatureActivated(context)) {

                            String logType = getLastCallLog(context);
                            if (logType.equals(String.valueOf(MISSED_TYPE))) {
                                utilityHelpers.turnSpeakerON(am);
                                int setVolume =obj_AMC.getPingVolume(context);
                                am.setStreamVolume(AudioManager.STREAM_MUSIC, setVolume, 0);
                                ringtones shorttune = new ringtones(context, 2);
                                shorttune.playShorttune(context);
                                //am.setStreamVolume(AudioManager.STREAM_MUSIC,Current_MediaVolume,0);
                            }
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
        String sortOrder=utilityHelpers.getSortOrder(context);

        if (sortOrder.equals("lastTime")) {
            cursorCallLogs.moveToLast();
        }
        else if(sortOrder.equals("firstTime")){
            cursorCallLogs.moveToFirst();
        }
        //cursorCallLogs.moveToLast();
        String stringType = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE));
        //Toast.makeText(context,logNumber+ stringType +" and "+ MISSED_TYPE, Toast.LENGTH_SHORT).show();
        return stringType;
    }

}
