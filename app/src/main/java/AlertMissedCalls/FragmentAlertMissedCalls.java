package AlertMissedCalls;

import static android.content.ContentValues.TAG;
import static android.content.Context.AUDIO_SERVICE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.fragment.app.Fragment;

import com.example.allowrepeatcallers.R;
import com.example.allowrepeatcallers.permissionhandler;
import com.example.allowrepeatcallers.ringtones;
import com.example.allowrepeatcallers.utilityHelpers;

import AlertMissedCalls.feat_AlertMissedCalls;

public class FragmentAlertMissedCalls extends Fragment {
    TextView FeatTitle;
    ImageView diagnosis;
    SwitchCompat TapToEnable;
    ImageView infoButton;
    Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    final int PERMISSION_CODE_POSTNOTIFICATIONS=1;
    String EnabledColor="#1A4314";
    ImageView pony;
    String DisabledColor="#72435C";
    ImageView share;
    String EnabledText="Tap To Disable";
    String DisabledText="Tap To Enable";
    View leftLine;
    View rightLine;
    SeekBar seekbar;
    feat_AlertMissedCalls obj_AMC;
    TextView taptotestvolume;
    permissionhandler OBJ_Permissions;
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
        super.onResume();
        Context context=getContext();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context=requireContext();
        //utilityHelpers.createNotificationChannel(context);
        TapToEnable=(SwitchCompat) getView().findViewById(R.id.TapToEnable);
        taptotestvolume=(TextView) getView().findViewById(R.id.taptotestvolume);
        FeatTitle=(TextView) getView().findViewById(R.id.FeatTitle);

        seekbar=(SeekBar) getView().findViewById(R.id.seekBar);

        leftLine=(View) getView().findViewById(R.id.leftLine);
        rightLine=(View) getView().findViewById(R.id.rightLine);
        utilityHelpers.adjustTitleTextSize(FeatTitle,context);
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        int maxMusicVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekbar.setMax(maxMusicVolume);
        obj_AMC=new feat_AlertMissedCalls(context);
        OBJ_Permissions=new permissionhandler(context);

        Animation anima_scaleleft = AnimationUtils.loadAnimation(context, R.anim.scale_fromleft);
        Animation anima_scaleright = AnimationUtils.loadAnimation(context, R.anim.scale_fromright);
        leftLine.startAnimation(anima_scaleleft);
        rightLine.startAnimation(anima_scaleright);
        // Set the current volume of the SeekBar to the current volume of the MediaPlayer:
        int lastSetMediaVolume=obj_AMC.getPingVolume(context);
        seekbar.setProgress(lastSetMediaVolume);
        if(obj_AMC.isFeatureActivated(context)){
            process_featureState(true,context);
        }
        else{
            process_featureState(false,context);
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

    }

    private void process_featureState(boolean state,Context context) {
        obj_AMC.setFeatureActivated(context,state);
        TapToEnable.setChecked(state);
        seekbar.setEnabled(state);
        taptotestvolume.setEnabled(state);
        if(state){
            adjustInterfaceButton(EnabledColor,EnabledText);
        }
        else{
            adjustInterfaceButton(DisabledColor,DisabledText);
        }

    }


    private void adjustInterfaceButton(String ipColor, String ipText) {
        TapToEnable.setText(ipText);
        TapToEnable.setTextColor(Color.parseColor(ipColor));
        FeatTitle.setTextColor(Color.parseColor(ipColor));
    }

    private void userTap() {
        Context context=requireContext();
        if(obj_AMC.isFeatureActivated(context)){
            process_featureState(false,context);

        }
        else{
                if (!utilityHelpers.ispermissionpending(context, feat_AlertMissedCalls.permissions)) {
                    process_featureState(true,context);
                    utilityHelpers.checkLogFormat(context);
                    Toast.makeText(context, " Feature Enable saved ", Toast.LENGTH_SHORT).show();

                } else {
                    String[] pend = utilityHelpers.getpendingpermissions(context, feat_AlertMissedCalls.permissions);
                    Boolean permission_already_requested=OBJ_Permissions.werePermissionsRequested(pend);
                    if (permission_already_requested == false) {

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
                OBJ_Permissions.setPermissionRequested(context,isGranted);
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


   /* @Override
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

                        break;

                    }
                    process_featureState(true,context);

                }

                feat_AlertMissedCalls.permission_already_requested=true;
                obj_AMC.setPermissionRequested(context);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/

}

