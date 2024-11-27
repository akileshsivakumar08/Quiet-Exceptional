package com.example.allowrepeatcallers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;

public class manageWork_AMC_ExtraPing extends Worker {
    public manageWork_AMC_ExtraPing(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context=getApplicationContext();

        feat_AlertMissedCalls obj_AMC=new feat_AlertMissedCalls(context);
        String notiKey = getInputData().getString("key");
        ArrayList<String> storedKeys =new ArrayList<String>();
        storedKeys=feat_AlertMissedCalls.loadMissedCallListFromMemory(context);
        if(storedKeys.contains(notiKey)) {
            obj_AMC.pingMissedCall(context,notiKey,0);
            obj_AMC.removeMissedCall(context,notiKey);
        }
        return null;
    }
}

