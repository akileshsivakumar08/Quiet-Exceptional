package com.example.allowrepeatcallers;

import android.Manifest;

public class feat_BabyPing {
    public static boolean featureActivated=true;
    public static String[] permissions= {android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_CALL_LOG};
    public static boolean permission_already_requested;
}
