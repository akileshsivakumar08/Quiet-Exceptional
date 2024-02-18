package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;
import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class FragmentsilentExceptions extends Fragment {
    TextView FeatTitle;
    private static final String CHANNEL_ID = "Missed Call Notification";
    ImageView diagnosis;
    SwitchCompat TapToEnable;
    Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    final int PERMISSION_CODE_POSTNOTIFICATIONS=1;
    String EnabledColor="#064663";
    ImageView share;
    String DisabledColor="#72435C";
    String EnabledText="Tap To Disable";
    String DisabledText="Tap To Enable";
    int blinknumber=3;
    SeekBar seekBar_silExcept;
    Button manageContacts;
    feat_silentExceptions GUIobj_silentExceptions;
    ImageView starimage;
    int pingvolume;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.exceptionally, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Context context=getContext();
        starimage=(ImageView) getView().findViewById(R.id.starimage);
        Animation starrotate = AnimationUtils.loadAnimation(context, R.anim.starrotate);
        starimage.startAnimation(starrotate);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context=requireContext();
        utilityHelpers.createNotificationChannel(context);
        //load GUI Elements
        FeatTitle=(TextView) getView().findViewById(R.id.FeatTitle);
        TapToEnable=(SwitchCompat) getView().findViewById(R.id.TapToEnable);

        seekBar_silExcept=(SeekBar) getView().findViewById(R.id.seekBar_silExcept);
        manageContacts=(Button) getView().findViewById(R.id.manageContacts);

        //load managers
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        GUIobj_silentExceptions=new feat_silentExceptions(context);
        //load from memory
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int lastSetMediaVolume=GUIobj_silentExceptions.getExceptionallyVolume(context);


        //initiate GUI Elements

        int maxMusicVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekBar_silExcept.setMax(maxMusicVolume);
        seekBar_silExcept.setProgress(lastSetMediaVolume);
        utilityHelpers.adjustTitleTextSize(FeatTitle,context);

        if (utilityHelpers.ispermissionpending(context, feat_silentExceptions.permissions)) {
            process_featureState(false,context);
        }

        seekBar_silExcept.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pingvolume=i;
                GUIobj_silentExceptions.saveVolume(context,pingvolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ringtones testSound = new ringtones(context, 0);
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int seekbarVolume = GUIobj_silentExceptions.getExceptionallyVolume(context);
                am.setStreamVolume(AudioManager.STREAM_MUSIC, seekbarVolume, 0);
                testSound.playShorttune(context);
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

        manageContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(context, LoadContactsActivity.class);
                    startActivity(intent);
            }
        });

        //checkLogFormat();
        process_featureState(GUIobj_silentExceptions.isFeatureActivated(context),context);




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
        manageContacts.setBackgroundColor(Color.parseColor(ipColor));
    }

    private void userTap() {
        Context context=requireContext();
        NotificationManager tap_notificationManager =
                (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(GUIobj_silentExceptions.isFeatureActivated(context)){
            process_featureState(false,context);
        }
        else{

            if (!tap_notificationManager.isNotificationPolicyAccessGranted()) {

                process_featureState(false,context);
                Dialog_requestDND();
            } else {
                if (!utilityHelpers.ispermissionpending(context, feat_silentExceptions.permissions)) {

                    process_featureState(true,context);

                } else {
                    boolean SILEXCEPT_permission_already_requested= utilityHelpers.loadBooleanFromMemory(context, "SILEXCEPT_PERMISSIONREQUESTED");
                    boolean ARC_permission_already_requested=utilityHelpers.loadBooleanFromMemory(context, "ARC_PERMISSIONREQUESTED");
                    boolean BP_permission_already_requested=utilityHelpers.loadBooleanFromMemory(context, "BABY_PING_PERMISSIONREQUESTED");
                    boolean permission_already_requested=((SILEXCEPT_permission_already_requested)||(ARC_permission_already_requested)||(BP_permission_already_requested));
                    if (!permission_already_requested) {
                        String[] pend = utilityHelpers.getpendingpermissions(context, feat_silentExceptions.permissions);
                        requestPermissionLauncher.launch(pend);
                    } else if (permission_already_requested) {
                        permissionAlreadyRequested_RequestDialog(context);

                    }
                }

            }
        }
    }

    private void permissionAlreadyRequested_RequestDialog(Context context) {
        try {
            //start a dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("To use this app permisions are needed to read Contacts and detect incoming calls. Press continue to provide these in the app settings menu").setPositiveButton("continue", dialogClickListener)
                    .setNegativeButton("cancel", dialogClickListener);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            Log.e(TAG, " Exception on dialog  " + e);
            Toast.makeText(context, " Exception on dialog ", Toast.LENGTH_SHORT).show();
        }
    }

    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                Context context =requireContext();
                boolean permission_already_requested=false;
                permission_already_requested=true;
                utilityHelpers.saveBooleanToMemory(context,"SILEXCEPT_PERMISSIONREQUESTED",permission_already_requested);
                    if(isGranted.containsValue(false)){
                        process_featureState(false,context);

                    }
                    else{
                        process_featureState(true,context);
                    }
            });


    private void process_featureState(boolean state,Context context) {
        GUIobj_silentExceptions.setFeatureActivated(context,state);
        TapToEnable.setChecked(state);
        seekBar_silExcept.setEnabled(state);
        manageContacts.setEnabled(state);

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
                            Settings
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
                    intent.setData(Uri.parse("package:" + context.getPackageName()));

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Context context =requireContext();
        boolean permission_already_requested=false;
        if (requestCode == PERMISSION_CODE_POSTNOTIFICATIONS) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // Permission Denied
                    process_featureState(false, context);
                    permission_already_requested = true;
                    utilityHelpers.saveBooleanToMemory(context, "REPEATCALLER_PERMISSIONREQUESTED", permission_already_requested);

                    break;

                }
                process_featureState(true, context);
                permission_already_requested = true;
                utilityHelpers.saveBooleanToMemory(context, "REPEATCALLER_PERMISSIONREQUESTED", permission_already_requested);
                utilityHelpers.checkLogFormat(context);

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}

