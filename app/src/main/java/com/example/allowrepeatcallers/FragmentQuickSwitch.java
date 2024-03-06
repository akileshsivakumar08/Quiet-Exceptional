package com.example.allowrepeatcallers;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class FragmentQuickSwitch extends Fragment {
    SwitchCompat TapToEnable;
    TextView FeatTitle;
    ImageView share;
    String EnabledColor="#1A4314";
    String DisabledColor="#72435C";
    String RingerDisabledColor="#E4E5E8";
    String RingerEnabledColor="#F79489";
    ImageView diagnosis;
    ImageView infoButton;
    String EnabledText="Tap To Disable";
    String DisabledText="Tap To Enable";
    ImageView imgdnd;
    ImageView imgbell;
    ImageView imgvibrate;
    feat_QuickSwitch obj_quickswitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quickswitch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context=requireContext();
        TapToEnable=(SwitchCompat) getView().findViewById(R.id.TapToEnable);
        imgbell=(ImageView) getView().findViewById(R.id.imgbell);
        imgdnd=(ImageView)getView().findViewById(R.id.imgdnd);
        imgvibrate=(ImageView) getView().findViewById(R.id.imgvibrate);
        FeatTitle=(TextView) getView().findViewById(R.id.FeatTitle);

        obj_quickswitch=new feat_QuickSwitch(context);

        utilityHelpers.adjustTitleTextSize(FeatTitle,context);
        int softwaretype=utilityHelpers.loadIntFromMemory(context,"SOFTWARETYPE",0);
        if(softwaretype==1) {

            checkAccessibilityPermission(context);
            if (obj_quickswitch.isFeatureActivated(context)) {
                TapToEnable.setChecked(true);
                adjustInterfaceButton(EnabledColor, EnabledText);
            } else {
                TapToEnable.setChecked(false);
                adjustInterfaceButton(DisabledColor, DisabledText);
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

        }
        else{
            FeatTitle.setText("PRO\nDEMO");
            adjustInterfaceButton(DisabledColor,DisabledText);
        }

        BroadcastReceiver receiver=new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int mode=am.getRingerMode();
                if(mode==AudioManager.RINGER_MODE_NORMAL){
                    imgbell.setColorFilter(Color.parseColor(RingerEnabledColor));
                    imgdnd.setColorFilter(Color.parseColor(RingerDisabledColor));
                    imgvibrate.setColorFilter(Color.parseColor(RingerDisabledColor));
                }
                else if(mode==(AudioManager.RINGER_MODE_VIBRATE)){
                    imgbell.setColorFilter(Color.parseColor(RingerDisabledColor));
                    imgdnd.setColorFilter(Color.parseColor(RingerDisabledColor));
                    imgvibrate.setColorFilter(Color.parseColor(RingerEnabledColor));
                }
                else if(mode==(AudioManager.RINGER_MODE_SILENT)){
                    imgbell.setColorFilter(Color.parseColor(RingerDisabledColor));
                    imgdnd.setColorFilter(Color.parseColor(RingerEnabledColor));
                    imgvibrate.setColorFilter(Color.parseColor(RingerDisabledColor));
                }

            }
        };
        IntentFilter filter=new IntentFilter(
                AudioManager.RINGER_MODE_CHANGED_ACTION);
       context.registerReceiver(receiver,filter);


    }

    private void checkAccessibilityPermission(Context context) {
        int accessibilityEnabled;
        NotificationManager notificationManager =
                (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            TapToEnable.setChecked(false);
            adjustInterfaceButton(DisabledColor,DisabledText);
            obj_quickswitch.setFeatureActivated(context,false);
        }
        else {
            try {
                accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
            } catch (Settings.SettingNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (accessibilityEnabled != 0) {
                obj_quickswitch.setFeatureActivated(context,true);
                TapToEnable.setChecked(true);
                adjustInterfaceButton(EnabledColor, EnabledText);
            } else {
                TapToEnable.setChecked(false);
                adjustInterfaceButton(DisabledColor, DisabledText);
                obj_quickswitch.setFeatureActivated(context,false);
                //start a dialog box
            }
        }
    }


    private void userTap() {
        int accessibilityEnabled;
        NotificationManager tap_notificationManager =
                (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Context context=requireContext();
        if(obj_quickswitch.isFeatureActivated(context)){
            obj_quickswitch.setFeatureActivated(context,false);

            Toast.makeText(context, " Feature disable saved ", Toast.LENGTH_SHORT).show();
            TapToEnable.setChecked(false);
            adjustInterfaceButton(DisabledColor,DisabledText);
        }
        else{

            if (!tap_notificationManager.isNotificationPolicyAccessGranted()) {
                TapToEnable.setChecked(false);
                adjustInterfaceButton(DisabledColor,DisabledText);
                obj_quickswitch.setFeatureActivated(context,false);
                Dialog_requestDND();
            } else {
                processAccessibilityPermission(context);

            }
        }

    }

    private void processAccessibilityPermission(Context context) {
        int accessibilityEnabled;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (accessibilityEnabled!=0) {
            obj_quickswitch.setFeatureActivated(context,true);
            Toast.makeText(context, " Feature Enable saved ", Toast.LENGTH_SHORT).show();
            TapToEnable.setChecked(true);
            adjustInterfaceButton(EnabledColor,EnabledText);
        } else {
            TapToEnable.setChecked(false);
            adjustInterfaceButton(DisabledColor,DisabledText);
            obj_quickswitch.setFeatureActivated(context,false);
            Dialog_requestACCESSIBILITY(context);
            //start a dialog box
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

    DialogInterface.OnClickListener noti_alert_dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = new Intent(
                            android.provider.Settings
                                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                    startActivity(intent);
                    //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    TapToEnable.setChecked(false);
                    adjustInterfaceButton(DisabledColor,DisabledText);
                    //No button clicked
                    //smsReceiver.DND_OverridePermission=false;
                    break;
            }
        }
    };

    DialogInterface.OnClickListener requestACCESSIBILITY_dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    TapToEnable.setChecked(false);
                    adjustInterfaceButton(DisabledColor,DisabledText);
                    //No button clicked
                    //smsReceiver.DND_OverridePermission=false;
                    break;
            }
        }
    };

    private void Dialog_requestACCESSIBILITY(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(obj_quickswitch.getRequestAccessibility()).setPositiveButton("continue", requestACCESSIBILITY_dialogClickListener)
                .setNegativeButton("cancel", requestACCESSIBILITY_dialogClickListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    private void adjustInterfaceButton(String ipColor, String ipText) {
        TapToEnable.setText(ipText);
        TapToEnable.setTextColor(Color.parseColor(ipColor));
        FeatTitle.setTextColor(Color.parseColor(ipColor));
    }





}
