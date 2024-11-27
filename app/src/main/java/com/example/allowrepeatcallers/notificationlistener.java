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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class notificationlistener extends NotificationListenerService {
    public static NotificationManager notificationManager;
    private static final String TAG = "QuietExceptional";
    String title = "";
    String text;
    String subtext;
    String bigtext;
    String todaydate;
    String currentTime;
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        String notiKey=sbn.getKey();
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
                    /*subtext=extras.getCharSequence("android.subtext").toString();
                    bigtext=extras.getCharSequence("android.bigtext").toString();*/
                } catch (Exception e) {
                    ErrorFlow = todaydate+"-"+currentTime+"-"+"notificationListener:Error Obtaining Notification title or Text";
                    Log.e(TAG, ErrorFlow);
                    utilityHelpers.saveErrorToMemory(context, "ERROR"+ErrorFlow);
                    throw new RuntimeException(e);
                }
                if (checkpossibleMissedCall(context)) {
                    if (callStateManager.notificationWindow) {
                        ErrorFlow = "notificationListener:Inside Notification Window";

                            if (!utilityHelpers.ispermissionpending(context, feat_AlertMissedCalls.permissions)) {
                                ErrorFlow = "NR_AMC_Permissions Provided";
                                feat_AlertMissedCalls obj_AMC = new feat_AlertMissedCalls(context);
                                obj_AMC.pingMissedCall(context,notiKey,1);
                            }
                            else{
                                ErrorFlow = "NR_AMC_Permission Pending";
                            }
                            utilityHelpers.saveFlowToMemory(context,ErrorFlow,feat_AlertMissedCalls.FLOW);

                    }
                } else if(feat_SMSAlarm.checkMessageMatch(text)){




                    /*SMS ALARM Section*/

                    String objBuddyName = "";
                String objBuddyNumber = "";
                String sender_message = text;
                CharSequence senderName = title;
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
                    Log.d(TAG,sender_message);
                    utilityHelpers.saveFlowToMemory(context,ErrorFlow,feat_SMSAlarm.FLOW);
                } catch (Exception e) {
                    Log.e(TAG, ErrorFlow);
                    ErrorFlow = todaydate+"-"+currentTime+"-"+ErrorFlow;
                    utilityHelpers.saveErrorToMemory(context, "ERROR "+ErrorFlow);
                    throw new RuntimeException(e);
                }
            }
            else{
                feat_AnyTextMatch obj_AnyMatch=new feat_AnyTextMatch(context);
                obj_AnyMatch.pingifCustomTextMatch(context,text,notiKey,1);


            }

        }
            else{
                ErrorFlow = "Title or Text is null";
                utilityHelpers.saveStringToMemory(context, "NL_General_FLOW", ErrorFlow);
            }
    }
        else{
            ErrorFlow = "Title or Text Key is not found";
            utilityHelpers.saveStringToMemory(context, "NL_General_FLOW", ErrorFlow);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Context context=getApplicationContext();
        feat_AnyTextMatch obj_ATM=new feat_AnyTextMatch(context);
        feat_AlertMissedCalls obj_AMC = new feat_AlertMissedCalls(context);
        String notiKey=sbn.getKey();
        obj_AMC.removeMissedCall(context,notiKey);
        obj_ATM.remove_ATM_Key(context,notiKey);


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
