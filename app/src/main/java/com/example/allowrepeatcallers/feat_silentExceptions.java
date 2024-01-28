package com.example.allowrepeatcallers;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

class feat_silentExceptions{
    public static int ringerMode;
    public static int Current_RingVolume;
    public static boolean silentExceptionActivated;
    private static boolean silentExceptionRingActivated;
    public static final String MEMORYSTRING = "text";
    private final int feat_ID=1;
     private boolean featureActivated=false;
    public static ArrayList<class_Buddy> silExceptList = new ArrayList<>();
    public String[] permissions= {android.Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS};

    public int getFeat_ID() {
        return feat_ID;
    }
public void addTosilExceptList(class_Buddy buddy){
    silExceptList.add(buddy);
}
     public boolean isSilentExceptionRingActivated() {
         return silentExceptionRingActivated;
     }

     public void setSilentExceptionRingActivated(boolean silentExceptionRingActivated) {
         this.silentExceptionRingActivated = silentExceptionRingActivated;
     }

    public ArrayList<class_Buddy> getSilExceptList() {
        return silExceptList;
    }

    public boolean isFeatureActivated() {
        return featureActivated;
    }

    public void setFeatureActivated(boolean featureActivated) {
        this.featureActivated = featureActivated;
    }

    public feat_silentExceptions(Context context) {
         featureActivated = utilityHelpers.loadBooleanFromMemory(context, "FEAT_REPEATCALLER_ACTIVE");
         loadsilExceptListFromMemory(context);
     }

     public static void loadsilExceptListFromMemory(Context context) {
         Gson gson = new Gson();
         String String_ExceptList = utilityHelpers.loadStringFromMemory(context,"STRINGSET_EXCEPTLIST");
         if(!String_ExceptList.equals("null")) {
             silExceptList = gson.fromJson(String_ExceptList, new TypeToken<List<class_Buddy>>() {
             }.getType());
         }
     }

     public static Boolean loadlistAndSearch(Context context, String senderNum) {
        boolean MatchfoundinList=false;

        loadsilExceptListFromMemory(context);
        int numberID = utilityHelpers.listLoopSearchObj(senderNum,silExceptList);
        if(numberID==255){
            MatchfoundinList=false;
        }
        else{
            MatchfoundinList=true;
        }
        return MatchfoundinList;
    }

    public void deletebuddy(int id,Context context){
        loadsilExceptListFromMemory(context);
        silExceptList.remove(id);
        saveDataToMemory(context);
       // refreshlistview(silExceptList);
    }

    public void saveDataToMemory(Context context) {
        Gson gson = new Gson();
        String Json_ExceptList = gson.toJson(silExceptList);
        utilityHelpers.saveStringToMemory(context,"STRINGSET_EXCEPTLIST",Json_ExceptList);
    }



}

