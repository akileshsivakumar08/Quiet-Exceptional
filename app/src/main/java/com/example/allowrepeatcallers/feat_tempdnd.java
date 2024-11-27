package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.quietexceptional.R;

import java.time.Duration;

public class feat_tempdnd {

    private static final String MEMCODE_TIMERCHOICE = "TEMPDND_TIMERCHOICE";
    public OneTimeWorkRequest tempDND;
    private static NotificationManager notificationManager;
    private static boolean featureActivated=false;
    public static String[] permissions= {};
    public static boolean permission_already_requested;
    private static String description;
    public String[] timeOptions={"5 Minutes","30 Minutes","60 Minutes"};

    private static final String MEMCODE_ACTIVATEFEAT="IS_TEMPDND_ACTIVATED";
    private static final String MEMCODE_PERMISSIONREQUESTED="TEMPDND_PERMISSIONREQUESTED";
    public feat_tempdnd(Context context) {
        featureActivated=utilityHelpers.loadBooleanFromMemory(context,MEMCODE_ACTIVATEFEAT);
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        description = context.getString(R.string.tempdnddesc);
    }

    public static String getDescription() {
        return description;
    }

    public static boolean isFeatureActivated(Context context) {
        featureActivated= utilityHelpers.loadBooleanFromMemory(context, MEMCODE_ACTIVATEFEAT);
        return featureActivated;
    }
public void starttempDND(Context context){
try {
    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
    String timerChoice = utilityHelpers.loadStringFromMemory(context, MEMCODE_TIMERCHOICE);
    int time_int = 0;
    switch (timerChoice) {
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
            time_int = 2;
            break;
    }
    tempDND = new OneTimeWorkRequest.Builder(manageWork_tempDND.class)
            .setInitialDelay(Duration.ofMinutes(time_int))
            .addTag("TEMP_DND")
            .build();
    WorkManager.getInstance(context).enqueue(tempDND);
}
catch (Exception e){
    String ErrorFlow="Tempdnd_error in feat_StarttempDND";
    Log.e(TAG,"Tempdnd_error in feat_StarttempDND");
    utilityHelpers.saveErrorToMemory(context,ErrorFlow);
}

}
    public void stoptempdnd(Context context){
        try {
            WorkManager.getInstance(context).cancelAllWorkByTag("TEMP_DND");
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            setFeatureActivated(context, false);
        }catch (Exception e){
            String ErrorFlow="Tempdnd_error in feat_StoptempDND";
            Log.e(TAG,"Tempdnd_error in feat_StoptempDND");
            utilityHelpers.saveErrorToMemory(context,ErrorFlow);
        }

    }
    public void setFeatureActivated(Context context,boolean featureActivated) {
        utilityHelpers.saveBooleanToMemory(context,MEMCODE_ACTIVATEFEAT,featureActivated);
        this.featureActivated = featureActivated;
    }

public boolean arePermissionsPending(Context context){
    NotificationManager tap_notificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    if ((!utilityHelpers.isNotificationServiceEnabled(context)) && (!tap_notificationManager.isNotificationPolicyAccessGranted())) {
        return true;
    }
    else{
        return false;
    }
}
    public void setPermissionRequested(Context context) {
        utilityHelpers.saveBooleanToMemory(context,MEMCODE_PERMISSIONREQUESTED,true);
    }
    public static boolean loadBOOL_Permission_already_requested(Context context){
            boolean Permission_already_requested=utilityHelpers.loadBooleanFromMemory(context,MEMCODE_PERMISSIONREQUESTED);
        return Permission_already_requested;
    }

    public String getSelectedTimerChoice(Context context) {
        String timerchoice=utilityHelpers.loadStringFromMemory(context,MEMCODE_TIMERCHOICE);
        if(!timerchoice.equals("null")){
            return timerchoice;
        }
         else{
             return "10 Seconds";
        }
    }

    public void savetimerChoice(Context context, String selectedItem) {
        utilityHelpers.saveStringToMemory(context,MEMCODE_TIMERCHOICE,selectedItem);
    }
}
