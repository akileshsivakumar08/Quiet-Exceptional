package com.example.allowrepeatcallers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class troubleshoot extends AppCompatActivity {
    TextView testNotification;
    TextView testCallLog;
    TextView getMissedList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.troubleshoot);
        Context context=getApplicationContext();
        testNotification=findViewById(R.id.testNotification);
        testCallLog=findViewById(R.id.testCallLog);
        getMissedList=findViewById(R.id.getmissedlist);
        getMissedList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feat_AllowRepeatCallers obj_getmissedlist=new feat_AllowRepeatCallers(context);
                String string_missedList="\n";
                ArrayList<String>local_missedList=obj_getmissedlist.getMissedList();
                if(local_missedList.size()==0){
                    string_missedList="No Active Missed Calls";
                }
                for(int i=0;i<local_missedList.size();i++){
                    string_missedList=string_missedList+local_missedList.get(i)+"\n";
                }
                AlertDialog.Builder noti_alertbuilder = new AlertDialog.Builder(com.example.allowrepeatcallers.troubleshoot.this);
                noti_alertbuilder.setMessage(string_missedList);
                AlertDialog alertDialog = noti_alertbuilder.create();
                alertDialog.show();
            }
        });
        testCallLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String logNumber;
                String logType;
                Uri uriCallLogs = Uri.parse("content://call_log/calls");
                Cursor cursorCallLogs = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    cursorCallLogs = getApplicationContext().getContentResolver().query(uriCallLogs, null, null, null);
                }
                cursorCallLogs.moveToLast();
                String logDate = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.DATE));
                long lastTime=Long.parseLong(logDate);
                cursorCallLogs.moveToFirst();
                logDate = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.DATE));
                long firstTime=Long.parseLong(logDate);
                long currentTime=System. currentTimeMillis();
                if(firstTime>lastTime) {
                    logNumber = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.NUMBER));
                    logType=cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE));
                }
                else{
                    cursorCallLogs.moveToLast();
                    logNumber = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.NUMBER));
                    logType=cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE));
                }
                AlertDialog.Builder noti_alertbuilder = new AlertDialog.Builder(com.example.allowrepeatcallers.troubleshoot.this);
                noti_alertbuilder.setMessage("LastTime="+lastTime+"\n"+"FirstTime="+firstTime+"\n"+"Now:"+currentTime+"\n"+"\n"+"\n"+"Number="+logNumber+"\n"+"Type="+logType);
                AlertDialog alertDialog = noti_alertbuilder.create();
                alertDialog.show();
            }
        });
        testNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManager post_notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                /*CharSequence chname = "ChannelName";
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, chname,importance);
                post_notificationManager.createNotificationChannel(mChannel);*/
                String NotiString="Test";
                int notificationID=1;
                Notification notification = utilityHelpers.buildNotification(NotiString, "Swipe or press Stop to mute next Call", notificationID,getApplicationContext());
                                       /* NotificationManager manager = null;
                                        manager = context.getSystemService(NotificationManager.class);*/
                post_notificationManager.notify(notificationID, notification);
            }
        });
    }


}
