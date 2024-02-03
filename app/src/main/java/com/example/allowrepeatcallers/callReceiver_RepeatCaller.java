package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.provider.CallLog.Calls.MISSED_TYPE;

import static java.lang.Thread.sleep;

import android.app.Notification;
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

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.time.Duration;
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
    ringtones repeatCaller_Ringtone;
    public static MediaPlayer local_mp;

    @Override
    public void onReceive(Context context, Intent intent) {
        feat_RepeatCaller obj_RepeatCaller=new feat_RepeatCaller(context);
        try {
            if (!utilityHelpers.ispermissionpending(context, feat_RepeatCaller.permissions)) {

            if (!utilityHelpers.ispermissionpending(context, obj_RepeatCaller.permissions)) {
            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            if (obj_RepeatCaller.isFeatureActivated()) {

                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);


                if (number != null) {
                    repeatCaller_Ringtone = new ringtones(context, obj_RepeatCaller.getFeat_ID());
                    error = "Number not null";
                    mLastState = state;
                    Log.e(TAG, state);

                    obj_RepeatCaller.matchActiveNotificationsWithMissedList(context, notificationManager);
                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        error = "ringing";
                        obj_RepeatCaller.setStateRINGING(true);
                        if ((AudioManager.RINGER_MODE_NORMAL != am.getRingerMode())) {
                            error = "Ringer mode not normal";

                            if (obj_RepeatCaller.isNumberRepeatCaller(number)) {

                                if (!(repeatCaller_Ringtone.isRingtonePlaying())) {
                                    error = "Ringer Mode is silent";

                                    int seekbarVolume = utilityHelpers.loadIntFromMemory(context, "ARC_VOLUME", repeatCaller_Ringtone.getCurrent_MediaVolume());
                                    am.setStreamVolume(AudioManager.STREAM_MUSIC, seekbarVolume, 0);

                                    local_mp = repeatCaller_Ringtone.playLongtune(context);
                                    flow = "Play Tune Silent";
                                    obj_RepeatCaller.setRepeatCallerRingActivated(true);
                                } else {
                                    error = "Ringtone was null";
                                }
                                utilityHelpers.saveStringToMemory(context, "errorStr", flow);
                            }
                        }
                    } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        obj_RepeatCaller.setStateRINGING(false);
                        handleReset(context, obj_RepeatCaller);
                        int numberID = utilityHelpers.listLoopSearchString(number, obj_RepeatCaller.getMissedList());
                        obj_RepeatCaller.nullifyMissedElement(numberID, notificationManager);

                        flow = "Offhook";
                        utilityHelpers.saveStringToMemory(context, "errorStr", flow);
                        obj_RepeatCaller.saveMissedListToMemory(context);
                    } else if ((state.equals(TelephonyManager.EXTRA_STATE_IDLE))) {
                        error = "ID:phone state is idle";
                        handleReset(context, obj_RepeatCaller);
                        if ((obj_RepeatCaller.isStateRINGING())) {
                            if (obj_RepeatCaller.isFeatureActivated()) {
                                error = "ID:feature Activated";
                                String logType = getLastCallLog(context);
                                if (logType.equals(String.valueOf(MISSED_TYPE))) {
                                    error = "ID:missedcall detected";
                                    flow = error;
                                    utilityHelpers.saveStringToMemory(context, "errorStr", flow);
                                    if (!(obj_RepeatCaller.isNumberRepeatCaller(number))) {
                                        error = "ID:number is repeat caller";
                                        obj_RepeatCaller.addToMissedList(logName, logNumber, "Missed");
                                        NotificationManager post_notificationManager = notificationManager;
                                        error = "ID:Created Notification Channel";
                                        feat_RepeatCaller.notificationIDcounter = feat_RepeatCaller.notificationIDcounter + 1;
                                        int notificationID = feat_RepeatCaller.notificationIDcounter;
                                        String NotiString = getNotificationTitleString();


                                        Notification notification = utilityHelpers.buildNotification(NotiString, "Swipe or press Stop to mute next Call", notificationID, context);

                                        post_notificationManager.notify(notificationID, notification);
                                        Data inputData = new Data.Builder()
                                                .putInt("NOTIID",notificationID)
                                                .build();
                                        obj_RepeatCaller.tenMinuteDelete=new OneTimeWorkRequest.Builder(manageWork_AllowRepeatCallers.class)
                                                .setInitialDelay(Duration.ofMinutes(3))
                                                .setInputData(inputData)
                                                .build();
                                        WorkManager.getInstance(context).enqueue(obj_RepeatCaller.tenMinuteDelete);
                                        flow = "Posted Notification";
                                        utilityHelpers.saveStringToMemory(context, "errorStr", flow);
                                        obj_RepeatCaller.saveMissedListToMemory(context);
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
            }
            }
        } catch(Exception e){
                Log.e(TAG, " Exception on receive CALL  " + e);
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                utilityHelpers.saveStringToMemory(context,"errorStr",error);
                obj_RepeatCaller.clearAndSaveMissedList(context);
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

    private void handleReset(Context context,feat_RepeatCaller obj_RepeatCaller) {
        repeatCaller_Ringtone.setMyRingerisplaying(false);
                if(obj_RepeatCaller.isRepeatCallerRingActivated()) {
                    AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, ringtones.getCurrent_MediaVolume(), 0);
                    repeatCaller_Ringtone.stoptune(context,local_mp);
                    obj_RepeatCaller.setRepeatCallerRingActivated(false);
                }
    }



}