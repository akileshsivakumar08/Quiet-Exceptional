package com.example.allowrepeatcallers;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Parcelable;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.quietexceptional.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class feat_AlertMissedCalls{

    private static final String MEMCODE_MC_NOTIKEY = "MC_NOTIKEY";
    private static final String MEMCODE_CUSTOMTEXT_MISSEDCALL = "MAP_CUSTOMTEXT_MISSEDCALL";
    private static final String MEMCODE_AMC_RINGTONE = "MC_AMC_RINGTONE";
    public OneTimeWorkRequest twoSecondPause;
    public OneTimeWorkRequest work_additionalPing;
    private static final String TAG = "QuietExceptional";
    public boolean additionalPingDismissed=false;
    private boolean featureActivated=false;
    public static String[] permissions= {Manifest.permission.READ_PHONE_STATE};
    public static String[] addedPermissions={Manifest.permission.MODIFY_AUDIO_SETTINGS};
    public static boolean permission_already_requested;
    private static String description;
    static String FLOW="NL_AMC_FLOW";
    private String MEMCODE_ADDITIONALPING="MEMCODE_ADDITIONALPING";

    private String MEMCODE_AMCTIMERCHOICE="MEMCODE_AMCTIMERCHOICE";
    public int getPingVolume(Context context) {
        int pingVolume= utilityHelpers.loadIntFromMemory(context,MEMCODE_VOLUME, ringtones.getCurrent_MediaVolume(context));
        return pingVolume;
    }
    public void saveVolume(Context context,int inputVolume){
        utilityHelpers.saveIntToMemory(context,MEMCODE_VOLUME,inputVolume);
    }
private static  final String MEMCODE_OVERRIDEDND="AMC_OVERRIDE_DND";
    private static final String MEMCODE_ACTIVATEFEAT="IS_AMC_ACTIVATED";
    private static final String MEMCODE_VOLUME="AMC_VOLUME";
    private static final String MEMCODE_PERMISSIONREQUESTED="AMC_PERMISSIONREQUESTED";
    public feat_AlertMissedCalls(Context context) {
        featureActivated=utilityHelpers.loadBooleanFromMemory(context,MEMCODE_ACTIVATEFEAT);
        description = context.getString(R.string.MCA_description);
    }

    public static String getDescription() {
        return description;
    }

    public boolean isFeatureActivated(Context context) {
        featureActivated= utilityHelpers.loadBooleanFromMemory(context, MEMCODE_ACTIVATEFEAT);
        return featureActivated;
    }

    public void setFeatureActivated(Context context,boolean featureActivated) {
        utilityHelpers.saveBooleanToMemory(context,MEMCODE_ACTIVATEFEAT,featureActivated);
        this.featureActivated = featureActivated;
    }


    public void setPermissionRequested(Context context) {
        utilityHelpers.saveBooleanToMemory(context,MEMCODE_PERMISSIONREQUESTED,true);
    }
    public static boolean loadBOOL_Permission_already_requested(Context context){
            boolean Permission_already_requested=utilityHelpers.loadBooleanFromMemory(context,MEMCODE_PERMISSIONREQUESTED);
        return Permission_already_requested;
    }

    public String pingMissedCall(Context context,String notiKey,int type){
        String ErrorFlow="Enteredfeat_AMC";
        try {

            if (!utilityHelpers.ispermissionpending(context, feat_AlertMissedCalls.permissions)) {
                ErrorFlow = "NR_AMC_Permissions Provided";
                if (isFeatureActivated(context)) {
                    ErrorFlow = "Feature Activated";
                    ErrorFlow = "NR_AMC_Missed Call Detected";
                    AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    ErrorFlow = "NR_AMC_Init Complete";
                    boolean ringDevice = (utilityHelpers.isSilentOverriden(context,MEMCODE_OVERRIDEDND));
                    if (ringDevice) {
                        ErrorFlow = "NR_AMC_Ring Device Activated";
                        if ((am.getMode() != AudioManager.MODE_IN_COMMUNICATION) && (am.getMode() != AudioManager.MODE_IN_CALL)) {
                            ErrorFlow = "NR_AMC_No Active calls";
                            if(!feat_tempdnd.isFeatureActivated(context)){
                                utilityHelpers.turnSpeakerON(am);
                                int setVolume = getPingVolume(context);
                                am.setStreamVolume(AudioManager.STREAM_MUSIC, setVolume, 0);
                                ringtones shorttune = new ringtones(context, 2,getRingtoneUri(context));
                                if (utilityHelpers.isDeviceConnected(am)) {
                                    ErrorFlow = "NR_AMC_Playing on connected Device";
                                    am.setStreamVolume(AudioManager.STREAM_MUSIC, (am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) / 2, 0);
                                }

                                shorttune.playShorttune(context);
                                /*type =1 means the function has been called form notification listener.type =0 means the function has been called from manage_work*/
                                if(type==1) {
                                    utilityHelpers.saveKeyToMemory(context, MEMCODE_MC_NOTIKEY, notiKey);
                                    int AddPingTime = getAddPingTime(context);
                                    if (AddPingTime != 0)
                                    {
                                        String additionalPing = utilityHelpers.loadStringFromMemory(context, MEMCODE_ADDITIONALPING);
                                    if (!additionalPing.equals("None")) {
                                        Data inputData = new Data.Builder()
                                                .putString("key", notiKey)
                                                .build();
                                        work_additionalPing = new OneTimeWorkRequest.Builder(manageWork_AMC_ExtraPing.class)
                                                .setInitialDelay(Duration.ofMinutes(AddPingTime))
                                                .setInputData(inputData)
                                                .build();
                                        WorkManager.getInstance(context).enqueue(work_additionalPing);
                                    }
                                }
                                }
                                ErrorFlow = "NR_AMC_PlayingTune"+"savedVolume"+ am.getStreamVolume(AudioManager.STREAM_MUSIC);
                                //am.setStreamVolume(AudioManager.STREAM_MUSIC,Current_MediaVolume,0);

                            }
                            else {
                                ErrorFlow = "NR_AMC_Tempdnd_activated";
                            }


                        }
                        else{
                            ErrorFlow = "NR_AMC_Not playing due to ongoing call";
                        }
                    }
                    else{
                        ErrorFlow = "NR_AMC_Ring Device Not Active";
                    }
                    //  Toast.makeText(context, "This a toast message", Toast.LENGTH_LONG).show();
                }
                else{
                    ErrorFlow = "NR_AMC_Feature Not Active";
                }
            }
            else{
                ErrorFlow = "NR_AMC_Permission Pending";
            }
            utilityHelpers.saveFlowToMemory(context,ErrorFlow,feat_AlertMissedCalls.FLOW);

        } catch (Exception e) {
            Log.e(TAG, ErrorFlow);
            String DateAndTime=utilityHelpers.getDateAndTime();
            ErrorFlow = DateAndTime+"-"+ErrorFlow;
            utilityHelpers.saveErrorToMemory(context, "ERROR"+ErrorFlow);
            throw new RuntimeException(e);
        }
        return ErrorFlow;
    }

    private int getAddPingTime(Context context) {
        String timerChoice = utilityHelpers.loadStringFromMemory(context, MEMCODE_AMCTIMERCHOICE);
        int time_int = 0;
        switch (timerChoice) {
            case "None":
                time_int=0;
                break;
            case "2 Minutes":
                time_int = 2;
                break;
            case "5 Minutes":
                time_int = 5;
                break;
            case "8 Minutes":
                time_int = 8;
                break;
            default:
                time_int = 0;
                break;
        }
        return time_int;
    }

    public void playTuneAMC(){

    }

    public void removeMissedCall(Context context,String notiKey) {
        ArrayList<String> storedKeys=new ArrayList<>();
        storedKeys=loadMissedCallListFromMemory(context);
        if(storedKeys.contains(notiKey)){
            for(int i =0;i<storedKeys.size();i++){
                if(storedKeys.get(i).equals(notiKey)){
                    storedKeys.remove(i);
                    utilityHelpers.saveArrayListToMemory(context,MEMCODE_MC_NOTIKEY,storedKeys);
                }
            }
        }

    }
    public void savetimerChoice(Context context, String selectedItem) {
        utilityHelpers.saveStringToMemory(context,MEMCODE_AMCTIMERCHOICE,selectedItem);
    }
    public String getSelectedTimerChoice(Context context) {
        String timerchoice=utilityHelpers.loadStringFromMemory(context,MEMCODE_AMCTIMERCHOICE);
        if(!timerchoice.equals("null")){
            return timerchoice;
        }
        else{
            return "10 Seconds";
        }
    }
    public static ArrayList<String> loadMissedCallListFromMemory(Context context) {
        Gson gson = new Gson();
        ArrayList<String> storedKeys=new ArrayList<>();
        String String_KeyList = utilityHelpers.loadStringFromMemory(context,MEMCODE_MC_NOTIKEY);
        if(!String_KeyList.equals("null")) {
            storedKeys = gson.fromJson(String_KeyList, new TypeToken<List<String>>() {}.getType());
        }
        return storedKeys;
    }


    public void saveRingInDND(Context context, boolean ringInDND) {
        utilityHelpers.saveBooleanToMemory(context,MEMCODE_OVERRIDEDND,ringInDND);
    }
    public boolean loadRingInDND(Context context) {
        return utilityHelpers.loadBooleanFromMemory(context,MEMCODE_OVERRIDEDND);
    }


    public ArrayList<class_setting> getSettingsList(Context context) {
        boolean isRingInDND = loadRingInDND(context);
        int volume = getPingVolume(context);
        String timerChoice = getSelectedTimerChoice(context);
        String ringtoneuri=getRingtoneUri(context);
        class_setting setting = new class_setting(timerChoice, volume, isRingInDND,ringtoneuri);
        ArrayList<class_setting> settingList = new ArrayList<>();
        settingList.add(setting);
        return settingList;
    }


    public ArrayList<String> loadCustomTextFromMemory(Context context) {
        Gson gson = new Gson();
        ArrayList<String> customTextList=new ArrayList<>();
        String String_ctl = utilityHelpers.loadStringFromMemory(context,MEMCODE_CUSTOMTEXT_MISSEDCALL);
        if(!String_ctl.equals("null")) {
            customTextList = gson.fromJson(String_ctl, new TypeToken<List<String>>() {}.getType());
        }
        return customTextList;
    }

    public void saveCustomTextList(Context context, ArrayList<String> outputList) {
        utilityHelpers.saveArrayListToMemory(context,MEMCODE_CUSTOMTEXT_MISSEDCALL,outputList);
    }

    public void saveRingtoneUri(Context context, String ringtoneUri) {
        utilityHelpers.saveStringToMemory(context,MEMCODE_AMC_RINGTONE,ringtoneUri);
    }
    public String getRingtoneUri(Context context){
        return utilityHelpers.loadStringFromMemory(context,MEMCODE_AMC_RINGTONE);
    }
}
