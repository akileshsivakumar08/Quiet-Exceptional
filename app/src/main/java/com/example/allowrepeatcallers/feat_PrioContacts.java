package com.example.allowrepeatcallers;

import android.Manifest;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

class feat_PrioContacts {
    public static int ringerMode;
    public static int Current_RingVolume;
    public static boolean silentExceptionActivated;
    private static boolean silentExceptionRingActivated;
    private static final String MEMCODE_ACTIVATEFEAT="FEAT_SILEXCEPT_ACTIVE";
    private static final String MEMCODE_EXCEPTLIST="STRINGSET_EXCEPTLIST";
    private static final String MEMCODE_VOLUME="SILEXCEPT_VOLUME";
    private static final String MEMCODE_PERMISSIONREQUESTED="SILEXCEPT_PERMISSIONREQUESTED";
    private final int feat_ID=1;

    public void saveVolume(Context context,int inputVolume){
        utilityHelpers.saveIntToMemory(context,MEMCODE_VOLUME,inputVolume);
    }
    private boolean featureActivated=false;
    public static String description;
    public static ArrayList<class_Buddy> PrioContactsList = new ArrayList<>();
    public static String[] permissions= {android.Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS,Manifest.permission.READ_CALL_LOG};

    public int getFeat_ID() {
        return feat_ID;
    }
    public void addToPrioContactsList(class_Buddy buddy,Context context){
    PrioContactsList.add(buddy);
    saveDataToMemory(context);
}
     public boolean isSilentExceptionRingActivated() {
         return silentExceptionRingActivated;
     }

     public void setSilentExceptionRingActivated(boolean silentExceptionRingActivated) {
         feat_PrioContacts.silentExceptionRingActivated = silentExceptionRingActivated;
     }

    public int getPingVolume(Context context) {
        int pingVolume=utilityHelpers.loadIntFromMemory(context,MEMCODE_VOLUME,ringtones.getCurrent_MediaVolume());
        return pingVolume;
    }

    public ArrayList<class_Buddy> getSilExceptList() {
        return PrioContactsList;
    }


    public feat_PrioContacts(Context context) {
        featureActivated = utilityHelpers.loadBooleanFromMemory(context, MEMCODE_ACTIVATEFEAT);
        loadsilExceptListFromMemory(context);
        description=context.getString(R.string.PC_description);
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



    public static void loadsilExceptListFromMemory(Context context) {
         Gson gson = new Gson();
         String String_ExceptList = utilityHelpers.loadStringFromMemory(context,MEMCODE_EXCEPTLIST);
        if(!String_ExceptList.equals("null")) {
             PrioContactsList = gson.fromJson(String_ExceptList, new TypeToken<List<class_Buddy>>() {
             }.getType());
         }
     }

     public Boolean isnumberinList( String senderNum) {
        boolean MatchfoundinList=false;

        int numberID = utilityHelpers.listLoopSearchObj(senderNum, PrioContactsList);
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
        PrioContactsList.remove(id);
        saveDataToMemory(context);
    }

    public void saveDataToMemory(Context context) {
        Gson gson = new Gson();
        String Json_ExceptList = gson.toJson(PrioContactsList);
        utilityHelpers.saveStringToMemory(context,MEMCODE_EXCEPTLIST,Json_ExceptList);
    }


    public int getPrioContactsVolume(Context context) {
        int seekbarVolume=utilityHelpers.loadIntFromMemory(context, MEMCODE_VOLUME, ringtones.getCurrent_MediaVolume());
        return seekbarVolume;
    }
    public static boolean loadBOOL_Permission_already_requested(Context context){
        boolean Permission_already_requested=utilityHelpers.loadBooleanFromMemory(context,MEMCODE_PERMISSIONREQUESTED);
        return Permission_already_requested;
    }
    public void setPermissionRequested(Context context) {
        utilityHelpers.saveBooleanToMemory(context,MEMCODE_PERMISSIONREQUESTED,true);
    }
}

