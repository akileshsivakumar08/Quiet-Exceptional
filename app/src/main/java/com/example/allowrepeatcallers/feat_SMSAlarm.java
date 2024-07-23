package com.example.allowrepeatcallers;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.quietexceptional.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class feat_SMSAlarm {
    private static final String CHANNEL_ID = "SMS Notification";
    public static String[] addedPermissions={Manifest.permission.MODIFY_AUDIO_SETTINGS};
    private static boolean silentExceptionRingActivated;
    private static final String MEMCODE_SMSALARM="STRINGSET_SMSALARM";
    private static final String MEMCODE_PERMISSIONREQUESTED="SMSALARM_PERMISSIONREQUESTED";
    private static final String MEMCODE_ACTIVATEFEAT="FEAT_SMSALARM_ACTIVE";
    public static final String SA_MESSAGE="Quiet Exceptional Alarm";
    private final int feat_ID=1;
    public static NotificationManager notificationManager;
    public static ringtones AlarmSound ;
    public static MediaPlayer player;
    private boolean featureActivated=false;
    public static int interruptionFilter;
    public static String FLOW="NL_SA_FLOW";



    public static String description;
    public ArrayList<class_Buddy> SMSAlarmList = new ArrayList<>();
    public static String[] permissions= {Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.READ_CONTACTS};

    public void setSMSAlarmList(Context context,ArrayList<class_Buddy> SMSAlarmList) {
        this.SMSAlarmList = SMSAlarmList;
        saveDataToMemory(context);
    }
    public int findFavourite() {
        for(int i=0;i<SMSAlarmList.size();i++){
            if(SMSAlarmList.get(i).isFavourite()){
                return i;
            }
        }
        return  255;
    }
     public boolean isSilentExceptionRingActivated() {
         return silentExceptionRingActivated;
     }

     public void setSilentExceptionRingActivated(boolean silentExceptionRingActivated) {
         feat_SMSAlarm.silentExceptionRingActivated = silentExceptionRingActivated;
     }


    public ArrayList<class_Buddy> getSilExceptList() {
        return SMSAlarmList;
    }
public class_Buddy getContact(int ID){
    return SMSAlarmList.get(ID);
}

    public int isMatchFoundInList(String sender_message, String senderName) {
        boolean MatchfoundinList=false;
        int MatchID=255;
        Log.d("myTag", "Object list obtained from Memory");

        for(int i=0;i<SMSAlarmList.size();i++) {
            Log.d("myTag", "Looping");
            if (((sender_message.trim()).equalsIgnoreCase(SMSAlarmList.get(i).getBuddy_Message()))) {
            Pattern p = Pattern.compile("\u2068(.*?)\u2069");
            Matcher matchName = p.matcher(senderName);
            if(matchName.find()) {
                senderName=matchName.group(1);
            }
            if (senderName.equals(SMSAlarmList.get(i).getBuddy_name())){
                MatchfoundinList=true;
                MatchID=i;
                Log.d("myTag", "Match found");
                break;
            }
            }
            else {
                MatchID=255;
                MatchfoundinList=false;
            }

        }

        return MatchID;
    }
    public feat_SMSAlarm(Context context) {
        featureActivated = utilityHelpers.loadBooleanFromMemory(context, MEMCODE_ACTIVATEFEAT);
        loadsilExceptListFromMemory(context);
        description=context.getString(R.string.SA_description);
        createNotificationChannel(context);
        AlarmSound= new ringtones(context,1);
     }

    public boolean isFeatureActivated(Context context) {
        featureActivated= utilityHelpers.loadBooleanFromMemory(context, MEMCODE_ACTIVATEFEAT);
        return featureActivated;
    }

    public void setFeatureActivated(Context context,boolean featureActivated) {
        this.featureActivated = featureActivated;
        utilityHelpers.saveBooleanToMemory(context,MEMCODE_ACTIVATEFEAT,featureActivated);
    }

    public static String getDescription() {
        return description;
    }



    public void loadsilExceptListFromMemory(Context context) {
         Gson gson = new Gson();
         String String_ExceptList = utilityHelpers.loadStringFromMemory(context,MEMCODE_SMSALARM);
        if(!String_ExceptList.equals("null")) {
             SMSAlarmList = gson.fromJson(String_ExceptList, new TypeToken<List<class_Buddy>>() {
             }.getType());
         }
     }

     public Boolean isnumberinList( String senderNum) {
        boolean MatchfoundinList=false;

        int numberID = utilityHelpers.listLoopSearchObj(senderNum, SMSAlarmList);
        if(numberID==255){
            MatchfoundinList=false;
        }
        else{
            MatchfoundinList=true;
        }
        return MatchfoundinList;
    }

    public void saveDataToMemory(Context context) {
        Gson gson = new Gson();
        String Json_ExceptList = gson.toJson(SMSAlarmList);
        utilityHelpers.saveStringToMemory(context,MEMCODE_SMSALARM,Json_ExceptList);
    }
    public void createNotificationChannel(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "SMS Alarm Notification",
                NotificationManager.IMPORTANCE_LOW
        );
        notificationManager.createNotificationChannel(channel);
    }
    public Notification buildSMSNotification(String title, Context context) {
        Intent stopIntent = new Intent(context, StopAlarmReceiver.class);
        stopIntent.setAction("STOP_SERVICE");
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setDeleteIntent(stopPendingIntent)
                .addAction(R.drawable.letter_q, "Stop", stopPendingIntent)
                .setContentText(context.getString(R.string.emergency_message_swipe_to_stop_playing_tune))
                .setSmallIcon(R.drawable.letter_q)
                .build();
    }

    public void sendSMS(Context context,int smstype,ArrayList<class_Buddy> contacts) {
        String phoneNo = "";
        String SMS;
        if(smstype==1) {
            SMS = context.getString(R.string.Quiet_Exceptional_Alarm);
        }
        else{
            SMS = context.getString(R.string.share_message_addedContact);
        }
        try {
            loadsilExceptListFromMemory(context);
            if (!(contacts.isEmpty())) {
                for (int i = 0; i < contacts.size(); i++) {
                    phoneNo = contacts.get(i).getBuddy_PhNo();
                    String name = contacts.get(i).getBuddy_name();
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, SMS, null, null);
                    Toast.makeText(context, "sending message to " + name, Toast.LENGTH_SHORT).show();

                }
            }
            else{
                Toast.makeText(context, R.string.no_contacts_found, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to send Message", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<class_Buddy> getLastnContacts(int differenceContactLength) {
        ArrayList<class_Buddy> ContactstoSendSMS=new ArrayList<class_Buddy>();
        int totalSize=SMSAlarmList.size();
        for(int i=0;i<differenceContactLength;i++){
            ContactstoSendSMS.add(SMSAlarmList.get(totalSize-1));
            totalSize=totalSize-1;
        }
        return ContactstoSendSMS;
    }

    public Boolean checkMessageMatch(String sender_message) {
        if (((sender_message.trim()).equalsIgnoreCase(SA_MESSAGE))) {
            return true;
        }
        else{
            return false;
        }
    }
    public int checkSenderMatch(CharSequence senderName) {
        boolean MatchfoundinList=false;
        int MatchID=255;
        for(int i=0;i<SMSAlarmList.size();i++) {
            Log.d("myTag", "Looping");
                Pattern p = Pattern.compile("\u2068(.*?)\u2069");
                Matcher matchName = p.matcher(senderName);
                if(matchName.find()) {
                    senderName=matchName.group(1);
                }
                if (senderName.equals(SMSAlarmList.get(i).getBuddy_name())){
                    MatchID=i;
                    Log.d("myTag", "Match found");
                    break;
                }
        }
        return MatchID;
    }
}

