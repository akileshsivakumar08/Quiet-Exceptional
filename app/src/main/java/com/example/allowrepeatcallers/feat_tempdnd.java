package com.example.allowrepeatcallers;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.quietexceptional.R;

import java.time.Duration;

public class feat_tempdnd {

    public OneTimeWorkRequest tempDND;
    private static NotificationManager notificationManager;
    private boolean featureActivated=false;
    public static String[] permissions= {Manifest.permission.READ_PHONE_STATE};
    public static boolean permission_already_requested;
    private static String description;
    public String[] timeOptions={"5 Minutes","30 Minutes","60 Minutes"};

    private static final String MEMCODE_ACTIVATEFEAT="IS_TEMPDND_ACTIVATED";
    private static final String MEMCODE_PERMISSIONREQUESTED="TEMPDND_PERMISSIONREQUESTED";
    public feat_tempdnd(Context context) {
        featureActivated=utilityHelpers.loadBooleanFromMemory(context,MEMCODE_ACTIVATEFEAT);
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        description = context.getString(R.string.MCA_description);
    }

    public static String getDescription() {
        return description;
    }

    public boolean isFeatureActivated(Context context) {
        featureActivated= utilityHelpers.loadBooleanFromMemory(context, MEMCODE_ACTIVATEFEAT);
        return featureActivated;
    }
public void starttempDND(Context context){

    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
    tempDND=new OneTimeWorkRequest.Builder(manageWork_tempDND.class)
            .setInitialDelay(Duration.ofSeconds(10))
            .addTag("TEMP_DND")
            .build();
    WorkManager.getInstance(context).enqueue(tempDND);

}
    public void stoptempdnd(Context context){
        WorkManager.getInstance(context).cancelAllWorkByTag("TEMP_DND");
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        setFeatureActivated(context, false);


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
}
