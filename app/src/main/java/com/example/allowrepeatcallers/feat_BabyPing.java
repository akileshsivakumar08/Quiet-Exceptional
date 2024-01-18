package com.example.allowrepeatcallers;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;

class feat_BabyPing {

    private boolean featureActivated=false;
    public static String[] permissions= {android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_CALL_LOG};
    public static boolean permission_already_requested;

    public feat_BabyPing(Context context) {
        featureActivated=utilityHelpers.loadBooleanFromMemory(context,"IS_BABYPING_ACTIVATED");

    }

    public boolean isFeatureActivated() {
        return featureActivated;
    }

    public void setFeatureActivated(boolean featureActivated) {
        this.featureActivated = featureActivated;
    }

    public boolean isDNDOverridden(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        int dndstatus = notificationManager.getCurrentInterruptionFilter();
        boolean overridednd_bool = utilityHelpers.loadBooleanFromMemory(context, "OVERRIDE_DND");
        boolean overriden = false;
        if (overridednd_bool) {
            overriden = true;
        } else if ((!overridednd_bool)) {
            if (dndstatus == NotificationManager.INTERRUPTION_FILTER_ALL) {
                overriden = true;
            } else {
                overriden = false;
            }
        }
        return overriden;
    }
}
