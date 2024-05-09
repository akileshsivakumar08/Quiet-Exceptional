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
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class callReceiver_AlertMissedCalls extends BroadcastReceiver {
    private static final String TAG = "QuietExceptional";
    public static NotificationManager notificationManager;
    public static MediaPlayer mp;

    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        String ErrorFlow="Init";
        try {
            if (!utilityHelpers.ispermissionpending(context, feat_AlertMissedCalls.permissions)) {
                ErrorFlow="CR_AMC_Permissions Provided";
                feat_AlertMissedCalls obj_AMC = new feat_AlertMissedCalls(context);
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                ErrorFlow="CR_AMC_Init Complete";
                if (number != null) {
                    boolean ringDevice = utilityHelpers.isDNDOverriden(context);
                    if (ringDevice) {
                        ErrorFlow="CR_AMC_Ring Device Activated";
                        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                            if ((am.getMode() != AudioManager.MODE_IN_COMMUNICATION) && (am.getMode() != AudioManager.MODE_IN_CALL)) {
                                ErrorFlow="CR_AMC_No Active calls";
                                if (obj_AMC.isFeatureActivated(context)) {
                                    ErrorFlow="Feature Activated";
                                    String logType = getLastCallLog(context);
                                    if (logType.equals(String.valueOf(MISSED_TYPE))) {
                                        ErrorFlow="CR_AMC_Missed Call Detected";
                                        utilityHelpers.turnSpeakerON(am);
                                        int setVolume = obj_AMC.getPingVolume(context);
                                        am.setStreamVolume(AudioManager.STREAM_MUSIC, setVolume, 0);
                                        ringtones shorttune = new ringtones(context, 2);
                                        if (utilityHelpers.isDeviceConnected(am)) {
                                            ErrorFlow="CR_AMC_Playing on connected Device";
                                            am.setStreamVolume(AudioManager.STREAM_MUSIC, (am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) / 2, 0);
                                        }

                                        shorttune.playShorttune(context);
                                        //am.setStreamVolume(AudioManager.STREAM_MUSIC,Current_MediaVolume,0);
                                    }
                                }
                            }
                        }
                        //  Toast.makeText(context, "This a toast message", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG,ErrorFlow);
            utilityHelpers.saveErrorToMemory(context,ErrorFlow);
            throw new RuntimeException(e);
        }

    }




    private String getLastCallLog(Context context) {
        // Toast.makeText(context,"sleeping", Toast.LENGTH_SHORT).show();
        try {
            sleep(200);
        } catch (Exception e) {
            String ErrorFlow="CR_AMC_SleepInterrupted";
            Log.e(TAG,ErrorFlow);
            utilityHelpers.saveErrorToMemory(context,ErrorFlow);
            throw new RuntimeException(e);

        }
        Cursor cursorCallLogs = null;
        try {
            Uri uriCallLogs = Uri.parse("content://call_log/calls");

            cursorCallLogs = context.getContentResolver().query(uriCallLogs, null, null, null);
        }
        catch (Exception e) {
            String ErrorFlow="CR_AMC_Error Getting Call Logs";
            Log.e(TAG,ErrorFlow);
            utilityHelpers.saveErrorToMemory(context,ErrorFlow);
            //Error Getting Call Logs
            throw new RuntimeException(e);
        }
        try {
            String sortOrder = utilityHelpers.getSortOrder(context);

            if (sortOrder.equals("date DESC")) {
                cursorCallLogs.moveToLast();
            } else if (sortOrder.equals("date ASC")) {
                cursorCallLogs.moveToFirst();
            } else {
                Toast.makeText(context, "Error:Sort Order Not initialized", Toast.LENGTH_SHORT).show();
            }
            //cursorCallLogs.moveToLast();
            String stringType = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE));
            return stringType;
        }
        catch (Exception e) {
            //Error getting type of last call log.
            String ErrorFlow="CR_AMC_Error getting type of last call log.";
            Log.e(TAG,ErrorFlow);
            utilityHelpers.saveErrorToMemory(context,ErrorFlow);
            throw new RuntimeException(e);
        }
        //Toast.makeText(context,logNumber+ stringType +" and "+ MISSED_TYPE, Toast.LENGTH_SHORT).show();

    }

}
