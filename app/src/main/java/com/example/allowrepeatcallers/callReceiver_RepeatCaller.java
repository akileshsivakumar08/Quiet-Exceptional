package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.provider.CallLog.Calls.MISSED_TYPE;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.err;
import static java.lang.Thread.sleep;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.service.notification.StatusBarNotification;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class callReceiver_RepeatCaller extends BroadcastReceiver {
    private static final UUID ID = null;
    private static String mLastState;
    public static NotificationManager notificationManager;
    private static final String CHANNEL_ID = "Missed Call Notification";
    private static String logNumber;
    private static String logName;
    public static String error;
    public static String flow;
    public static String mode;
    public static MediaPlayer mp;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {

            boolean featureActivated = utilityHelpers.loadBooleanFromMemory(context, "FEAT_REPEATCALLER_ACTIVE");
            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);


            if (featureActivated) {

                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);


                if (number != null) {
                    error = "Number not null";
                    mLastState = state;
                    Log.e(TAG, state);
                    feat_RepeatCaller.missedList = utilityHelpers.loadStringSetFromMemory(context, "STRINGSET_MISSEDLIST");
                    matchActiveNotificationsWithMissedList(context);
                        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                            error = "ringing";
                            feat_RepeatCaller.stateRINGING = true;
                            if ((AudioManager.RINGER_MODE_NORMAL != am.getRingerMode())) {
                                error = "Ringer mode not normal";
                                getCurrentSettings(context);

                                if (feat_RepeatCaller.isNumberRepeatCaller(number)) {
                                    ringtones.initRingtoneProperties(context);

                                        if ((!ringtones.ringtone.isPlaying())) {
                                                error = "Ringer Mode is silent";

                                                int seekbarVolume=utilityHelpers.loadIntFromMemory(context,"ARC_VOLUME",ringtones.Current_MediaVolume);
                                                am.setStreamVolume(AudioManager.STREAM_MUSIC, seekbarVolume, 0);

                                                mp = utilityHelpers.playtune(context);
                                                flow = "Play Tune Silent";
                                                feat_RepeatCaller.repeatCallerRingActivated = "SILENT";
                                        } else {
                                            error = "Ringtone was null";
                                        }
                                    utilityHelpers.saveStringToMemory(context, "errorStr", flow);
                                }
                            }
                        } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                            feat_RepeatCaller.stateRINGING = false;
                            handleReset(context);
                            int numberID = utilityHelpers.listLoopSearch(number, feat_RepeatCaller.missedList);
                            feat_RepeatCaller.nullifyMissedElement(numberID,notificationManager);


                            flow = "Offhook";
                            utilityHelpers.saveStringToMemory(context, "errorStr", flow);
                            utilityHelpers.saveStringSetToMemory(context, "STRINGSET_MISSEDLIST", feat_RepeatCaller.missedList);
                        } else if ((state.equals(TelephonyManager.EXTRA_STATE_IDLE))) {
                            error = "ID:phone state is idle";
                            handleReset(context);
                            if ((feat_RepeatCaller.stateRINGING)) {
                                if (feat_RepeatCaller.featureActivated) {
                                    error = "ID:feature Activated";
                                    String logType = getLastCallLog(context);
                                    if (logType.equals(MISSED_TYPE)) {
                                        error = "ID:missedcall detected";
                                        flow = error;
                                        utilityHelpers.saveStringToMemory(context, "errorStr", flow);
                                        if (!(feat_RepeatCaller.isNumberRepeatCaller(number))) {
                                            error = "ID:number is repeat caller";
                                            feat_RepeatCaller.addToMissedList(logName, logNumber, "Missed");
                                            NotificationManager post_notificationManager = notificationManager;
                                            error = "ID:Created Notification Channel";
                                            int notificationID = ++feat_RepeatCaller.notificationIDcounter;
                                            String NotiString=getNotificationTitleString();


                                            Notification notification = utilityHelpers.buildNotification(NotiString, "Swipe or press Stop to mute next Call", notificationID, context);

                                            post_notificationManager.notify(notificationID, notification);
                                            flow = "Posted Notification";
                                            utilityHelpers.saveStringToMemory(context, "errorStr", flow);
                                            utilityHelpers.saveStringSetToMemory(context, "STRINGSET_MISSEDLIST", feat_RepeatCaller.missedList);
                                        } else {
                                            flow = "Repeated Missed Call";
                                            utilityHelpers.saveStringToMemory(context, "errorStr", flow);
                                        }
                                    }
                                }
                            }
                        }
                }
            }
            } catch(Exception e){
                Log.e(TAG, " Exception on receive CALL  " + e);
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                utilityHelpers.saveStringToMemory(context,"errorStr",error);
                feat_RepeatCaller.missedList.clear();
            }
    }

    private String getNotificationTitleString() {
        String title_string;
        if (logName == null) {
            title_string = logNumber;
        } else {
            title_string = logName;
        }
        return title_string;
    }

    private void matchActiveNotificationsWithMissedList(Context context) {
        StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();

        int numberOfActiveNotifications = activeNotifications.length;
        if ((numberOfActiveNotifications == 0) && (feat_RepeatCaller.missedList.size() > 0)) {
            feat_RepeatCaller.missedList.clear();
            utilityHelpers.saveStringSetToMemory(context, "STRINGSET_MISSEDLIST", feat_RepeatCaller.missedList);
        }
    }


    private String getLastCallLog(Context context) {
        String stringType;
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
        int index_type=cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE);
        int index_number=cursorCallLogs.getColumnIndex(CallLog.Calls.NUMBER);
        int index_name=cursorCallLogs.getColumnIndex(CallLog.Calls.CACHED_NAME);
        if((index_type>0)&&(index_name>0)&&(index_number>0)) {
            stringType = cursorCallLogs.getString(index_type);
            logNumber = cursorCallLogs.getString(index_number);
            logName = cursorCallLogs.getString(index_name);
        }
        else{
            stringType = null;
            logNumber = null;
            logName = null;
            Toast.makeText(context,"Negative column index", Toast.LENGTH_SHORT).show();
        }
        return stringType;
    }

    private void handleReset(Context context) {
                ringtones.myRingerisplaying=false;
                    AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, ringtones.Current_MediaVolume, 0);
                    utilityHelpers.stoptune(context,mp);
    }

    private void getCurrentSettings(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        feat_RepeatCaller.ringerMode=am.getRingerMode();
        feat_RepeatCaller.Current_RingVolume = am.getStreamVolume(AudioManager.STREAM_RING);
    }




}