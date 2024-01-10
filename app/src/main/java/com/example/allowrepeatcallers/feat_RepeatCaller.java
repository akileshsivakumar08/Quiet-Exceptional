package com.example.allowrepeatcallers;

import android.Manifest;
import android.app.NotificationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class feat_RepeatCaller {
    //public static List<class_Buddy> missedList=new ArrayList<>();
    public static  ArrayList<String> missedList=new ArrayList<>();
    public static boolean repeatCaller=false;
    public static int ringerMode;
    public static boolean stateRINGING=false;
    public static boolean featureActivated=true;
    public static int Current_RingVolume;
    public static String repeatCallerRingActivated="NULL";
    public static String[] permissions= {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG,Manifest.permission.POST_NOTIFICATIONS};
    public static boolean upToDown;
    public static int nullCount;
    public static int notificationIDcounter;
    // public static NotificationManager notificationManager;

    public static void nullifyMissedElement(int numberID,NotificationManager notificationManager) {
        int notiid = numberID + 1;
        if (255 != numberID) {

            feat_RepeatCaller.missedList.set(numberID, "null");
            feat_RepeatCaller.nullCount = feat_RepeatCaller.nullCount + 1;
            if (feat_RepeatCaller.nullCount == feat_RepeatCaller.missedList.size()) {
                feat_RepeatCaller.missedList.clear();
                feat_RepeatCaller.nullCount = 0;
            }

            notificationManager.cancel(notiid);
            feat_RepeatCaller.notificationIDcounter--;
        }
    }
    public static void addToMissedList(String p_logName,String p_logNumber,String text) {
        //create buddy with phone number and name
        //Add to missed list
        class_Buddy missedBuddy=new class_Buddy(p_logName,p_logNumber,text);
       // missedList.add(missedBuddy);
        missedList.add(p_logNumber);
    }
    public static boolean isNumberRepeatCaller(String number) {
        Boolean MatchfoundinList;
        int numberID =utilityHelpers.listLoopSearch(number,missedList);
        if(numberID==255){
            MatchfoundinList=false;
            feat_RepeatCaller.repeatCaller = false;
        }
        else{
            MatchfoundinList=true;
            feat_RepeatCaller.repeatCaller = true;
        }
        return MatchfoundinList;
    }
}
