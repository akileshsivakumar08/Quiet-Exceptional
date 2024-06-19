package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;
import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.quietexceptional.R;


public class FragmentAllowRepeatCallers extends Fragment {
    TextView FeatTitle;
    private static final String CHANNEL_ID = "Missed Call Notification";
    SwitchCompat TapToEnable;
    String EnabledColor="#FFD369";
    ImageView share;
    String DisabledColor="#72435C";
    String EnabledText="Tap To Disable";
    String DisabledText="Tap To Enable";
    TextView taptotestvolume;
    SeekBar seekbar_ARC;
    feat_AllowRepeatCallers GUIobj_RepeatCaller;
    int pingvolume;
    ImageView arrow1;
    ImageView arrow2;
    float animationDistance=20f;
    permissionhandler OBJ_Permissions;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.allowrepeatcallers, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context=requireContext();
        utilityHelpers.createNotificationChannel(context);
        //load GUI Elements
        TapToEnable=(SwitchCompat) getView().findViewById(R.id.TapToEnable);

        FeatTitle=(TextView) getView().findViewById(R.id.FeatTitle);
        taptotestvolume=(TextView) getView().findViewById(R.id.taptotestvolume);
        arrow1=(ImageView) getView().findViewById(R.id.arrow1);
        arrow2=(ImageView) getView().findViewById(R.id.arrow2);
        seekbar_ARC=(SeekBar) getView().findViewById(R.id.seekBar_ARC);

        animateDiagonalPan(arrow1,(animationDistance*-1));
        animateDiagonalPan(arrow2,animationDistance);
        animateScalePan(FeatTitle,1.01f);
        //load managers
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        GUIobj_RepeatCaller=new feat_AllowRepeatCallers(context);
        OBJ_Permissions=new permissionhandler(context);
        //load from memory
        int lastSetMediaVolume=GUIobj_RepeatCaller.getPingVolume(context);


        //initiate GUI Elements

        int maxMusicVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekbar_ARC.setMax(maxMusicVolume);
        seekbar_ARC.setProgress(lastSetMediaVolume);
        utilityHelpers.adjustTitleTextSize(FeatTitle,context,75);
        //utilityHelpers.adjustTitleTextSize(FeatTitle2,context);


        if (utilityHelpers.ispermissionpending(context, feat_AllowRepeatCallers.permissions)) {
            process_featureState(false,context);
            TapToEnable.setText(R.string.Allfeat_Tap2Permission);
        }

        taptotestvolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtones testSound = new ringtones(context, 0);
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int seekbarVolume = GUIobj_RepeatCaller.getPingVolume(context);
                am.setStreamVolume(AudioManager.STREAM_MUSIC, seekbarVolume, 0);
                testSound.playShorttune(context);
            }
        });

        seekbar_ARC.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pingvolume=i;
                GUIobj_RepeatCaller.saveVolume(context,pingvolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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



        if(GUIobj_RepeatCaller.isFeatureActivated()){
            //checkLogFormat();
            process_featureState(true,context);
        }
        else{
            process_featureState(false,context);
        }




    }


    private void Dialog_requestDND() {
        NotificationManager policy_notificationManager =
                (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
        if (!policy_notificationManager.isNotificationPolicyAccessGranted()) {
            try {
                //start a dialog box
                AlertDialog.Builder noti_alertbuilder = new AlertDialog.Builder(requireContext());
                noti_alertbuilder.setMessage(R.string.provide_dnd_permissions).setPositiveButton(R.string.continue_menu, noti_alert_dialogClickListener)
                        .setNegativeButton(R.string.cancel_menu, noti_alert_dialogClickListener);
                AlertDialog alertDialog = noti_alertbuilder.create();
                alertDialog.show();
            } catch (Exception e) {

               // Log.e(TAG, " Exception on dialog  " + e);
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
        if(GUIobj_RepeatCaller.isFeatureActivated()){
            GUIobj_RepeatCaller.clearAndSaveMissedList(context);
            GUIobj_RepeatCaller.clearNotifications(tap_notificationManager);

            process_featureState(false,context);
        }
        else{

            if (!tap_notificationManager.isNotificationPolicyAccessGranted()) {

                process_featureState(false,context);
                TapToEnable.setText(R.string.Allfeat_Tap2Permission);
                Dialog_requestDND();
            } else {
                if (!utilityHelpers.ispermissionpending(context, GUIobj_RepeatCaller.permissions)) {

                    utilityHelpers.checkLogFormat(context);
                    process_featureState(true,context);

                } else {
                    String[] pend = utilityHelpers.getpendingpermissions(context, feat_AllowRepeatCallers.permissions);
                    Boolean permission_already_requested=OBJ_Permissions.werePermissionsRequested(pend);
                    if (permission_already_requested == false) {
                        requestPermissionLauncher.launch(pend);
                    } else if (permission_already_requested == true) {
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
            builder.setMessage(R.string.permission_already_requested).setPositiveButton(R.string.continue_menu, dialogClickListener)
                    .setNegativeButton(R.string.cancel_menu, dialogClickListener);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            Log.e(TAG, " Exception on dialog  " + e);
        }
    }

    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                Context context =requireContext();
                OBJ_Permissions.setPermissionRequested(context,isGranted);
                if(isGranted.containsValue(false)){
                        process_featureState(false,context);

                    }
                    else{
                        process_featureState(true,context);
                        utilityHelpers.checkLogFormat(context);
                    }
            });


    private void process_featureState(boolean state,Context context) {
        GUIobj_RepeatCaller.setFeatureActivated(context,state);
        TapToEnable.setChecked(state);
        seekbar_ARC.setEnabled(state);
        taptotestvolume.setEnabled(state);
        if(state){
            adjustInterfaceButton(EnabledColor,getString(R.string.tap_to_disable));
        }
        else{
            adjustInterfaceButton(DisabledColor,getString(R.string.Allfeat_Tap2Enable));
        }

    }

    private void animateDiagonalPan(View v,float distance) {
        AnimatorSet animSetXY = new AnimatorSet();

        float targetY = distance;
        float targetX=distance;
        ObjectAnimator y1 = ObjectAnimator.ofFloat(v,
                "translationY",v.getY(), targetY);
        y1.setRepeatCount(ValueAnimator.INFINITE);
        y1.setRepeatMode(ValueAnimator.REVERSE);
        ObjectAnimator x1 = ObjectAnimator.ofFloat(v,
                "translationX", v.getX(), targetX);

        x1.setRepeatCount(ValueAnimator.INFINITE);
        x1.setRepeatMode(ValueAnimator.REVERSE);
        animSetXY.playTogether(x1, y1);
        animSetXY.setInterpolator(new LinearInterpolator());
        animSetXY.setDuration(1800);
        animSetXY.start();

    }

    private void animateScalePan(View v,float scale) {
        AnimatorSet animSetXY = new AnimatorSet();

        ObjectAnimator scaleanimY = ObjectAnimator.ofFloat(v,
                "ScaleY",1f, scale);
        scaleanimY.setRepeatCount(ValueAnimator.INFINITE);
        scaleanimY.setRepeatMode(ValueAnimator.REVERSE);
        ObjectAnimator scaleanimX = ObjectAnimator.ofFloat(v,
                "ScaleX", 1f, scale);

        scaleanimX.setRepeatCount(ValueAnimator.INFINITE);
        scaleanimX.setRepeatMode(ValueAnimator.REVERSE);
        animSetXY.playTogether(scaleanimX, scaleanimY);
        animSetXY.setInterpolator(new LinearInterpolator());
        animSetXY.setDuration(1500);
        animSetXY.start();

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



}

