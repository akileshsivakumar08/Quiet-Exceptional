package com.example.allowrepeatcallers;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class manageWork_AllowRepeatCallers extends Worker {
    public manageWork_AllowRepeatCallers(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context=getApplicationContext();
        feat_RepeatCaller timeout_repeatCaller=new feat_RepeatCaller(context);
        int notiid=getInputData().getInt("NOTIID",255);
        int listid=notiid-1;
        NotificationManager remove_notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        timeout_repeatCaller.nullifyMissedElement(listid,remove_notificationManager);
        timeout_repeatCaller.saveMissedListToMemory(context);
        return null;
    }
}
