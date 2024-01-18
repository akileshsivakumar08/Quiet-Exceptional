package com.example.allowrepeatcallers;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class StopCountdownReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        feat_RepeatCaller stropButton_repeatCaller=new feat_RepeatCaller(context);
        if(intent.getAction().equals("STOP_SERVICE")){
            int notiid=intent.getIntExtra("TIMERID",-1);
            int listid=notiid-1;
            NotificationManager remove_notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            stropButton_repeatCaller.nullifyMissedElement(listid,remove_notificationManager);

            Toast.makeText(context, "removedfrom MissedList", Toast.LENGTH_SHORT).show();
            stropButton_repeatCaller.saveMissedListToMemory(context);

            //WorkManager.getInstance(context).cancelWorkById(getWorkId(notiid));

        }
    }
    //private static UUID getWorkId(int notificationId) {
        //CountdownServiceWork.
  //  }
}
