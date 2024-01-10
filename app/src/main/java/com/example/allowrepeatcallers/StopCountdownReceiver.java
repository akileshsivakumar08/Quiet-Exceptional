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
        if(intent.getAction().equals("STOP_SERVICE")){
            class_Buddy nullBuddy=new class_Buddy(null,null,null);
            int notiid=intent.getIntExtra("TIMERID",-1);
            int listid=notiid-1;
            NotificationManager remove_notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            remove_notificationManager.cancel(notiid);
            callReceiver_RepeatCaller.notificationIDcounter--;
           // feat_RepeatCaller.missedList.set(listid,nullBuddy);
            feat_RepeatCaller.missedList.set(listid,"null");
            feat_RepeatCaller.missedList.set(listid,"null");
            feat_RepeatCaller.nullCount=feat_RepeatCaller.nullCount+1;
            if(feat_RepeatCaller.nullCount==feat_RepeatCaller.missedList.size()){
                feat_RepeatCaller.missedList.clear();
                feat_RepeatCaller.nullCount=0;
            }
            Toast.makeText(context, "removedfrom MissedList", Toast.LENGTH_SHORT).show();
            utilityHelpers.saveStringSetToMemory(context,"STRINGSET_MISSEDLIST",feat_RepeatCaller.missedList);
            //WorkManager.getInstance(context).cancelWorkById(getWorkId(notiid));

        }
    }
    //private static UUID getWorkId(int notificationId) {
        //CountdownServiceWork.
  //  }
}
