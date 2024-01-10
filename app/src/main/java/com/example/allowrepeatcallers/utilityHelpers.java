package com.example.allowrepeatcallers;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class utilityHelpers {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static MediaPlayer mp;
    public static final String CHANNEL_ID = "Missed Call Notification";
    public static String sortOrder;

    public static void adjustTitleTextSize(TextView text,Context context){
        Configuration config = context.getResources().getConfiguration();
        if(config.getLocales().get(0).getLanguage().contains("en")){
            text.setTextSize(75);
        }
        else{
            text.setTextSize(60);
        }
    }


    public static void checkLogFormat(Context context) {
        Uri uriCallLogs = Uri.parse("content://call_log/calls");
        Cursor cursorCallLogs = null;
        cursorCallLogs = context.getContentResolver().query(uriCallLogs, null, null, null);
        cursorCallLogs.moveToLast();
        int columnIndex=cursorCallLogs.getColumnIndex(CallLog.Calls.DATE);
        if(columnIndex>0) {
            String logDate = cursorCallLogs.getString(columnIndex);
            long lastTime = Long.parseLong(logDate);
            cursorCallLogs.moveToFirst();
            logDate = cursorCallLogs.getString(columnIndex);
            long firstTime = Long.parseLong(logDate);
            if (firstTime > lastTime) {
                sortOrder = "firstTime";
                utilityHelpers.saveStringToMemory(context, "UPTODOWN", sortOrder);
            } else {
                sortOrder = "lastTime";
                utilityHelpers.saveStringToMemory(context, "UPTODOWN", sortOrder);
            }
        }
    }

    public static boolean ispermissionpending(Context context,String[] permissions){
        boolean ispending=true;
        for(int i=0;i< permissions.length;i++){
            if((ActivityCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED)){
                ispending=true;
                break;
            }
            else{
                ispending=false;
            }
        }
        return ispending;
    }

    public static String[] getpendingpermissions(Context context,String[] permissions){
        ArrayList<String> pendingpermissions=new ArrayList<>();
        //String[] pendingpermissions_array=new String[permissions.length];
        int j=0;
        for(int i=0;i< permissions.length;i++){

            if((ActivityCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED)){
                pendingpermissions.add(permissions[i]);
            }
        }
        String[] pendingpermissions_array=pendingpermissions.toArray(new String[pendingpermissions.size()]);
        return pendingpermissions_array;
    }


    //public static int listLoopSearch(String number, List<class_Buddy> myList) {
    public static int listLoopSearch(String number, List<String> myList) {
        Boolean MatchfoundinList = false;
        int i=0;
        if (!(myList.isEmpty())) {
            Log.d("myTag", "Object list obtained from Memory");
            callReceiver_RepeatCaller.error="ID:list is not empty";
            for (i = 0; i < myList.size(); i++) {
                Log.d("myTag", "Looping");

               // if ((PhoneNumberUtils.compare(number, myList.get(i).getBuddy_PhNo()))) {
                if ((PhoneNumberUtils.compare(number, myList.get(i)))) {
                    MatchfoundinList = true;
                    callReceiver_RepeatCaller.error="ID:Match is found";
                    Log.d("myTag", "Match found");
                    break;
                }
            }

        }
        if(MatchfoundinList==false){
            callReceiver_RepeatCaller.error="ID:No Match found or list is empty";
            i=255;
        }
        return i;
    }

    public static void createNotificationChannel(Context context) {
        callReceiver_RepeatCaller.notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Missed Call Notification",
                NotificationManager.IMPORTANCE_LOW
        );
        callReceiver_RepeatCaller.notificationManager.createNotificationChannel(channel);
    }
    public static void saveBooleanToMemory(Context context,String DataID,boolean booldata){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DataID,booldata);
        editor.commit();

    }
    public static void saveIntToMemory(Context context,String DataID,int intdata){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(DataID,intdata);
        editor.commit();

    }
    public static void saveStringToMemory(Context context,String DataID,String stringdata){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DataID,stringdata);
        editor.commit();

    }
    public static void saveStringSetToMemory(Context context,String DataID,ArrayList<String> stringdata){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> stringSet = new HashSet<>(stringdata);
        editor.putStringSet(DataID,stringSet);
        editor.commit();

    }
    public static boolean loadBooleanFromMemory(Context context,String DataID){
        boolean variable;
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        variable=sharedPreferences.getBoolean(DataID,false);
        return variable;
    }
    public static int loadIntFromMemory(Context context,String DataID,int init){
        int variable;
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        variable=sharedPreferences.getInt(DataID,init);
        return variable;
    }
    public static String loadStringFromMemory(Context context,String DataID){
        String variable;
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        variable=sharedPreferences.getString(DataID,"null");
        return variable;
    }
    public static ArrayList<String>  loadStringSetFromMemory(Context context,String DataID){
        Set<String> stringSet;
        ArrayList<String> stringList=new ArrayList<String>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        stringSet=sharedPreferences.getStringSet(DataID,null);
        if(stringSet!=null) {
            stringList = new ArrayList<>(stringSet);
        }
        return stringList;
    }
    public static MediaPlayer playtune(Context context) {
        Toast.makeText(context,"Playing Ringtext",Toast.LENGTH_LONG).show();
        mp = MediaPlayer.create(context, ringtones.ringtoneUri);
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(!mp.isPlaying()){
            utilityHelpers.turnSpeakerON(am);
            mp.start();
        }
        return mp;
    }
    public static void stoptune(Context context,MediaPlayer mp){
        mp.stop();
        mp.reset();
    }
    public static void turnSpeakerON(AudioManager am) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioDeviceInfo[] audioDevices = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo device : audioDevices) {
                if (device.getType() == AudioDeviceInfo.TYPE_AUX_LINE || device.getType() == AudioDeviceInfo.TYPE_BLE_HEADSET) {
                    am.setSpeakerphoneOn(true);
                }
            }
        }
    }
    public static Notification buildNotification(String title, String content, int notificationID, Context context) {
        callReceiver_RepeatCaller.error = "ID:building notification";
        String CHANNEL_ID = "Missed Call Notification";
        Intent stopIntent = new Intent(context.getApplicationContext(), StopCountdownReceiver.class);
        stopIntent.setAction("STOP_SERVICE");
        stopIntent.putExtra("TIMERID", notificationID);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), notificationID, stopIntent, PendingIntent.FLAG_IMMUTABLE);
        if (title.equals("Test")) {
            return new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.baseline_account_circle_24)
                    .build();
        } else {
            return new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setDeleteIntent(stopPendingIntent)
                    .setSmallIcon(R.drawable.baseline_account_circle_24)
                    .addAction(R.drawable.baseline_account_circle_24, "Stop", stopPendingIntent)
                    .build();
        }
    }

}
