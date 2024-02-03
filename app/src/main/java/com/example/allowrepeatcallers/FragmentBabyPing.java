package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;
import static android.content.Context.AUDIO_SERVICE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class FragmentBabyPing extends Fragment {
    TextView FeatTitle;
    ImageView diagnosis;
    Switch TapToEnable;
    ImageView infoButton;
    Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    final int PERMISSION_CODE_POSTNOTIFICATIONS=1;
    String EnabledColor="#2C5E1A";
    ImageView pony;
    String DisabledColor="#72435C";
    ImageView share;
    String EnabledText="Tap To Disable";
    String DisabledText="Tap To Enable";
    SeekBar seekbar;
    feat_BabyPing obj_AMC;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.babyping, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context=requireContext();
        utilityHelpers.createNotificationChannel(context);
        TapToEnable=(Switch) getView().findViewById(R.id.TapToEnable);
        diagnosis=(ImageView) getView().findViewById(R.id.Diagnosis);
        FeatTitle=(TextView) getView().findViewById(R.id.FeatTitle);
        infoButton=(ImageView) getView().findViewById(R.id.infoButton);
        seekbar=(SeekBar) getView().findViewById(R.id.seekBar);
        share=(ImageView)  getView().findViewById(R.id.share);
        utilityHelpers.adjustTitleTextSize(FeatTitle,context);
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        int maxMusicVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekbar.setMax(maxMusicVolume);
        obj_AMC=new feat_BabyPing(context);

        // Set the current volume of the SeekBar to the current volume of the MediaPlayer:
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int lastSetMediaVolume=utilityHelpers.loadIntFromMemory(context,"BABYPINGVOLUME",currVolume);
        seekbar.setProgress(lastSetMediaVolume);
        if(obj_AMC.isFeatureActivated()){
            process_featureState(true,context);
        }
        else{
            process_featureState(false,context);
        }

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage= "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int pingvolume=i;
                utilityHelpers.saveIntToMemory(context,"BABYPINGVOLUME",pingvolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ringtones testSound = new ringtones(context, 0);
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int seekbarVolume = utilityHelpers.loadIntFromMemory(context, "BABYPINGVOLUME", testSound.getCurrent_MediaVolume());
                am.setStreamVolume(AudioManager.STREAM_MUSIC, seekbarVolume, 0);
                testSound.playShorttune(context);

            }
        });
        diagnosis.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, settings.class);
                FragmentBabyPing.this.startActivity(intent);
            }
        });
        FeatTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userTap();

            }
        });
        TapToEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userTap();
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postInfoDialog();
            }
        });



    }

    private void process_featureState(boolean state,Context context) {
        obj_AMC.setFeatureActivated(state);
        utilityHelpers.saveBooleanToMemory(context,"IS_BABYPING_ACTIVATED",state);
        TapToEnable.setChecked(state);
        seekbar.setEnabled(state);
        if(state){
            adjustInterfaceButton(EnabledColor,EnabledText);
        }
        else{
            adjustInterfaceButton(DisabledColor,DisabledText);
        }

    }


    private void postInfoDialog() {
        Context context=requireContext();
        try {
            //start a dialog box
            AlertDialog.Builder noti_alertbuilder = new AlertDialog.Builder(context);
            noti_alertbuilder.setMessage("Device makes a short sound after a missed call");
            AlertDialog alertDialog = noti_alertbuilder.create();
            alertDialog.show();
        } catch (Exception e) {

            Log.e(TAG, " Exception on dialog  " + e);
            Toast.makeText(getContext(), " Exception on dialog ", Toast.LENGTH_SHORT).show();
        }
    }

    private void adjustInterfaceButton(String ipColor, String ipText) {
        TapToEnable.setText(ipText);
        TapToEnable.setTextColor(Color.parseColor(ipColor));
        FeatTitle.setTextColor(Color.parseColor(ipColor));
    }

    private void userTap() {
        Context context=requireContext();
        if(obj_AMC.isFeatureActivated()){
            process_featureState(false,context);

        }
        else{
                if (!utilityHelpers.ispermissionpending(context, feat_BabyPing.permissions)) {
                    process_featureState(true,context);
                    utilityHelpers.checkLogFormat(context);
                    Toast.makeText(context, " Feature Enable saved ", Toast.LENGTH_SHORT).show();

                } else {
                    boolean ARC_permission_already_requested= utilityHelpers.loadBooleanFromMemory(context, "REPEATCALLER_PERMISSIONREQUESTED");
                    boolean BP_permission_already_requested=utilityHelpers.loadBooleanFromMemory(context, "BABY_PING_PERMISSIONREQUESTED");
                    boolean SILEXCEPT_permission_already_requested= utilityHelpers.loadBooleanFromMemory(context, "SILEXCEPT_PERMISSIONREQUESTED");
                    boolean permission_already_requested=((SILEXCEPT_permission_already_requested)||(ARC_permission_already_requested)||(BP_permission_already_requested));
                    if (permission_already_requested == false) {
                        String[] pend = utilityHelpers.getpendingpermissions(context, feat_BabyPing.permissions);
                        //requestPermissions(pend, PERMISSION_CODE_POSTNOTIFICATIONS);
                        requestPermissionLauncher.launch(pend);
                    } else if (permission_already_requested == true) {
                        try {
                            //start a dialog box
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("To use this app permisions are needed to read call logs and detect incoming calls. Press continue to provide these in the app settings menu").setPositiveButton("continue", dialogClickListener)
                                    .setNegativeButton("cancel", dialogClickListener);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } catch (Exception e) {
                            Log.e(TAG, " Exception on dialog  " + e);
                            Toast.makeText(context, " Exception on dialog ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }



        }
    }

    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                Context context =requireContext();
                boolean permission_already_requested=false;
                permission_already_requested=true;
                utilityHelpers.saveBooleanToMemory(context,"BABY_PING_PERMISSIONREQUESTED",permission_already_requested);
                if(isGranted.containsValue(false)){
                    process_featureState(false,context);

                }
                else{
                    process_featureState(true,context);
                    utilityHelpers.checkLogFormat(context);
                }
            });


    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Context context=requireContext();
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));

                    startActivity(intent);
                    //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    TapToEnable.setChecked(false);
                    //No button clicked
                    //smsReceiver.DND_OverridePermission=false;
                    break;
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Context context =requireContext();
        switch (requestCode) {
            case PERMISSION_CODE_POSTNOTIFICATIONS:
                for(int i =0;i<permissions.length;i++){
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    } else {
                        // Permission Denied
                        Toast.makeText(context, "denied", Toast.LENGTH_SHORT).show();
                        process_featureState(false,context);
                        feat_BabyPing.permission_already_requested=true;
                        utilityHelpers.saveBooleanToMemory(context,"BABY_PING_PERMISSIONREQUESTED",feat_BabyPing.permission_already_requested);

                        break;

                    }
                    process_featureState(true,context);
                    feat_BabyPing.permission_already_requested=true;
                    utilityHelpers.saveBooleanToMemory(context,"BABY_PING_PERMISSIONREQUESTED",feat_BabyPing.permission_already_requested);


                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}

