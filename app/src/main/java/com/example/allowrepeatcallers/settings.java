package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.quietexceptional.R;


public class settings extends AppCompatActivity {

    SwitchCompat overridednd;
    TextView configure;
    Spinner timechoices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Context context=getApplicationContext();
        ArrayAdapter<CharSequence> adapter;
        feat_AlertMissedCalls obj_AMC=new feat_AlertMissedCalls(context);
        overridednd=findViewById(R.id.overridednd);
        configure=findViewById(R.id.configuretext);

        boolean overridechecked=utilityHelpers.loadBooleanFromMemory(context,"AMC_OVERRIDE_DND");
        if(overridechecked){
            overridednd.setChecked(true);
        }

        else{
            overridednd.setChecked(false);
        }



        configure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(context);
            }
        });
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
                utilityHelpers.saveBooleanToMemory(context,"AMC_OVERRIDE_DND",overridednd_bool);
            }
        });

    }

    private void createDialog(Context context) {
        try {
            //start a dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(settings.this,R.style.AlertDialogStyle);
            final EditText textInput=new EditText(context);
            builder.setTitle(R.string.custom_match_text_input);
            builder.setMessage(R.string.confirm_missedcall_dialog_text);
            textInput.setInputType(InputType.TYPE_CLASS_TEXT);
            String HintText=LanguageManager.getCustomText(context);
            if(HintText.equals("null")){
                HintText=LanguageManager.getMissedCallText(context);
            }
            textInput.setHint(HintText);
            builder.setView(textInput);
                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String text= textInput.getText().toString();
                            if (text.isEmpty()) {

                            } else {
                                LanguageManager.setCustomText(context, textInput.getText().toString());
                            }


                        }
                    });
                    builder.setNegativeButton(R.string.cancel_menu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            String ErrorFlow=" Settings_configure_Exception on dialog  ";
            utilityHelpers.saveErrorToMemory(context,ErrorFlow);
            Log.e(TAG, " Exception on dialog  " + e);
            Toast.makeText(context, " Exception on dialog ", Toast.LENGTH_SHORT).show();
        }
    }

}
