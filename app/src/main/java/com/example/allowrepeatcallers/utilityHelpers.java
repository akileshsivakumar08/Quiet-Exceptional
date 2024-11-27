package com.example.allowrepeatcallers;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

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
import android.net.Uri;
import android.provider.CallLog;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.quietexceptional.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class utilityHelpers {
    public static final String SHARED_PREFS = "sharedPrefs";

    public static final String CHANNEL_ID = "Missed Call Notification";
    private static final String TAG = "QuietExceptional";
    private static String sortOrder;
    private static final String MEMCODE_SORTORDER="UPTODOWN";


    public static void adjustTitleTextSize(TextView text,Context context,int size){
      /*  Configuration config = context.getResources().getConfiguration();
        if(config.getLocales().get(0).getLanguage().contains("en")){
            text.setTextSize(size);
        }
        else{
            text.setTextSize(size/2);
        }*/
        text.setTextSize(50);

    }


    public static void checkLogFormat(Context context) {
        Uri uriCallLogs = Uri.parse("content://call_log/calls");
        Cursor cursorCallLogs = null;
        String columnorder=(CallLog.Calls.DEFAULT_SORT_ORDER);
        if (columnorder.contains("date DESC")){
            sortOrder = "date DESC";
            utilityHelpers.saveStringToMemory(context, "UPTODOWN", sortOrder);
            Log.i(TAG,"Sort order date DESC");
        }
        else if(columnorder.contains("date ASC")){
            sortOrder = "date ASC";
            utilityHelpers.saveStringToMemory(context, "UPTODOWN", sortOrder);
            Log.i(TAG,"Sort order date ASC");
        }
        else {
            try {
                cursorCallLogs = context.getContentResolver().query(uriCallLogs, null, null, null);
                cursorCallLogs.moveToLast();
                int columnIndex = cursorCallLogs.getColumnIndex(CallLog.Calls.DATE);
                if (columnIndex >= 0) {
                    String logDate = cursorCallLogs.getString(columnIndex);
                    long lastTime = Long.parseLong(logDate);
                    cursorCallLogs.moveToFirst();
                    logDate = cursorCallLogs.getString(columnIndex);
                    long firstTime = Long.parseLong(logDate);
                    if (firstTime > lastTime) {
                        sortOrder = "date ASC";
                        Log.i(TAG,"Sort order date ASC with timestamp");
                        utilityHelpers.saveStringToMemory(context, "UPTODOWN", sortOrder);
                    } else {
                        sortOrder = "date DESC";
                        Log.i(TAG,"Sort order date DSC with timestamp");
                        utilityHelpers.saveStringToMemory(context, "UPTODOWN", sortOrder);
                    }
                }
            }
            catch (Exception e) {
                //Error checking Log format with Time stamp
                Log.e(TAG, "Error checking Log format with Time stamp");
                throw new RuntimeException(e);
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

    public static String[] getpendingpermissions_Mandatory(Context context,String[] permissions){
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

    public static void saveFlowToMemory(Context context,String flowString, String FlowCode) {
        String DateAndTime=utilityHelpers.getDateAndTime();
        flowString=DateAndTime+"-"+flowString;
        utilityHelpers.saveStringToMemory(context, FlowCode, flowString);
    }

    public static String[] getpendingpermissions_All(Context context,String[] permissions,String[] addedPermissions){
        ArrayList<String> pendingpermissions=new ArrayList<>();
        //String[] pendingpermissions_array=new String[permissions.length];
        int j=0;
        for(int i=0;i< permissions.length;i++){

            if((ActivityCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED)){
                pendingpermissions.add(permissions[i]);
            }
        }
        for(int i=0;i< addedPermissions.length;i++){

            if((ActivityCompat.checkSelfPermission(context, addedPermissions[i]) != PackageManager.PERMISSION_GRANTED)){
                pendingpermissions.add(addedPermissions[i]);
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
            for (i = 0; i < myList.size(); i++) {
                Log.d("myTag", "Looping");

               // if ((PhoneNumberUtils.compare(number, myList.get(i).getBuddy_PhNo()))) {
                if ((PhoneNumberUtils.compare(number, myList.get(i).getBuddy_PhNo()))) {
                    MatchfoundinList = true;
                    Log.d("myTag", "Match found");
                    break;
                }
            }

        }
        if(MatchfoundinList==false){
            i=255;
        }
        return i;
    }
    public static int listLoopSearchString(String number, List<String> myList) {
        Boolean MatchfoundinList = false;
        int i=0;
        if (!(myList.isEmpty())) {
            Log.d("myTag", "Object list obtained from Memory");
            for (i = 0; i < myList.size(); i++) {
                Log.d("myTag", "Looping");

                // if ((PhoneNumberUtils.compare(number, myList.get(i).getBuddy_PhNo()))) {
                if ((PhoneNumberUtils.compare(number, myList.get(i)))) {
                    MatchfoundinList = true;
                    Log.d("myTag", "Match found");
                    break;
                }
            }

        }
        if(MatchfoundinList==false){
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
    public static void saveErrorToMemory(Context context,String stringdata){
        String Error=loadStringFromMemory(context,MainActivity.MEMCODE_ERRORMEMORY);
        if(!Error.equals("null")){
            Error=Error.concat("\n");
            Error=Error.concat(stringdata);
        }
        else{
            Error=stringdata;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MainActivity.MEMCODE_ERRORMEMORY,Error);
        editor.commit();

    }

    public static void clearMemory(Context context,String DataID)
    {
        SharedPreferences preferences =context.getSharedPreferences("sharedPrefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(DataID);
        editor.commit();
    }

    public static boolean loadBooleanFromMemory(Context context,String DataID){
        boolean variable;
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        variable=sharedPreferences.getBoolean(DataID,false);
        return variable;
    }

    public static String getDateAndTime(){
        String todaydate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        // Get the current date and time
        String currentTime = sdf.format(new Date());
        return todaydate+"-"+currentTime;
    }

    public static boolean isNotificationServiceEnabled(Context context) {
        // Check if the notification listener service is enabled
        Set<String> enabledListenerServices = NotificationManagerCompat.getEnabledListenerPackages(context);
        return enabledListenerServices.contains(context.getPackageName());
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

    public static boolean isSilentOverriden(Context context,String MemKey) {
        Boolean deviceQuiet=false;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        int dndstatus = notificationManager.getCurrentInterruptionFilter();
        boolean ring = false;
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean overridednd_bool = loadBooleanFromMemory(context, MemKey);

        if ((dndstatus != NotificationManager.INTERRUPTION_FILTER_ALL)||(am.getRingerMode()!=AudioManager.RINGER_MODE_NORMAL)) {
            deviceQuiet=true;
        }
        else{
            deviceQuiet=false;
        }


         /*
        Override    ON  ON  OFF OFF
        deviceQuiet ON  OFF ON  OFF
        Result      T   T   F   T
        True: Ring Device
        False: Dont Ring device
        * */
        if (overridednd_bool) {
            ring = true;
        } else if ((!overridednd_bool)) {
            if (deviceQuiet) {
                ring = false;
            } else {
                ring = true;
            }
        }
        return ring;
    }


    public static HashMap<String, Boolean> convertStringToPermissionHashMap(String loadedmap) {
        Gson gson = new Gson();
        HashMap<String,Boolean> grantedPermissions= new HashMap<String, Boolean>();
        if(!loadedmap.equals("null")) {
            grantedPermissions = gson.fromJson(loadedmap, new TypeToken<HashMap<String, Boolean>>() {
            }.getType());
        }
        return grantedPermissions;
    }
    public static HashMap<String, String> convertStringToLanguageHashMap(String loadedmap) {
        Gson gson = new Gson();
        HashMap<String,String> LanguagesMap= new HashMap<String, String>();
        if(!loadedmap.equals("null")) {
            LanguagesMap = gson.fromJson(loadedmap, new TypeToken<HashMap<String, String>>() {
            }.getType());
        }
        return LanguagesMap;
    }
    public static String convertHashMapToString(Context context,HashMap<String,Boolean> requestedPermissions){
        Gson gson = new Gson();
        String String_requestedPermissions = gson.toJson(requestedPermissions);
        return String_requestedPermissions;
    }


    public static void saveKeyToMemory(Context context, String memcodeMcNotikey, String notiKey) {
        String notikeylist_String_in=loadStringFromMemory(context,memcodeMcNotikey);
        ArrayList<String> notiKeyList=convertGsonStringToArrayList(notikeylist_String_in);
        notiKeyList.add(notiKey);
        saveArrayListToMemory(context,memcodeMcNotikey,notiKeyList);
    }
public static void saveArrayListToMemory(Context context,String memCode,ArrayList<String> listToStore){
    String notikeylist_String_out=convertArrayListToGson(listToStore);
    saveStringToMemory(context,memCode,notikeylist_String_out);
}
    private static String convertArrayListToGson(ArrayList<String> notiKeyList) {
        Gson gson = new Gson();
        String String_requestedPermissions = gson.toJson(notiKeyList);
        return String_requestedPermissions;
    }

    private static ArrayList<String> convertGsonStringToArrayList(String notikeylist_string) {
        Gson gson = new Gson();
        ArrayList<String> storedKeys=new ArrayList<>();
        if(!notikeylist_string.equals("null")) {
            storedKeys = gson.fromJson(notikeylist_string, new TypeToken<List<String>>() {}.getType());
        }
        return storedKeys;
    }
}
