package com.example.allowrepeatcallers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class manageWork_tempDND extends Worker {
    public manageWork_tempDND(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    Intent tileServiceIntent;
    @NonNull
    @Override
    public Result doWork() {
        Context context=getApplicationContext();
        feat_tempdnd obj_tempdnd=new feat_tempdnd(context);
        if(obj_tempdnd.isFeatureActivated(context)) {
            obj_tempdnd.stoptempdnd(context);
            CustomTileService.requestListeningState(context, new ComponentName(context, CustomTileService.class));
            tileServiceIntent = new Intent(context, CustomTileService.class);
            tileServiceIntent.putExtra("isActive", false);
            context.startService(tileServiceIntent);

            Intent broadcastIntent=new Intent();
            broadcastIntent.putExtra("TDND_State","STOP_WORKOVER");
            broadcastIntent.setAction("com.example.allowrepeatcallers.TIMERWORK_OVER");
            context.sendBroadcast(broadcastIntent);
        }
        else{
            utilityHelpers.saveStringToMemory(context, "WorkManager_TempDND", "TempDND_Inactive in manage work");
        }

        return null;
    }
}

