package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;
import static java.lang.Thread.sleep;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

public class notificationlistener extends NotificationListenerService {
    public static NotificationManager notificationManager;
    private static final String TAG = "QuietExceptional";
    String title = "";
    String text;
    String subtext;
    String bigtext;
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        String ErrorFlow = "notificationListener:Start";
        Context context = getApplicationContext();
        if ((sbn.getNotification().flags & Notification.FLAG_GROUP_SUMMARY) != 0) {

        } else if (extras.containsKey("android.title") && (extras.containsKey("android.text"))) {
            ErrorFlow = "notificationListener:Extras contain title and Text";
            if ((extras.getCharSequence("android.title") != null) && (extras.getCharSequence("android.text") != null)) {
                try {
                    title = extras.getCharSequence("android.title").toString();
                    text = extras.getCharSequence("android.text").toString();
                    subtext=extras.getCharSequence("android.subtext").toString();
                    bigtext=extras.getCharSequence("android.bigtext").toString();
                } catch (Exception e) {
                    ErrorFlow = "notificationListener:Error Obtaining Notification title or Text";
                    Log.e(TAG, ErrorFlow);
                    utilityHelpers.saveErrorToMemory(context, "ERROR"+ErrorFlow);
                    throw new RuntimeException(e);
                }
                if (checkpossibleMissedCall(context)) {
                    if (callStateManager.notificationWindow) {
                        ErrorFlow = "notificationListener:Inside Notification Window";

                        try {
                            if (!utilityHelpers.ispermissionpending(context, feat_AlertMissedCalls.permissions)) {
                                ErrorFlow = "NR_AMC_Permissions Provided";
                                feat_AlertMissedCalls obj_AMC = new feat_AlertMissedCalls(context);
                                if (obj_AMC.isFeatureActivated(context)) {
                                    ErrorFlow = "Feature Activated";
                                    ErrorFlow = "NR_AMC_Missed Call Detected";
                                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                ErrorFlow = "NR_AMC_Init Complete";
                                boolean ringDevice = utilityHelpers.isDNDOverriden(context);
                                if (ringDevice) {
                                    ErrorFlow = "NR_AMC_Ring Device Activated";
                                    if ((am.getMode() != AudioManager.MODE_IN_COMMUNICATION) && (am.getMode() != AudioManager.MODE_IN_CALL)) {
                                        ErrorFlow = "NR_AMC_No Active calls";

                                            utilityHelpers.turnSpeakerON(am);
                                            int setVolume = obj_AMC.getPingVolume(context);
                                            am.setStreamVolume(AudioManager.STREAM_MUSIC, setVolume, 0);
                                            ringtones shorttune = new ringtones(context, 2);
                                            if (utilityHelpers.isDeviceConnected(am)) {
                                                ErrorFlow = "NR_AMC_Playing on connected Device";
                                                am.setStreamVolume(AudioManager.STREAM_MUSIC, (am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) / 2, 0);
                                            }

                                            shorttune.playShorttune(context);
                                        ErrorFlow = "NR_AMC_PlayingTune";
                                            //am.setStreamVolume(AudioManager.STREAM_MUSIC,Current_MediaVolume,0);
                                        }
                                    else{
                                        ErrorFlow = "NR_AMC_Not playing due to ongoing call";
                                    }
                                    }
                                else{
                                    ErrorFlow = "NR_AMC_Ring Device Not Active";
                                }
                                    //  Toast.makeText(context, "This a toast message", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    ErrorFlow = "NR_AMC_Feature Not Active";
                                }
                            }
                            else{
                                ErrorFlow = "NR_AMC_Permission Pending";
                            }
                            utilityHelpers.saveStringToMemory(context, "NL_AMC_FLOW", ErrorFlow);
                        } catch (Exception e) {
                            Log.e(TAG, ErrorFlow);
                            utilityHelpers.saveErrorToMemory(context, "ERROR"+ErrorFlow);
                            throw new RuntimeException(e);
                        }

                    }
                } else{




                    /*SMS ALARM Section*/

                    String objBuddyName = "";
                String objBuddyNumber = "";
                String sender_message = text;
                String senderName = title;
                int MatchfoundinListID = 255;
                try {
                    feat_SMSAlarm obj_SMSAlarm = new feat_SMSAlarm(context);
                    ErrorFlow = "NR_SA_object created";
                    if (!utilityHelpers.ispermissionpending(context, feat_SMSAlarm.permissions)) {
                        ErrorFlow = "NR_SA_NoPermissionPending";
                        feat_SMSAlarm smsReceiver = new feat_SMSAlarm(context);
                        if (smsReceiver.isFeatureActivated(context)) {
                            ErrorFlow = "NR_SA_FeatureActivated";
                            if (!obj_SMSAlarm.isSilentExceptionRingActivated()) {
                                ErrorFlow = "NR_SA_NoRingActivated";
                                Boolean messageMatch = smsReceiver.checkMessageMatch(sender_message);
                                if (messageMatch) {

                                MatchfoundinListID=smsReceiver.checkSenderMatch(senderName);

                               // MatchfoundinListID = smsReceiver.isMatchFoundInList(sender_message, senderName);

                                if (MatchfoundinListID != 255) {
                                    ErrorFlow = "NR_SA_MatchFoundInList";
                                    objBuddyName = smsReceiver.SMSAlarmList.get(MatchfoundinListID).getBuddy_name();
                                    objBuddyNumber = smsReceiver.SMSAlarmList.get(MatchfoundinListID).getBuddy_PhNo();
                                    AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                                    int Volume = (am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                                    NotificationManager Notimanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                    feat_SMSAlarm.interruptionFilter = Notimanager.getCurrentInterruptionFilter();
                                    if (feat_SMSAlarm.interruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL) {
                                        ErrorFlow = "NR_SA_DND_IS_ON";
                                        Notimanager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                                    }
                                    int Current_MediaVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    utilityHelpers.turnSpeakerON(am);
                                    am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);


                                    feat_SMSAlarm.player = obj_SMSAlarm.AlarmSound.playLongtune(context);
                                    ErrorFlow = "NR_SA_LONGTUNESTARTED";
                                    obj_SMSAlarm.setSilentExceptionRingActivated(true);
                                    Notification notification = obj_SMSAlarm.buildSMSNotification(objBuddyName, context);

                                    feat_SMSAlarm.notificationManager.notify(1, notification);
                                    ErrorFlow = "NR_SA_POSTEDNOTIFICATION";
                                    //am.setStreamVolume(AudioManager.STREAM_MUSIC,Current_MediaVolume,0);
                                    //play tune
                                    //if media playback was interrupted by a phone call, decide if tone should be played during the call.
                                    //if incoming call during media playback is from number that sent the message, should call be allowed?
                                }
                                else{
                                    ErrorFlow = "NR_SA_SenderMatchFailed"+senderName;
                                }
                            }
                                else{

                                    ErrorFlow = "NR_SA_MessageMatchFailed\n"+ sender_message+"\n"+subtext+"\n"+bigtext;
                                }

                            }
                            else{
                                ErrorFlow = "NR_SA_SilentExceptionAlreadyActive";
                            }
                        }
                        else{
                            ErrorFlow = "NR_SA_FeatureNotActivated";
                        }
                    }
                    else{
                        ErrorFlow = "NR_SA_PermissionPending";
                    }
                    Log.d(TAG,sender_message+"\n"+subtext+"\n"+bigtext);
                    utilityHelpers.saveStringToMemory(context, "NL_SA_FLOW", ErrorFlow);
                } catch (Exception e) {
                    Log.e(TAG, ErrorFlow);
                    utilityHelpers.saveErrorToMemory(context, "ERROR"+ErrorFlow);
                    throw new RuntimeException(e);
                }
            }

        }
    }
    }



    private boolean checkpossibleMissedCall(Context context) {

            String missedCallText=LanguageManager.getMissedCallText(context);
            String customText=LanguageManager.getCustomText(context);
            if ((title.contains(missedCallText))
                    || (text.contains(missedCallText))){

                return true;
            } else if ((title.contains(customText))
                    || (text.contains(customText))) {
                return true;
            } else {
                return false;
            }
        }
}
