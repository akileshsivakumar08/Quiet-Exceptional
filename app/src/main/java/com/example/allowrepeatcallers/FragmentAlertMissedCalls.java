package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;
import static android.content.Context.AUDIO_SERVICE;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.quietexceptional.R;

import java.util.Arrays;
import java.util.Set;

public class FragmentAlertMissedCalls extends Fragment {
    TextView FeatTitle;
    ImageView settings_AMC;
    SwitchCompat TapToEnable;
    ImageView infoButton;
    Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    final int PERMISSION_CODE_POSTNOTIFICATIONS=1;
    String EnabledColor="#FFD369";
    ImageView pony;
    String DisabledColor="#F79489";
    ImageView share;
    String EnabledText="Tap To Disable";
    String DisabledText="Tap To Enable";
    View leftLine;
    View rightLine;
    SeekBar seekbar;
    feat_AlertMissedCalls obj_AMC;

    TextView taptotestvolume;
    permissionhandler OBJ_Permissions;
    private String settingscolor_enabled="#2F435A";
    private String settingscolor_disabled="#E4E5E8";
    TextView settings_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alertmissedcalls, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        try {
            super.onResume();
            Context context = getContext();
            Log.i(TAG,"onResume AMC");
            if (obj_AMC.isFeatureActivated(context)) {
                Log.i(TAG,"onResume AMC Feat Activated");
                process_featureState(2, context);
            } else {
                Log.i(TAG,"onResume AMC  Permission Granted But Deactivated");
                process_featureState(1, context);
            }
            if (utilityHelpers.ispermissionpending(context, feat_AlertMissedCalls.permissions)) {
                Log.i(TAG,"onResume AMC  Permission Not Granted");
                process_featureState(0, context);
                TapToEnable.setText(R.string.Allfeat_Tap2Permission);
            }
        }
        catch (Exception e) {
            String ErrorFlow="AMC_frag_Error On Resume";
            utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
            Log.e(TAG,"AMC_frag_Error On Resume");
            //Error On Resume
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context=requireContext();

        //Initialize GUI
        try {
            //utilityHelpers.createNotificationChannel(context);
            TapToEnable = (SwitchCompat) getView().findViewById(R.id.TapToEnable);
            taptotestvolume = (TextView) getView().findViewById(R.id.taptotestvolume);
            FeatTitle = (TextView) getView().findViewById(R.id.FeatTitle);
            settings_AMC = (ImageView) getView().findViewById(R.id.settings_AMC);
            settings_text = (TextView) getView().findViewById(R.id.settings_text);
            seekbar = (SeekBar) getView().findViewById(R.id.seekBar);

            leftLine = (View) getView().findViewById(R.id.leftLine);
            rightLine = (View) getView().findViewById(R.id.rightLine);
            utilityHelpers.adjustTitleTextSize(FeatTitle, context,75);
            Animation anima_scaleleft = AnimationUtils.loadAnimation(context, R.anim.scale_fromleft);
            Animation anima_scaleright = AnimationUtils.loadAnimation(context, R.anim.scale_fromright);
            leftLine.startAnimation(anima_scaleleft);
            rightLine.startAnimation(anima_scaleright);
        } catch (Exception e) {
            String ErrorFlow="AMC_frag_Throw GUI INIT Exception";
            Log.e(TAG,"AMC_frag_Throw GUI INIT Exception");
            utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
            //Throw GUI INIT Exception
            throw new RuntimeException(e);
        }
        try{
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        int maxMusicVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekbar.setMax(maxMusicVolume);
        obj_AMC=new feat_AlertMissedCalls(context);
        OBJ_Permissions=new permissionhandler(context);




        // Set the current volume of the SeekBar to the current volume of the MediaPlayer:
        int lastSetMediaVolume=obj_AMC.getPingVolume(context);
        seekbar.setProgress(lastSetMediaVolume);
    } catch (Exception e) {
            String ErrorFlow="AMC_frag_Throw Feature Variables INIT Exception";
            Log.e(TAG,"AMC_frag_Throw Feature Variables INIT Exception");
            utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
        //Throw Feature Variables INIT Exception
        throw new RuntimeException(e);
    }

try {
    if (obj_AMC.isFeatureActivated(context)) {
        process_featureState(2, context);
    } else {
        process_featureState(1, context);
    }
    if (utilityHelpers.ispermissionpending(context, feat_AlertMissedCalls.permissions)) {
        process_featureState(0, context);
        TapToEnable.setText(R.string.Allfeat_Tap2Permission);
    }
}
catch (Exception e) {
    String ErrorFlow="AMC_frag_Throw feature state Exception";
    Log.e(TAG,"AMC_frag_Throw feature state Exception");
    utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
    //Throw feature state Exception
    throw new RuntimeException(e);
}

        taptotestvolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtones testSound = new ringtones(context, 0);
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int seekbarVolume = obj_AMC.getPingVolume(context);
                am.setStreamVolume(AudioManager.STREAM_MUSIC, seekbarVolume, 0);
                testSound.playShorttune(context);
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int pingvolume=i;
                obj_AMC.saveVolume(context,pingvolume);
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

        settings_AMC.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(obj_AMC.isFeatureActivated(context)) {
                    Intent intent = new Intent(context, settings.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(context, R.string.enable_feature_to_access_settings, Toast.LENGTH_SHORT).show();
                }
            }
        });



    }



    private void process_featureState(int state,Context context) {
        try {
            Boolean bool_state = false;
            if (state == 2) {
                bool_state = true;
            }
            obj_AMC.setFeatureActivated(context, bool_state);
            TapToEnable.setChecked(bool_state);
            seekbar.setEnabled(bool_state);
            taptotestvolume.setEnabled(bool_state);
            //settings_AMC.setEnabled(bool_state);
            if (state == 2) {
                adjustInterfaceButton(EnabledColor, getString(R.string.tap_to_disable));
            } else if (state == 1) {
                adjustInterfaceButton(DisabledColor, getString(R.string.Allfeat_Tap2Enable));
            } else if (state == 0) {
                adjustInterfaceButton(DisabledColor, getString(R.string.Allfeat_Tap2Permission));
            }
        }
        catch (Exception e) {
            String ErrorFlow="AMC_frag_Throw feature state Exception";
            Log.e(TAG,"AMC_frag_Throw feature state Exception");
            utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
            //errror processing feature state
            throw new RuntimeException(e);
        }

    }


    private void adjustInterfaceButton(String ipColor, String ipText) {
        TapToEnable.setText(ipText);
        TapToEnable.setTextColor(Color.parseColor(ipColor));
        FeatTitle.setTextColor(Color.parseColor(ipColor));
    }

    private void userTap() {
    try{
        Context context = requireContext();
        if (obj_AMC.isFeatureActivated(context)) {
            process_featureState(1, context);

        } else {
            NotificationManager tap_notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if ((!utilityHelpers.isNotificationServiceEnabled(context)) || (!tap_notificationManager.isNotificationPolicyAccessGranted())) {
                postPermissionDialog(context);
              //  Intent intent = new Intent(context, startup_permissions.class);
              //  startActivity(intent);
            }
            else if ((!utilityHelpers.ispermissionpending(context, feat_AlertMissedCalls.permissions))&&(!utilityHelpers.ispermissionpending(context,feat_AlertMissedCalls.addedPermissions))) {
                process_featureState(2, context);

            }  else {
                String[] pend = utilityHelpers.getpendingpermissions_All(context, feat_AlertMissedCalls.permissions,feat_AlertMissedCalls.addedPermissions);

                Boolean permission_already_requested = OBJ_Permissions.werePermissionsRequested(pend);
                if (permission_already_requested == false) {

                    requestPermissionLauncher.launch(pend);
                } else if (permission_already_requested == true) {
                    try {
                        //start a dialog box
                        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialogStyle);
                        builder.setMessage(R.string.permission_already_requested).setPositiveButton(R.string.continue_menu, dialogClickListener)
                                .setNegativeButton(R.string.cancel_menu, dialogClickListener);
                        builder.setTitle(getString(R.string.permissions_needed_title));
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } catch (Exception e) {
                        String ErrorFlow=" AMC_frag_Exception on dialog  ";
                        utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
                        Log.e(TAG, " Exception on dialog  " + e);
                        Toast.makeText(context, " Exception on dialog ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    catch (Exception e) {
        String ErrorFlow="AMC_frag_Feature Enable User Tap exception";
        Log.e(TAG,"AMC_frag_Feature Enable User Tap exception");
        utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
        //Feature Enable User Tap exception
        throw new RuntimeException(e);
    }
    }

    private void postPermissionDialog(Context context) {

        DialogUtils.showAlertDialog(context,
                getString(R.string.permissions_needed),
                getString(R.string.AMC_PermissionsNeeded),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                        Toast.makeText(context, " Select Quiet Exceptional from the list ", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

    }

    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                Context context =requireContext();
                OBJ_Permissions.setPermissionRequested(context,isGranted);
                if(isGranted.containsValue(false)){
                    process_featureState(0,context);

                }
                else{
                    process_featureState(1,context);
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

}

