package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


public class FragmentSMSAlarm extends Fragment {
    TextView FeatTitle;
    private static final String CHANNEL_ID = "Missed Call Notification";
    ImageView diagnosis;
    TextView TapToEnable;
    Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    final int PERMISSION_CODE_POSTNOTIFICATIONS=1;
    String EnabledColor="#064663";
    ImageView smsimage;
    String DisabledColor="#72435C";
    String EnabledText="Tap To Disable";
    String DisabledText="Tap To Enable";
    ImageView manageContacts;
    TextView taptotestvolume;
    feat_SMSAlarm GUIobj_SA;
    int pingvolume;
    permissionhandler OBJ_Permissions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.smsalarm, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context=requireContext();
        utilityHelpers.createNotificationChannel(context);
        //load GUI Elements
        FeatTitle=(TextView) getView().findViewById(R.id.FeatTitle);
        TapToEnable=(TextView) getView().findViewById(R.id.TapToEnable);
        manageContacts=(ImageView) getView().findViewById(R.id.manageContacts);
        smsimage=(ImageView) getView().findViewById(R.id.smsimage);
        //load managers
        GUIobj_SA =new feat_SMSAlarm(context);
        OBJ_Permissions=new permissionhandler(context);
        //load from memory

        //initiate GUI Elements
        animateScalePan(smsimage,1.1f);
        utilityHelpers.adjustTitleTextSize(FeatTitle,context);

        if (utilityHelpers.ispermissionpending(context, feat_SMSAlarm.permissions)) {
            process_featureState(false,context);
        }



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

                intent.putParcelableArrayListExtra("List_Parcel",GUIobj_SA.getSilExceptList());
                LoadContactsActivityResultLauncher.launch(intent);
            }
        });

        //checkLogFormat();
        process_featureState(GUIobj_SA.isFeatureActivated(context),context);

        FeatTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(GUIobj_SA.isFeatureActivated(context)){
                    if(!utilityHelpers.ispermissionpending(context,feat_SMSAlarm.permissions)) {
                        GUIobj_SA.sendSMS(context);
                    }
                    else{
                        Toast.makeText(context, R.string.missing_permissions, Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

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
        if(!GUIobj_SA.isFeatureActivated(context)){

            if (!tap_notificationManager.isNotificationPolicyAccessGranted()) {

                process_featureState(false,context);
                Dialog_requestDND();
            } else {
                if (!utilityHelpers.ispermissionpending(context, feat_SMSAlarm.permissions)) {

                    process_featureState(true,context);

                } else {
                    String[] pend = utilityHelpers.getpendingpermissions(context, feat_SMSAlarm.permissions);
                    Boolean permission_already_requested=OBJ_Permissions.werePermissionsRequested(pend);
                    if (!permission_already_requested) {
                        requestPermissionLauncher.launch(pend);
                    } else if (permission_already_requested) {
                        permissionAlreadyRequested_RequestDialog(context);
                    }
                }

            }
        }
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
        animSetXY.setDuration(900);
        animSetXY.start();

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
                OBJ_Permissions.setPermissionRequested(context,isGranted);
                if(isGranted.containsValue(false)){
                        process_featureState(false,context);

                    }
                    else{
                        process_featureState(true,context);
                    }
            });

    ActivityResultLauncher<Intent> LoadContactsActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Here, no request code
                    Intent data = result.getData();
                    GUIobj_SA.setSMSAlarmList(getContext(), data.getParcelableArrayListExtra("outputList"));
                }
            });
    private void process_featureState(boolean state,Context context) {
        GUIobj_SA.setFeatureActivated(context,state);

        manageContacts.setEnabled(state);
        if(state){
            adjustInterfaceButton(EnabledColor,EnabledText);
            TapToEnable.setText("Long Press to Send Emergency");
        }
        else{
            adjustInterfaceButton(DisabledColor,DisabledText);
            TapToEnable.setText("Tap to grant Permissions");
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




}

