package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class settings extends AppCompatActivity {

    Switch overridednd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Context context=getApplicationContext();
        overridednd=findViewById(R.id.overridednd);
        boolean overridechecked=utilityHelpers.loadBooleanFromMemory(context,"OVERRIDE_DND");
        if(overridechecked){
            overridednd.setChecked(true);
        }

        else{
            overridednd.setChecked(false);
        }
        overridednd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean overridednd_bool;
                if(overridednd.isChecked()){
                    overridednd_bool=true;
                }
                else{
                    overridednd_bool=false;
                }
                utilityHelpers.saveBooleanToMemory(context,"OVERRIDE_DND",overridednd_bool);
            }
        });
    }


}
