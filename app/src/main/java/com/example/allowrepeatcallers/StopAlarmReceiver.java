package com.example.allowrepeatcallers;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class StopAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("STOP_SERVICE")){
            feat_SMSAlarm obj_SMSAlarm=new feat_SMSAlarm(context);
            feat_SMSAlarm.AlarmSound.stoptune(context,feat_SMSAlarm.player);
            obj_SMSAlarm.setSilentExceptionRingActivated(false);
            NotificationManager remove_notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            remove_notificationManager.cancel(1);
            remove_notificationManager.setInterruptionFilter(obj_SMSAlarm.interruptionFilter);
        }
    }
    //private static UUID getWorkId(int notificationId) {
        //CountdownServiceWork.
  //  }
}
