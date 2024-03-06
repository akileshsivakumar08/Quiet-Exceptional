package com.example.allowrepeatcallers;

import android.content.Context;

class feat_AlertMissedCalls {

    private boolean featureActivated=false;
    public static String[] permissions= {android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_CALL_LOG};
    public static boolean permission_already_requested;
    private static String description;

    public int getPingVolume(Context context) {
        int pingVolume=utilityHelpers.loadIntFromMemory(context,MEMCODE_VOLUME,ringtones.getCurrent_MediaVolume());
        return pingVolume;
    }
    public void saveVolume(Context context,int inputVolume){
        utilityHelpers.saveIntToMemory(context,MEMCODE_VOLUME,inputVolume);
    }

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
}
