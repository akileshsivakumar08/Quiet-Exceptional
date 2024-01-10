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
    public static int notificationIDcounter;
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
                                    Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                    ringtones.ringtone = RingtoneManager.getRingtone(context, ringtoneUri);

                                        if ((!ringtones.ringtone.isPlaying())) {
                                                error = "Ringer Mode is silent";
                                                ringtones.Current_MediaVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                                                int seekbarVolume=utilityHelpers.loadIntFromMemory(context,"ARC_VOLUME",ringtones.Current_MediaVolume);
                                                utilityHelpers.turnSpeakerON(am);
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
                            //NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                            feat_RepeatCaller.stateRINGING = false;
                            handleReset(context);
                            int numberID = utilityHelpers.listLoopSearch(number, feat_RepeatCaller.missedList);
                            int notiid = numberID + 1;
                            Toast.makeText(context, "offhook" + notiid, Toast.LENGTH_SHORT).show();
                            if (255 != numberID) {
                            /*class_Buddy nullBuddy=new class_Buddy(null,null,null);
                            feat_RepeatCaller.missedList.set(numberID,nullBuddy);*/
                                feat_RepeatCaller.missedList.set(numberID, "null");
                                feat_RepeatCaller.nullCount = feat_RepeatCaller.nullCount + 1;
                                if (feat_RepeatCaller.nullCount == feat_RepeatCaller.missedList.size()) {
                                    feat_RepeatCaller.missedList.clear();
                                    feat_RepeatCaller.nullCount = 0;
                                }

                                notificationManager.cancel(notiid);
                                callReceiver_RepeatCaller.notificationIDcounter--;

                            }
                            //Toast.makeText(context, "offhook"+feat_RepeatCaller.missedList.size(), Toast.LENGTH_SHORT).show();
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
                                    // Toast.makeText(context, "Obtained last call log", Toast.LENGTH_SHORT).show();

                                    if (logType.equals("3")) {
                                        Toast.makeText(context, "MissedList Size is" + (feat_RepeatCaller.missedList.size()), Toast.LENGTH_SHORT).show();
                                        error = "ID:missedcall detected";
                                        flow = error;
                                        utilityHelpers.saveStringToMemory(context, "errorStr", flow);
                                        if (!(feat_RepeatCaller.isNumberRepeatCaller(number))) {
                                            error = "ID:number is repeat caller";
                                            feat_RepeatCaller.addToMissedList(logName, logNumber, "Missed");
                                            //NotificationManager post_notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                                            NotificationManager post_notificationManager = notificationManager;
                                            error = "ID:Created Notification Channel";
                                            int notificationID = ++notificationIDcounter;
                                            String NotiString;
                                            if (logName == null) {
                                                NotiString = logNumber;
                                            } else {
                                                NotiString = logName;
                                            }

                                            Notification notification = utilityHelpers.buildNotification(NotiString, "Swipe or press Stop to mute next Call", notificationID, context);
                                       /* NotificationManager manager = null;
                                        manager = context.getSystemService(NotificationManager.class);*/
                                            post_notificationManager.notify(notificationID, notification);
                                            Toast.makeText(context, "Added " + logNumber + " to missedlist", Toast.LENGTH_SHORT).show();
                                            flow = "Posted Notification";
                                            utilityHelpers.saveStringToMemory(context, "errorStr", flow);
                                            utilityHelpers.saveStringSetToMemory(context, "STRINGSET_MISSEDLIST", feat_RepeatCaller.missedList);
                                        } else {
                                            //Toast.makeText(context, "Matchfound in list", Toast.LENGTH_SHORT).show();
                                            flow = "Repeated Missed Call";
                                            utilityHelpers.saveStringToMemory(context, "errorStr", flow);
                                        }
                                    }
                                }
                            }
                        }
               // }

                }
            }
            } catch(Exception e){
                Log.e(TAG, " Exception on receive CALL  " + e);
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                utilityHelpers.saveStringToMemory(context,"errorStr",error);
                feat_RepeatCaller.missedList.clear();
            }
    }

    private void matchActiveNotificationsWithMissedList(Context context) {
        StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();

        int numberOfActiveNotifications = activeNotifications.length;
        if ((numberOfActiveNotifications == 0) && (feat_RepeatCaller.missedList.size() > 0)) {
            feat_RepeatCaller.missedList.clear();
            utilityHelpers.saveStringSetToMemory(context, "STRINGSET_MISSEDLIST", feat_RepeatCaller.missedList);
        }
    }


  /*  private void addToMissedList(String p_logName,String p_logNumber,String text) {
        //create buddy with phone number and name
        //Add to missed list
        class_Buddy missedBuddy=new class_Buddy(p_logName,p_logNumber,text);
        missedList.add(missedBuddy);
    }*/

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
        logNumber = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.NUMBER));
        logName = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.CACHED_NAME));
        //Toast.makeText(context,logNumber+ stringType +" and "+ MISSED_TYPE, Toast.LENGTH_SHORT).show();
        return stringType;
    }

    /*private Notification buildNotification(String title, String content, int notificationID,Context context) {
        callReceiver_RepeatCaller.error="ID:building notification";

        Intent stopIntent = new Intent(context.getApplicationContext(), StopCountdownReceiver.class);
        stopIntent.setAction("STOP_SERVICE");
        stopIntent.putExtra("TIMERID",notificationID);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), notificationID, stopIntent, PendingIntent.FLAG_IMMUTABLE);
        return new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setDeleteIntent(stopPendingIntent)
                .setSmallIcon(R.drawable.baseline_account_circle_24)
                .addAction(R.drawable.baseline_account_circle_24,"Stop",stopPendingIntent)
                .build();
    }*/
    private void handleReset(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(!(feat_RepeatCaller.repeatCallerRingActivated.equals("NULL"))){
                ringtones.myRingerisplaying=false;
                if((feat_RepeatCaller.repeatCallerRingActivated.equals("VIBRATE"))){
                    ringtones.stopPlayingRingtone();
                    resetRingerSettings(context);
                }
                else if((feat_RepeatCaller.repeatCallerRingActivated.equals("SILENT"))){
                    AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, ringtones.Current_MediaVolume, 0);
                    utilityHelpers.stoptune(context,mp);
                }

                feat_RepeatCaller.repeatCallerRingActivated="NULL";
            }
        }
    }

    private void resetRingerSettings(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(feat_RepeatCaller.ringerMode);
        am.setStreamVolume(AudioManager.STREAM_RING,feat_RepeatCaller.Current_RingVolume,0);
        if(feat_RepeatCaller.ringerMode==AudioManager.RINGER_MODE_SILENT){
            NotificationManager Notimanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            Notimanager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            am.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
    }



    private void getCurrentSettings(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        feat_RepeatCaller.ringerMode=am.getRingerMode();
        feat_RepeatCaller.Current_RingVolume = am.getStreamVolume(AudioManager.STREAM_RING);
    }




}