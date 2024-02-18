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

    public boolean isFeatureActivated() {
        return featureActivated;
    }

    public void setFeatureActivated(boolean featureActivatedlocal) {
        featureActivated = featureActivatedlocal;
        //utilityHelpers.saveBooleanToMemory(context,"IS_QUICKSWITCH_ACTIVATED",featureActivated);
    }

    public feat_quickSwitch(Context context) {
        featureActivated = utilityHelpers.loadBooleanFromMemory(context, "FEAT_QUICKSWITCH_ACTIVE");
        description=context.getString(R.string.QS_description);
    }
}
