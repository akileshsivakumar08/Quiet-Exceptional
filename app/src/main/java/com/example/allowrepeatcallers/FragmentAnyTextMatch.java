package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;
import static android.content.Context.AUDIO_SERVICE;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.quietexceptional.R;

import java.util.ArrayList;

public class FragmentAnyTextMatch extends Fragment {
    TextView FeatTitle;
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

    feat_AnyTextMatch obj_ATM;
    ImageView settings_ATM;
    ImageView managecustomText;


    permissionhandler OBJ_Permissions;
    TextView descText;

    private String settingscolor_enabled="#2F435A";
    private String settingscolor_disabled="#E4E5E8";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.anytextmatch, container, false);
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
            Log.i(TAG,"onResume ATM");
            if (obj_ATM.isFeatureActivated(context)) {
                Log.i(TAG,"onResume ATM Feat Activated");
                process_featureState(2, context);
            } else {
                Log.i(TAG,"onResume ATM  Permission Granted But Deactivated");
                process_featureState(1, context);
            }
            /*if (utilityHelpers.ispermissionpending(context, feat_AnyTextMatch.permissions)) {
                Log.i(TAG,"onResume ATM  Permission Not Granted");
                process_featureState(0, context);
                TapToEnable.setText(R.string.Allfeat_Tap2Permission);
            }*/
        }
        catch (Exception e) {
            String ErrorFlow="ATM_frag_Error On Resume";
            utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
            Log.e(TAG,"ATM_frag_Error On Resume");
            //Error On Resume
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context=requireContext();
        ArrayAdapter<CharSequence> adapter;
        //Initialize GUI
        try {
            //utilityHelpers.createNotificationChannel(context);
            TapToEnable = (SwitchCompat) getView().findViewById(R.id.TapToEnable);
            FeatTitle = (TextView) getView().findViewById(R.id.FeatTitle);
            settings_ATM = (ImageView) getView().findViewById(R.id.settings_ATM);
            //utilityHelpers.adjustTitleTextSize(FeatTitle, context,75);
            managecustomText=(ImageView) getView().findViewById(R.id.managecustomText);
            descText=(TextView) getView().findViewById(R.id.desctext);

        } catch (Exception e) {
            String ErrorFlow="ATM_frag_Throw GUI INIT Exception";
            Log.e(TAG,"ATM_frag_Throw GUI INIT Exception");
            utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
            //Throw GUI INIT Exception
            throw new RuntimeException(e);
        }
        try{

        obj_ATM=new feat_AnyTextMatch(context);
        OBJ_Permissions=new permissionhandler(context);
        descText.setText(feat_AnyTextMatch.getDescription());
        descText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //Animation anima_scaleright = AnimationUtils.loadAnimation(context, R.anim.scale_fromright);
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF,1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(1000);
            anim.setStartOffset(500);
            //line1.startAnimation(anima_scaleright);
            //animateScalePan(line1, 1.0f, 0);
    } catch (Exception e) {
            String ErrorFlow="ATM_frag_Throw Feature Variables INIT Exception";
            Log.e(TAG,"ATM_frag_Throw Feature Variables INIT Exception");
            utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
        //Throw Feature Variables INIT Exception
        throw new RuntimeException(e);
    }

try {
    if (obj_ATM.isFeatureActivated(context)) {
        process_featureState(2, context);
    } else {
        process_featureState(1, context);
    }

   /* PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    if(!powerManager.isIgnoringBatteryOptimizations(context.getPackageName())){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        startActivity(intent);
    }*/
    /*if (utilityHelpers.ispermissionpending(context, feat_AnyTextMatch.permissions)) {
        process_featureState(0, context);
        TapToEnable.setText(R.string.Allfeat_Tap2Permission);
    }*/
}
catch (Exception e) {
    String ErrorFlow="ATM_frag_Throw feature state Exception";
    Log.e(TAG,"ATM_frag_Throw feature state Exception");
    utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
    //Throw feature state Exception
    throw new RuntimeException(e);
}


managecustomText.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(obj_ATM.isFeatureActivated(context)) {
            Intent intent = new Intent(context, custom_text_list.class);
            intent.putStringArrayListExtra("CustomTextList_Parcel", obj_ATM.loadCustomTextFromMemory(context));
            LoadCTLActivityResultLauncher.launch(intent);
        }
        else{
            Toast.makeText(context, R.string.enable_feature_to_access_settings, Toast.LENGTH_SHORT).show();
        }
    }
});

        settings_ATM.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(obj_ATM.isFeatureActivated(context)) {
                    Intent intent = new Intent(context, settings.class);
                    intent.putParcelableArrayListExtra("settingList_input",obj_ATM.getSettingsList(context));
                    settingsActivityResultLauncher.launch(intent);
                }
                else{
                    Toast.makeText(context, R.string.enable_feature_to_access_settings, Toast.LENGTH_SHORT).show();
                }
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



    }



    private void process_featureState(int state,Context context) {
        try {
            Boolean bool_state = false;
            if (state == 2) {
                bool_state = true;
            }
            obj_ATM.setFeatureActivated(context, bool_state);
            TapToEnable.setChecked(bool_state);
            //settings_ATM.setEnabled(bool_state);
            if (state == 2) {
                adjustInterfaceButton(EnabledColor, getString(R.string.tap_to_disable));
            } else if (state == 1) {
                adjustInterfaceButton(DisabledColor, getString(R.string.Allfeat_Tap2Enable));
            }/* else if (state == 0) {
                adjustInterfaceButton(DisabledColor, getString(R.string.Allfeat_Tap2Permission));
            }*/
        }
        catch (Exception e) {
            String ErrorFlow="ATM_frag_Throw feature state Exception";
            Log.e(TAG,"ATM_frag_Throw feature state Exception");
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
        if (obj_ATM.isFeatureActivated(context)) {
            process_featureState(1, context);

        } else {
            NotificationManager tap_notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if ((!utilityHelpers.isNotificationServiceEnabled(context)) || (!tap_notificationManager.isNotificationPolicyAccessGranted())) {
                postPermissionDialog(context);
              //  Intent intent = new Intent(context, startup_permissions.class);
              //  startActivity(intent);
            }
            else {
                process_featureState(2, context);

            }
        }
    }
    catch (Exception e) {
        String ErrorFlow="ATM_frag_Feature Enable User Tap exception";
        Log.e(TAG,"ATM_frag_Feature Enable User Tap exception");
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
                        Toast.makeText(context, R.string.select_quiet_exceptional_from_the_list, Toast.LENGTH_SHORT).show();
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



    ActivityResultLauncher<Intent> LoadCTLActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Here, no request code
                    Intent data = result.getData();
                    ArrayList<String> outputList=data.getStringArrayListExtra("outputList");
                    obj_ATM.saveCustomTextList(getContext(), outputList);
                }
            });

    ActivityResultLauncher<Intent> settingsActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Here, no request code
                    Intent data = result.getData();
                    ArrayList<class_setting> outputList= new ArrayList<class_setting>();
                    outputList = data.getParcelableArrayListExtra("settingList");
                    class_setting setting_out=outputList.get(0);

                    obj_ATM.savetimerChoice(getContext(),setting_out.getExtraPing());
                    obj_ATM.saveVolume(getContext(),setting_out.getVolume());
                    obj_ATM.saveRingInDND(getContext(),setting_out.isRingInDND());
                    obj_ATM.saveRingtoneUri(getContext(),setting_out.getRingtoneUri());
                }
            });

    private void animateScalePan(View v,float scale,long startDelay) {
        AnimatorSet animSetXY = new AnimatorSet();

        ObjectAnimator fadeout = ObjectAnimator.ofFloat(v,
                "alpha", 1f, 0f);
        fadeout.setDuration(1000);

        fadeout.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator fadein = ObjectAnimator.ofFloat(v,
                "alpha", 0f, 1f);
        fadein.setDuration(1000);

        fadeout.setRepeatCount(ValueAnimator.INFINITE);

        //scaleanimX.setRepeatMode(ValueAnimator.REVERSE);
        animSetXY.playSequentially(fadeout,fadein);
        animSetXY.setInterpolator(new LinearInterpolator());
        animSetXY.setStartDelay(startDelay);
        animSetXY.start();

    }

}



