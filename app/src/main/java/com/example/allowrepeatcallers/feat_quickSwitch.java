package com.example.allowrepeatcallers;

import android.content.Context;

public class feat_quickSwitch {

    public static int upcounter;
    public static int downcounter;
    public static int Current_MediaVolume;
    public static long currentTime;
    public static long timediff;
    public static long oldtime;
    public static int call_upcounter;
    public static int call_downcounter;
    private boolean featureActivated;
    private static String description;
    private final String requestAccessibility;
    private static final String MEMCODE_ACTIVATEFEAT="FEAT_QUICKSWITCH_ACTIVE";
    public boolean isFeatureActivated(Context context) {
        featureActivated= utilityHelpers.loadBooleanFromMemory(context, MEMCODE_ACTIVATEFEAT);
        return featureActivated;
    }

    public String getRequestAccessibility() {
        return requestAccessibility;
    }

    public feat_quickSwitch(Context context) {
        featureActivated = utilityHelpers.loadBooleanFromMemory(context, MEMCODE_ACTIVATEFEAT);
        description=context.getString(R.string.QS_description);
        requestAccessibility=context.getString(R.string.QS_RequestAccessibility);
    }
    public void setFeatureActivated(Context context,boolean featureActivated) {
        utilityHelpers.saveBooleanToMemory(context,MEMCODE_ACTIVATEFEAT,featureActivated);
        this.featureActivated = featureActivated;
    }
    public static String getDescription() {
        return description;
    }
}
