package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;
import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Activity;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.quietexceptional.R;


public class FragmentPrioContacts extends Fragment {
    TextView FeatTitle;
    private static final String CHANNEL_ID = "Missed Call Notification";
    ImageView diagnosis;
    SwitchCompat TapToEnable;
    Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    final int PERMISSION_CODE_POSTNOTIFICATIONS=1;

    SeekBar seekBar_PrioContacts;
    ImageView manageContacts;
    TextView taptotestvolume;
    feat_PrioContacts GUIobj_PrioContacts;
    ImageView starimage;
    int pingvolume;
    ImageView share;
    permissionhandler OBJ_Permissions;
    private TextView manageContacts_text;
    private String settingscolor_enabled="#2F435A";
    private String settingscolor_disabled="#E4E5E8";
   // String EnabledColor ;
    //String DisabledColor=(String.valueOf(R.color.orange));
    String EnabledColor ="#FFD369";
    String DisabledColor="#F79489";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.priocontacts, container, false);
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
        //utilityHelpers.createNotificationChannel(context);
        //load GUI Elements
        FeatTitle=(TextView) getView().findViewById(R.id.FeatTitle);
        TapToEnable=(SwitchCompat) getView().findViewById(R.id.TapToEnable);
        taptotestvolume=(TextView) getView().findViewById(R.id.taptotestvolume);
        seekBar_PrioContacts =(SeekBar) getView().findViewById(R.id.seekBar_PrioContacts);
        manageContacts=(ImageView) getView().findViewById(R.id.manageContacts);
        manageContacts_text=(TextView) getView().findViewById(R.id.manageContacts_text);

        //load managers
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        GUIobj_PrioContacts =new feat_PrioContacts(context);
        OBJ_Permissions=new permissionhandler(context);
        //load from memory
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int lastSetMediaVolume= GUIobj_PrioContacts.getPrioContactsVolume(context);

        //initiate GUI Elements
        //EnabledColor= String.valueOf((getResources().getColor(R.color.yellow)));

        int maxMusicVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekBar_PrioContacts.setMax(maxMusicVolume);
        seekBar_PrioContacts.setProgress(lastSetMediaVolume);
       // utilityHelpers.adjustTitleTextSize(FeatTitle,context,75);

        if (utilityHelpers.ispermissionpending(context, feat_PrioContacts.permissions)) {
            process_featureState(false,context);
            TapToEnable.setText(R.string.Allfeat_Tap2Permission);
        }

        taptotestvolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtones testSound = new ringtones(context, 0,"null");
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int seekbarVolume = GUIobj_PrioContacts.getPrioContactsVolume(context);
                am.setStreamVolume(AudioManager.STREAM_MUSIC, seekbarVolume, 0);
                testSound.playShorttune(context);
            }
        });

        seekBar_PrioContacts.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pingvolume=i;
                GUIobj_PrioContacts.saveVolume(context,pingvolume);
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

        manageContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(context, LoadContactsActivity.class);
                    intent.putParcelableArrayListExtra("List_Parcel",GUIobj_PrioContacts.getSilExceptList());
                    LoadContactsActivityResultLauncher.launch(intent);
            }
        });

        //checkLogFormat();
        process_featureState(GUIobj_PrioContacts.isFeatureActivated(context),context);

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
                Toast.makeText(requireContext(), R.string.exception_on_dialog, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void adjustInterfaceButton(String ipColor, String ipText) {
        TapToEnable.setText(ipText);
        TapToEnable.setTextColor(Color.parseColor(ipColor));
        FeatTitle.setTextColor(Color.parseColor(ipColor));
        manageContacts.setBackgroundColor(Color.parseColor (ipColor));
    }

    private void userTap() {
        Context context=requireContext();
        NotificationManager tap_notificationManager =
                (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(GUIobj_PrioContacts.isFeatureActivated(context)){
            process_featureState(false,context);
        }
        else{

            if (!tap_notificationManager.isNotificationPolicyAccessGranted()) {

                process_featureState(false,context);
                TapToEnable.setText(R.string.Allfeat_Tap2Permission);
                Dialog_requestDND();
            } else {
                if (!utilityHelpers.ispermissionpending(context, feat_PrioContacts.permissions)) {

                    process_featureState(true,context);

                } else {
                    String[] pend = utilityHelpers.getpendingpermissions_Mandatory(context, feat_PrioContacts.permissions);
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
                    TapToEnable.setText(R.string.Allfeat_Tap2Permission);
                    }
                    else{
                        process_featureState(true,context);
                    TapToEnable.setText(R.string.Allfeat_Tap2Enable);
                    }
            });


    private void process_featureState(boolean state,Context context) {
        GUIobj_PrioContacts.setFeatureActivated(context, state);
        TapToEnable.setChecked(state);
        seekBar_PrioContacts.setEnabled(state);
        manageContacts.setEnabled(state);
        taptotestvolume.setEnabled(state);
        if (state) {
            adjustInterfaceButton(EnabledColor, getString(R.string.tap_to_disable));
            manageContacts.setColorFilter(Color.parseColor(settingscolor_enabled));
            manageContacts_text.setTextColor(Color.parseColor(settingscolor_enabled));
        } else {
            adjustInterfaceButton(DisabledColor, getString(R.string.Allfeat_Tap2Enable));
            manageContacts.setColorFilter(Color.parseColor(settingscolor_disabled));
            manageContacts_text.setTextColor(Color.parseColor(settingscolor_disabled));


        }
    }

        ActivityResultLauncher<Intent> LoadContactsActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        Intent data = result.getData();
                            GUIobj_PrioContacts.setPrioContactsList(getContext(), data.getParcelableArrayListExtra("outputList"));
                    }
                });

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

