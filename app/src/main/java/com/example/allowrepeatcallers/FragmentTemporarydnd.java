package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;
import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.RECEIVER_NOT_EXPORTED;

import static androidx.core.content.ContextCompat.RECEIVER_EXPORTED;
import static androidx.core.content.ContextCompat.registerReceiver;

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
            Log.i(TAG,"onResume AMC");
            if (obj_tempDND.isFeatureActivated(context)) {
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

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            timechoices.setAdapter(adapter);
            utilityHelpers.adjustTitleTextSize(FeatTitle, context,50);
        } catch (Exception e) {
            String ErrorFlow="AMC_frag_Throw GUI INIT Exception";
            Log.e(TAG,"AMC_frag_Throw GUI INIT Exception");
            utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
            //Throw GUI INIT Exception
            throw new RuntimeException(e);
        }
        try{
        obj_tempDND=new feat_tempdnd(context);
        OBJ_Permissions=new permissionhandler(context);

            String choice=obj_tempDND.getSelectedTimerChoice(context);
            int position=adapter.getPosition(choice);
            timechoices.setSelection(position);

    } catch (Exception e) {
            String ErrorFlow="AMC_frag_Throw Feature Variables INIT Exception";
            Log.e(TAG,"AMC_frag_Throw Feature Variables INIT Exception");
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

        timechoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                obj_tempDND.savetimerChoice(context,selectedItem);
                // Handle the item selection here
                // For example, display a toast message
                 Toast.makeText(context, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
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




        if (obj_tempDND.isFeatureActivated(context)) {
            //startCustomTileService
            tileServiceIntent.putExtra("isActive", false);
            context.startService(tileServiceIntent);
            process_featureState(1, context);
            obj_tempDND.setFeatureActivated(context, false);

        } else {
            if (!utilityHelpers.ispermissionpending(context, feat_AlertMissedCalls.permissions)) {
                //StopCustomTileService
                tileServiceIntent.putExtra("isActive", true);
                context.startService(tileServiceIntent);
                obj_tempDND.setFeatureActivated(context, true);
                process_featureState(2, context);


            } else {
                String[] pend = utilityHelpers.getpendingpermissions(context, feat_AlertMissedCalls.permissions);
                Boolean permission_already_requested = OBJ_Permissions.werePermissionsRequested(pend);
                if (permission_already_requested == false) {

                    requestPermissionLauncher.launch(pend);
                } else if (permission_already_requested == true) {
                    try {
                        //start a dialog box
                        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialogStyle);
                        builder.setMessage(R.string.permission_already_requested).setPositiveButton(R.string.continue_menu, dialogClickListener)
                                .setNegativeButton(R.string.cancel_menu, dialogClickListener);
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

