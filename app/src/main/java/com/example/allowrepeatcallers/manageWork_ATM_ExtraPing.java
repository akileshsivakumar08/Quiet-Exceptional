package com.example.allowrepeatcallers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;

public class manageWork_ATM_ExtraPing extends Worker {
    public manageWork_ATM_ExtraPing(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context=getApplicationContext();

        feat_AnyTextMatch obj_ATM=new feat_AnyTextMatch(context);
        String notiKey = getInputData().getString("key");
        ArrayList<String> storedKeys =new ArrayList<String>();
        storedKeys=feat_AnyTextMatch.loadATM_KeysFromMemory(context);
        if(storedKeys.contains(notiKey)) {
            obj_ATM.pingcustomText(context,notiKey,0);
            obj_ATM.remove_ATM_Key(context,notiKey);
        }
        return null;
    }
}

