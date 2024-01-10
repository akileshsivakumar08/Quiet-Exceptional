package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;
import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class FragmentAllowRepeatCallers extends Fragment {
    TextView FeatTitle;
    TextView FeatTitle2;
    private static final String CHANNEL_ID = "Missed Call Notification";
    ImageView diagnosis;
    Button firstMissedCall;
    Switch TapToEnable;
    ImageView infoButton;
    Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    final int PERMISSION_CODE_POSTNOTIFICATIONS=1;
    String EnabledColor="#2C5E1A";
    ImageView share;
    String DisabledColor="#72435C";
    String EnabledText="Tap To Disable";
    String DisabledText="Tap To Enable";
    SeekBar seekbar_ARC;
    Switch overridednd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.allowrepeatcallers, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context=requireContext();
        utilityHelpers.createNotificationChannel(context);
        //load GUI Elements
        TapToEnable=(Switch) getView().findViewById(R.id.TapToEnable);
        diagnosis=(ImageView) getView().findViewById(R.id.Diagnosis);
        FeatTitle=(TextView) getView().findViewById(R.id.FeatTitle);
        infoButton=(ImageView) getView().findViewById(R.id.infoButton);
        share=(ImageView)  getView().findViewById(R.id.share);
        FeatTitle2=(TextView) getView().findViewById(R.id.FeatTitle2);
        seekbar_ARC=(SeekBar) getView().findViewById(R.id.seekBar_ARC);


        //load managers
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);

        //load from memory
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        feat_RepeatCaller.featureActivated=utilityHelpers.loadBooleanFromMemory(context,"FEAT_REPEATCALLER_ACTIVE");
        int lastSetMediaVolume=utilityHelpers.loadIntFromMemory(context,"ARC_VOLUME",currVolume);


        //initiate GUI Elements

        int maxMusicVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekbar_ARC.setMax(maxMusicVolume);
        seekbar_ARC.setProgress(lastSetMediaVolume);
        utilityHelpers.adjustTitleTextSize(FeatTitle,context);
        utilityHelpers.adjustTitleTextSize(FeatTitle2,context);







        seekbar_ARC.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int pingvolume=i;
                utilityHelpers.saveIntToMemory(context,"ARC_VOLUME",pingvolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        diagnosis.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, settings.class);
                FragmentAllowRepeatCallers.this.startActivity(intent);
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


        if(feat_RepeatCaller.featureActivated){
            //checkLogFormat();
            process_featureState(true,context);
        }
        else{
            process_featureState(false,context);
        }




    }


    private void postInfoDialog() {
        Context context=requireContext();
        try {
            //start a dialog box
            AlertDialog.Builder noti_alertbuilder = new AlertDialog.Builder(context);
            noti_alertbuilder.setMessage("Device Rings on Successive calls, if a call was missed in Silent or Vibrate Mode.\n\n\nNotifications can be dismissed to disable the ringing.");
            AlertDialog alertDialog = noti_alertbuilder.create();
            alertDialog.show();
        } catch (Exception e) {

            Log.e(TAG, " Exception on dialog  " + e);
            Toast.makeText(getContext(), " Exception on dialog ", Toast.LENGTH_SHORT).show();
        }
    }

    private void Dialog_requestDND() {
        NotificationManager policy_notificationManager =
                (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
        if (!policy_notificationManager.isNotificationPolicyAccessGranted()) {
            try {
                //start a dialog box
                AlertDialog.Builder noti_alertbuilder = new AlertDialog.Builder(requireContext());
                noti_alertbuilder.setMessage("To use this app DND permisions are needed . Press continue to provide these in the app settings menu").setPositiveButton("continue", noti_alert_dialogClickListener)
                        .setNegativeButton("cancel", noti_alert_dialogClickListener);
                AlertDialog alertDialog = noti_alertbuilder.create();
                alertDialog.show();
            } catch (Exception e) {

               // Log.e(TAG, " Exception on dialog  " + e);
                Toast.makeText(requireContext(), " Exception on dialog ", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void adjustInterfaceButton(String ipColor, String ipText) {
        TapToEnable.setText(ipText);
        TapToEnable.setTextColor(Color.parseColor(ipColor));
        FeatTitle.setTextColor(Color.parseColor(ipColor));
    }

    private void userTap() {
        Context context=requireContext();
        NotificationManager tap_notificationManager =
                (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(feat_RepeatCaller.featureActivated){
            feat_RepeatCaller.missedList.clear();
            feat_RepeatCaller.notificationIDcounter=0;
            tap_notificationManager.cancelAll();
            utilityHelpers.saveStringSetToMemory(context,"STRINGSET_MISSEDLIST",feat_RepeatCaller.missedList);

            process_featureState(false,context);
        }
        else{

            if (!tap_notificationManager.isNotificationPolicyAccessGranted()) {

                process_featureState(false,context);
                Dialog_requestDND();
            } else {
                if (!utilityHelpers.ispermissionpending(context, feat_RepeatCaller.permissions)) {

                    utilityHelpers.checkLogFormat(context);
                    process_featureState(true,context);

                } else {
                    boolean ARC_permission_already_requested= utilityHelpers.loadBooleanFromMemory(context, "REPEATCALLER_PERMISSIONREQUESTED");
                    boolean BP_permission_already_requested=utilityHelpers.loadBooleanFromMemory(context, "BABY_PING_PERMISSIONREQUESTED");
                    boolean permission_already_requested=((ARC_permission_already_requested)||(BP_permission_already_requested));
                    if (permission_already_requested == false) {
                        String[] pend = utilityHelpers.getpendingpermissions(context, feat_RepeatCaller.permissions);
                        requestPermissions(pend, PERMISSION_CODE_POSTNOTIFICATIONS);
                    } else if (permission_already_requested == true) {
                        try {
                            //start a dialog box
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("To use this app permisions are needed to read call logs,post notifications and detect incoming calls. Press continue to provide these in the app settings menu").setPositiveButton("continue", dialogClickListener)
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
    }

    private void process_featureState(boolean state,Context context) {
        feat_RepeatCaller.featureActivated = state;
        utilityHelpers.saveBooleanToMemory(context,"FEAT_REPEATCALLER_ACTIVE",feat_RepeatCaller.featureActivated);
        TapToEnable.setChecked(state);
        seekbar_ARC.setEnabled(state);
        if(state){
            adjustInterfaceButton(EnabledColor,EnabledText);
        }
        else{
            adjustInterfaceButton(DisabledColor,DisabledText);
        }

    }


    DialogInterface.OnClickListener noti_alert_dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Context context=requireContext();
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = new Intent(
                            android.provider.Settings
                                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                    startActivity(intent);
                    //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    process_featureState(false,context);
                    //No button clicked
                    //smsReceiver.DND_OverridePermission=false;
                    break;
            }
        }
    };
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Context context=requireContext();
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(android.net.Uri.parse("package:" + context.getPackageName()));

                    startActivity(intent);
                    //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    process_featureState(false,context);
                    //No button clicked
                    //smsReceiver.DND_OverridePermission=false;
                    break;
            }
        }
    };



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Context context =requireContext();
        boolean permission_already_requested=false;
        switch (requestCode) {
            case PERMISSION_CODE_POSTNOTIFICATIONS:
                for(int i =0;i<permissions.length;i++){
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    } else {
                        // Permission Denied
                        process_featureState(false,context);
                        permission_already_requested=true;
                        utilityHelpers.saveBooleanToMemory(context,"REPEATCALLER_PERMISSIONREQUESTED",permission_already_requested);

                        break;

                    }
                    process_featureState(true,context);
                    permission_already_requested=true;
                    utilityHelpers.saveBooleanToMemory(context,"REPEATCALLER_PERMISSIONREQUESTED",permission_already_requested);


                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}

