package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.quietexceptional.R;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class settings extends AppCompatActivity {

    SwitchCompat overridednd;
    Spinner timechoices;
    SeekBar seekbar;
    TextView taptotestvolume;
    boolean overridednd_bool;
    String selectedItem;
    int pingvolume;
    TextView change_ringtone;
    String string_ringtone_uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Context context=getApplicationContext();
        ArrayAdapter<CharSequence> adapter;
        feat_AlertMissedCalls obj_AMC=new feat_AlertMissedCalls(context);
        overridednd=findViewById(R.id.overridednd);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        taptotestvolume = (TextView) findViewById(R.id.taptotestvolume);
        timechoices = (Spinner) findViewById(R.id.timechoices);
        change_ringtone=(TextView) findViewById(R.id.change_ringtone);

        ArrayList<class_setting> loadedSettings_List = getIntent().getParcelableArrayListExtra("settingList_input");
        class_setting loadedSettings=loadedSettings_List.get(0);


        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        int maxMusicVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekbar.setMax(maxMusicVolume);
        pingvolume=loadedSettings.getVolume();
        seekbar.setProgress(pingvolume);



        adapter = ArrayAdapter.createFromResource(context,
                R.array.extraping_options, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timechoices.setAdapter(adapter);
        String choice=loadedSettings.getExtraPing();
        int position=adapter.getPosition(choice);
        timechoices.setSelection(position);
        string_ringtone_uri=loadedSettings.getRingtoneUri();

        if(loadedSettings.isRingInDND()){
            overridednd.setChecked(true);
            overridednd_bool=true;
        }

        else{
            overridednd.setChecked(false);
            overridednd_bool=false;
        }



        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                        pingvolume=i;
             //   obj_AMC.saveVolume(context,pingvolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        taptotestvolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtones testSound = new ringtones(context,0, string_ringtone_uri);
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int seekbarVolume = seekbar.getProgress();
                am.setStreamVolume(AudioManager.STREAM_MUSIC, seekbarVolume, 0);
                testSound.playShorttune(context);
            }
        });
        timechoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if(!powerManager.isIgnoringBatteryOptimizations(context.getPackageName())){
                    //postPermissionDialog(context);
                    postQuestionDialog();
                }

                        selectedItem = parent.getItemAtPosition(position).toString();
              //  obj_AMC.savetimerChoice(context,selectedItem);
                // Handle the item selection here
                // For example, display a toast message
                // Toast.makeText(context, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected
            }

        });
        overridednd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(overridednd.isChecked()){
                    overridednd_bool=true;
                }
                else{
                    overridednd_bool=false;
                }
               // utilityHelpers.saveBooleanToMemory(context,"AMC_OVERRIDE_DND",overridednd_bool);
            }
        });

        change_ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RingtoneListActivity.class);
                loadRingtoneList.launch(intent);
            }
        });

    }

    private void postQuestionDialog() {
        try {
            //start a dialog box
            AlertDialog.Builder TS_alertbuilder = new AlertDialog.Builder(this,R.style.AlertDialogStyle);
            TS_alertbuilder.setMessage( getString(R.string.bypass_batt_info_dialog)).setPositiveButton("Yes", TSdialogListener).setNegativeButton("No", TSdialogListener);
            AlertDialog alertDialog = TS_alertbuilder.create();
            alertDialog.show();
        } catch (Exception e) {

            Log.e(TAG, " Exception on dialog  " + e);
            Toast.makeText(getApplicationContext(), " Exception on dialog ", Toast.LENGTH_SHORT).show();
        }
    }

    DialogInterface.OnClickListener TSdialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                    startActivity(intent);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    ActivityResultLauncher<Intent> loadRingtoneList = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Here, no request code
                    Intent data = result.getData();
                    ArrayList<String> Stringlist_Uri= new ArrayList<>();
                    Stringlist_Uri=data.getStringArrayListExtra("outputURI");
                    string_ringtone_uri=Stringlist_Uri.get(0);
                }
            });

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

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        class_setting currentSettings=new class_setting(selectedItem,pingvolume,overridednd_bool,string_ringtone_uri);
        ArrayList<class_setting> currentSettings_List=new ArrayList<class_setting>();
        currentSettings_List.add(currentSettings);
        resultIntent.putParcelableArrayListExtra("settingList", currentSettings_List);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

}
