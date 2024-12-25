package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;
import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.RECEIVER_NOT_EXPORTED;

import static androidx.core.content.ContextCompat.RECEIVER_EXPORTED;
import static androidx.core.content.ContextCompat.registerReceiver;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.quietexceptional.R;

public class FragmentTemporarydnd extends Fragment {
    TextView FeatTitle;
    SwitchCompat TapToEnable;
    String EnabledColor="#FFD369";
    NotificationManager notificationManager;
    ImageView pony;
    String DisabledColor="#F79489";
    ImageView share;
    String EnabledText="Tap To Disable";
    String DisabledText="Tap To Enable";
    feat_tempdnd obj_tempDND;

    TextView taptotestvolume;
    TextView descText;
    BroadcastReceiver workOverBroadcast;
    Spinner timechoices;
    permissionhandler OBJ_Permissions;
    Intent tileServiceIntent;
    private String settingscolor_enabled="#2F435A";
    private String settingscolor_disabled="#E4E5E8";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tempdnd, container, false);
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
            Log.i(TAG,"onResume TempDND");
            if (obj_tempDND.isFeatureActivated(context)) {
                Log.i(TAG,"onResume TempDND Feat Activated");
                process_featureState(2, context);
            } else {
                Log.i(TAG,"onResume TempDND  Permission Granted But Deactivated");
                process_featureState(1, context);
            }
            if (obj_tempDND.arePermissionsPending(context)) {
                Log.i(TAG,"onResume TempDND  Permission Not Granted");
                process_featureState(0, context);
                TapToEnable.setText(R.string.Allfeat_Tap2Permission);
            }
        }
        catch (Exception e) {
            String ErrorFlow="TempDND_frag_Error On Resume";
            utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
            Log.e(TAG,"TempDND_frag_Error On Resume");
            //Error On Resume
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Context context=requireContext();
        notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        CustomTileService.requestListeningState(context, new ComponentName(context, CustomTileService.class));
        tileServiceIntent = new Intent(context, CustomTileService.class);
        ArrayAdapter<CharSequence> adapter;
        //Initialize GUI
        try {
            TapToEnable = (SwitchCompat) getView().findViewById(R.id.TapToEnable);
            FeatTitle = (TextView) getView().findViewById(R.id.FeatTitle);
            timechoices = (Spinner) getView().findViewById(R.id.timechoices);
            adapter = ArrayAdapter.createFromResource(context,
                    R.array.time_options, android.R.layout.simple_spinner_item);
            descText=(TextView) getView().findViewById(R.id.desctext);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            timechoices.setAdapter(adapter);
           // utilityHelpers.adjustTitleTextSize(FeatTitle, context,50);


            //animateScalePan(leftLine,  0);
        } catch (Exception e) {
            String ErrorFlow="TempDND_frag_Throw GUI INIT Exception";
            Log.e(TAG,"TempDND_frag_Throw GUI INIT Exception");
            utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
            //Throw GUI INIT Exception
            throw new RuntimeException(e);
        }
        try{
        obj_tempDND=new feat_tempdnd(context);

            String choice=obj_tempDND.getSelectedTimerChoice(context);
            int position=adapter.getPosition(choice);
            timechoices.setSelection(position);

    } catch (Exception e) {
            String ErrorFlow="TempDND_frag_Throw Feature Variables INIT Exception";
            Log.e(TAG,"TempDND_frag_Throw Feature Variables INIT Exception");
            utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
        //Throw Feature Variables INIT Exception
        throw new RuntimeException(e);
    }

try {
    if (obj_tempDND.isFeatureActivated(context)) {
        process_featureState(2, context);
    } else {
        process_featureState(1, context);
    }
    if(obj_tempDND.arePermissionsPending(context)){
        process_featureState(0, context);
    }
    descText.setText(feat_tempdnd.getDescription());
    descText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
}
catch (Exception e) {
    String ErrorFlow="TempDND_frag_Throw feature state Exception";
    Log.e(TAG,"TempDND_frag_Throw feature state Exception");
    utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
    //Throw feature state Exception
    throw new RuntimeException(e);
}

        timechoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                obj_tempDND.savetimerChoice(context,selectedItem);
                // Handle the item selection here
                // For example, display a toast message
                // Toast.makeText(context, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected
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


        workOverBroadcast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String enable=intent.getStringExtra("TDND_State");
                if(enable.equals("STOP_WORKOVER")){
                    obj_tempDND.setFeatureActivated(context, false);
                    process_featureState(1,context);
                }
                else if(enable.equals("START_CUSTOMTILETAP")){
                    process_featureState(2,context);
                } else if (enable.equals("STOP_CUSTOMTILETAP")) {
                    process_featureState(1,context);
                }

               // Toast.makeText(context, "SMS SENT!!", Toast.LENGTH_SHORT).show();

                


            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.allowrepeatcallers.TIMERWORK_OVER");
        getActivity().registerReceiver(workOverBroadcast, intentFilter, Context.RECEIVER_EXPORTED);


    }


    @Override
    public void onDestroy() {
        Context context =getContext();
        context.unregisterReceiver(workOverBroadcast);
        super.onDestroy();
    }

    private void process_featureState(int state, Context context) {
        try {
            Boolean bool_state = false;
            if (state == 2) {
                bool_state = true;
            }


            
            

            TapToEnable.setChecked(bool_state);
            if (state == 2) {
                adjustInterfaceButton(EnabledColor, getString(R.string.tap_to_disable));
                obj_tempDND.starttempDND(context);
            } else if (state == 1) {
                adjustInterfaceButton(DisabledColor, getString(R.string.Allfeat_Tap2Enable));
                obj_tempDND.stoptempdnd(context);
            } else if (state == 0) {
                adjustInterfaceButton(DisabledColor, getString(R.string.Allfeat_Tap2Permission));
            }
        }
        catch (Exception e) {
            String ErrorFlow="TempDND_frag_Throw feature state Exception";
            Log.e(TAG,"TempDND_frag_Throw feature state Exception");
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



        if (obj_tempDND.arePermissionsPending(context)) {
            postPermissionDialog(context);
        } else if (obj_tempDND.isBatteryNotoptimized(context)) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            startActivity(intent);

        } else {
            if (obj_tempDND.isFeatureActivated(context)) {
                //startCustomTileService
                tileServiceIntent.putExtra("isActive", false);
                context.startService(tileServiceIntent);
                process_featureState(1, context);
                obj_tempDND.setFeatureActivated(context, false);

            } else {
                //StopCustomTileService
                tileServiceIntent.putExtra("isActive", true);
                context.startService(tileServiceIntent);
                obj_tempDND.setFeatureActivated(context, true);
                process_featureState(2, context);

            }
        }
    }
    catch (Exception e) {
        String ErrorFlow="TempDND_frag_Feature Enable User Tap exception";
        Log.e(TAG,"TempDND_frag_Feature Enable User Tap exception");
        utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
        //Feature Enable User Tap exception
        throw new RuntimeException(e);
    }
    }


    private void postPermissionDialog(Context context) {

        DialogUtils.showAlertDialog(context,
                getString(R.string.permissions_needed),
                getString(R.string.TempDND_Permissionsneeded),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
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


}

