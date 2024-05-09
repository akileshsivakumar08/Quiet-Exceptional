package com.example.allowrepeatcallers;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quietexceptional.R;


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
