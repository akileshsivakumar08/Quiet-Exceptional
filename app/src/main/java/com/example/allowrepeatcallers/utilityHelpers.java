package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;
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

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class utilityHelpers {
    public static final String SHARED_PREFS = "sharedPrefs";

    public static final String CHANNEL_ID = "Missed Call Notification";
    public static String sortOrder;
    private static final String MEMCODE_SORTORDER="UPTODOWN";


    public static void adjustTitleTextSize(TextView text,Context context){
        Configuration config = context.getResources().getConfiguration();
        if(config.getLocales().get(0).getLanguage().contains("en")){
            text.setTextSize(75);
        }
        else{
            text.setTextSize(50);
        }
    }


    public static void checkLogFormat(Context context) {
        Uri uriCallLogs = Uri.parse("content://call_log/calls");
        Cursor cursorCallLogs = null;
        cursorCallLogs = context.getContentResolver().query(uriCallLogs, null, null, null);
        cursorCallLogs.moveToLast();
        int columnIndex=cursorCallLogs.getColumnIndex(CallLog.Calls.DATE);
        if(columnIndex>=0) {
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
    public static int listLoopSearchObj(String number, List<class_Buddy> myList) {
        Boolean MatchfoundinList = false;
        int i=0;
        if (!(myList.isEmpty())) {
            Log.d("myTag", "Object list obtained from Memory");
            callReceiver_RepeatCaller.error="ID:list is not empty";
            for (i = 0; i < myList.size(); i++) {
                Log.d("myTag", "Looping");

               // if ((PhoneNumberUtils.compare(number, myList.get(i).getBuddy_PhNo()))) {
                if ((PhoneNumberUtils.compare(number, myList.get(i).getBuddy_PhNo()))) {
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
    public static int listLoopSearchString(String number, List<String> myList) {
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

    public static void turnSpeakerON(AudioManager am) {
        if (isDeviceConnected(am)) {
            am.setSpeakerphoneOn(true);
        }
    }
    public static boolean isDeviceConnected(AudioManager am){
        boolean isDeviceConnected=false;
        AudioDeviceInfo[] audioDevices = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        for (AudioDeviceInfo device : audioDevices) {
            if (device.getType() == AudioDeviceInfo.TYPE_AUX_LINE || device.getType() == AudioDeviceInfo.TYPE_BLE_HEADSET) {
                isDeviceConnected=true;
                break;
            }
        }
        return isDeviceConnected;
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


    public static String getSortOrder(Context context) {
        String sortOrder=utilityHelpers.loadStringFromMemory(context,MEMCODE_SORTORDER);
        return sortOrder;
    }

    public static boolean isDNDOverriden(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        int dndstatus = notificationManager.getCurrentInterruptionFilter();
        boolean ring = false;

        boolean overridednd_bool = loadBooleanFromMemory(context, "OVERRIDE_DND");

         /*
        Override    ON  ON  OFF OFF
        DND State   ON  OFF ON  OFF
        Result      T   T   F   T
        True: Ring Device
        False: Dont Ring device
        * */
        if (overridednd_bool) {
            ring = true;
        } else if ((!overridednd_bool)) {
            if (dndstatus == NotificationManager.INTERRUPTION_FILTER_ALL) {
                ring = true;
            } else {
                ring = false;
            }
        }
        return ring;
    }


    public static HashMap<String, Boolean> convertStringToHashMap(String loadedmap) {
        Gson gson = new Gson();
        HashMap<String,Boolean> grantedPermissions= new HashMap<String, Boolean>();
        if(!loadedmap.equals("null")) {
            grantedPermissions = gson.fromJson(loadedmap, new TypeToken<HashMap<String, Boolean>>() {
            }.getType());
        }
        return grantedPermissions;
    }
    public static String convertHashMapToString(Context context,HashMap<String,Boolean> requestedPermissions){
        Gson gson = new Gson();
        String String_requestedPermissions = gson.toJson(requestedPermissions);
        return String_requestedPermissions;
    }
}
