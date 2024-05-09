package com.example.allowrepeatcallers;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;

public class Receiver_SMSAlarm extends BroadcastReceiver {
    public static final String SHARED_PREFS = "sharedPrefs";
    private static final String KEY_ARRAY_LIST3 = "arrayListData3";
    public static ArrayList<class_Buddy> obj_BuddyLocal = new ArrayList<>();
    private NotificationManagerCompat notificationManager;
    private int MatchID;

    @Override
    public void onReceive(Context context, Intent intent) {

        String objBuddyName = "";
        String objBuddyNumber = "";
        String sender_message = "";
        String senderNum = "";
        int MatchfoundinListID = 255;
        feat_SMSAlarm obj_SMSAlarm=new feat_SMSAlarm(context);
        if (!utilityHelpers.ispermissionpending(context, feat_SMSAlarm.permissions)) {
            if (intent.getAction().equalsIgnoreCase("android.provider.Telephony.SMS_RECEIVED")) {
                SmsMessage messages = getSMSContent(intent, senderNum, sender_message);
                if (messages != null) {
                    senderNum = messages.getOriginatingAddress();

                    sender_message = messages.getMessageBody();
                feat_SMSAlarm smsReceiver = new feat_SMSAlarm(context);
                if (smsReceiver.isFeatureActivated(context)) {
                    if (!obj_SMSAlarm.isSilentExceptionRingActivated()) {
                        MatchfoundinListID = smsReceiver.isMatchFoundInList(sender_message, senderNum);

                        if (MatchfoundinListID != 255) {
                            objBuddyName = smsReceiver.SMSAlarmList.get(MatchID).getBuddy_name();
                            objBuddyNumber = smsReceiver.SMSAlarmList.get(MatchID).getBuddy_PhNo();
                            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                            int Volume = (am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                            NotificationManager Notimanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            feat_SMSAlarm.interruptionFilter = Notimanager.getCurrentInterruptionFilter();
                            if (feat_SMSAlarm.interruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL) {
                                Notimanager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                            }
                            int Current_MediaVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                            utilityHelpers.turnSpeakerON(am);
                            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);


                            feat_SMSAlarm.player = obj_SMSAlarm.AlarmSound.playLongtune(context);
                            obj_SMSAlarm.setSilentExceptionRingActivated(true);
                            Notification notification = obj_SMSAlarm.buildSMSNotification(objBuddyName, context);

                            feat_SMSAlarm.notificationManager.notify(1, notification);
                            //am.setStreamVolume(AudioManager.STREAM_MUSIC,Current_MediaVolume,0);
                            //play tune
                            //if media playback was interrupted by a phone call, decide if tone should be played during the call.
                            //if incoming call during media playback is from number that sent the message, should call be allowed?
                        }
                    }
                }
            }
        }
    }
    }



    private SmsMessage getSMSContent(Intent intent,String senderNum, String sender_message) {
        Bundle b=intent.getExtras();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (b != null) {
                final Object[] pdusObj = (Object[]) b.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdusObj.length];
                for (int i = 0; i < messages.length; i++) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        String format = b.getString("format");
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
                    } else {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    }

                    return messages[i];
                }

            }
        }
        return null;
    }

}
