package com.example.allowrepeatcallers;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.service.notification.StatusBarNotification;

import androidx.work.OneTimeWorkRequest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

class feat_AllowRepeatCallers {
    //public static List<class_Buddy> missedList=new ArrayList<>();
    private static ArrayList<String> missedList=new ArrayList<>();
    private static boolean stateRINGING=false;
    private boolean featureActivated=false;

    private static final String MEMCODE_VOLUME="ARC_VOLUME";
    private static final String MEMCODE_PERMISSIONREQUESTED="REPEATCALLER_PERMISSIONREQUESTED";

    public static String getDescription() {
        return description;
    }

    public int getPingVolume(Context context) {
        int pingVolume=utilityHelpers.loadIntFromMemory(context,MEMCODE_VOLUME,ringtones.getCurrent_MediaVolume(context));
        return pingVolume;
    }
    public void saveVolume(Context context,int inputVolume){
        utilityHelpers.saveIntToMemory(context,MEMCODE_VOLUME,inputVolume);
    }

    private static String description= String.valueOf(R.string.ARC_description);


    public void setFeatureActivated(Context context,boolean featureActivated) {
        utilityHelpers.saveBooleanToMemory(context,"FEAT_REPEATCALLER_ACTIVE",featureActivated);
        this.featureActivated = featureActivated;
    }

    private static boolean repeatCallerRingActivated=false;
    public static String[] permissions= {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG,Manifest.permission.POST_NOTIFICATIONS};
    private static int nullCount;
    public static int notificationIDcounter=0;

    public boolean isRepeatCallerRingActivated() {
        return repeatCallerRingActivated;
    }

    public ArrayList<String> getMissedList() {
        return missedList;
    }

    private final int feat_ID=1;
    public OneTimeWorkRequest tenMinuteDelete;

    public boolean isStateRINGING() {
        return stateRINGING;
    }

    public boolean isFeatureActivated() {
        return featureActivated;
    }

    public void setRepeatCallerRingActivated(boolean repeatCallerRingActivated) {
        this.repeatCallerRingActivated = repeatCallerRingActivated;
    }

    public void setStateRINGING(boolean stateRINGING) {
        this.stateRINGING = stateRINGING;
    }

    public int getFeat_ID() {
        return feat_ID;
    }

    public feat_AllowRepeatCallers(Context context) {
        featureActivated = utilityHelpers.loadBooleanFromMemory(context, "FEAT_REPEATCALLER_ACTIVE");
        missedList = loadMissedListFromMemory(context);
        description=context.getString(R.string.ARC_description);

    }
    // public static NotificationManager notificationManager;

    public void nullifyMissedElement(int numberID,NotificationManager notificationManager) {
        int notiid = numberID + 1;
        if (255 != numberID) {

            missedList.set(numberID, "null");
            nullCount = nullCount + 1;
            if (nullCount == missedList.size()) {
                missedList.clear();
                nullCount = 0;
                notificationIDcounter=0;
            }

            notificationManager.cancel(notiid);
            //notificationIDcounter--;
        }
    }
    void addToMissedList(String p_logName,String p_logNumber,String text) {
        //create buddy with phone number and name
        //Add to missed list
        class_Buddy missedBuddy=new class_Buddy(p_logName,p_logNumber,text);
       // missedList.add(missedBuddy);
        missedList.add(p_logNumber);
    }
    boolean isNumberRepeatCaller(String number) {
        Boolean MatchfoundinList;
        Boolean repeatCaller;
        int numberID =utilityHelpers.listLoopSearchString(number,missedList);
        if(numberID==255){
            MatchfoundinList=false;
        }
        else{
            MatchfoundinList=true;
        }
        return MatchfoundinList;
    }

    public void clearAndSaveMissedList(Context context) {
        missedList.clear();
        saveMissedListToMemory(context);
    }

    public void matchActiveNotificationsWithMissedList(Context context,NotificationManager notificationManager) {
        StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();

        int numberOfActiveNotifications = activeNotifications.length;
        if ((numberOfActiveNotifications == 0) && (missedList.size() > 0)) {
            clearAndSaveMissedList(context);
        }
    }

    public void clearNotifications(NotificationManager notificationManager) {
        notificationManager.cancelAll();
        notificationIDcounter=0;
    }

    public void saveMissedListToMemory(Context context) {
        Gson gson = new Gson();
        String string_missedList = gson.toJson(missedList);
        utilityHelpers.saveStringToMemory(context,"STRINGSET_MISSEDLIST",string_missedList);
    }
    public ArrayList<String> loadMissedListFromMemory(Context context) {
        Gson gson = new Gson();
        ArrayList<String> missedList=new ArrayList<>();
        String String_missedList = utilityHelpers.loadStringFromMemory(context,"STRINGSET_MISSEDLIST");
        if(!String_missedList.equals("null")) {
            missedList = gson.fromJson(String_missedList, new TypeToken<List<String>>() {
            }.getType());
        }
        return  missedList;
    }
}
