package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
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

import com.example.quietexceptional.R;

import java.util.ArrayList;


public class FragmentSMSAlarm extends Fragment {
    TextView FeatTitle;
    private static final String CHANNEL_ID = "Missed Call Notification";
    ImageView diagnosis;
    TextView TapToEnable;
    Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    final int PERMISSION_CODE_POSTNOTIFICATIONS=1;
    String EnabledColor="#064663";
    ImageView smsimage;
    ImageView smsimage2;
    String DisabledColor="#72435C";
    String EnabledText="Tap To Disable";
    String DisabledText="Tap To Enable";
    ImageView manageContacts;
    TextView taptotestvolume;
    feat_SMSAlarm GUIobj_SA;
    int pingvolume;
    permissionhandler OBJ_Permissions;
    ArrayList<class_Buddy> ContactstoSendSMS;

    private TextView manageContacts_text;
    int start_length;
    int differenceContactLength;
    private String settingscolor_enabled="#2F435A";
    private String settingscolor_disabled="#E4E5E8";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.smsalarm, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        try{
        super.onResume();
        Context context = getContext();

        if (utilityHelpers.ispermissionpending(context, feat_SMSAlarm.permissions)) {
            process_featureState(false, context);
            TapToEnable.setText(R.string.Allfeat_Tap2Permission);
        } else {
            process_featureState(true, context);
            TapToEnable.setText(R.string.long_press_to_send_emergency);
        }
    }
        catch (Exception e) {
            String ErrorFlow="SA_frag_Error On Resume";
            utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context=requireContext();
        utilityHelpers.createNotificationChannel(context);
        try {
            //load GUI Elements
            FeatTitle = (TextView) getView().findViewById(R.id.FeatTitle);
            TapToEnable = (TextView) getView().findViewById(R.id.TapToEnable);
            manageContacts = (ImageView) getView().findViewById(R.id.manageContacts);
            manageContacts_text = (TextView) getView().findViewById(R.id.manageContacts_text);
            smsimage = (ImageView) getView().findViewById(R.id.smsimage);
            smsimage2 = (ImageView) getView().findViewById(R.id.smsimage2);
            //load managers

            //load from memory

            //initiate GUI Elements
            animateScalePan(smsimage, 1.2f, 0);
            //animateScalePan(smsimage2,1.1f,500);
        }
        catch (Exception e) {
            //Throw GUI INIT Exception

            String ErrorFlow="SA_frag_Throw GUI INIT Exception";
            utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
            throw new RuntimeException(e);
        }
        try {
            GUIobj_SA = new feat_SMSAlarm(context);
            OBJ_Permissions = new permissionhandler(context);
            ContactstoSendSMS = new ArrayList<class_Buddy>();

        }
        catch (Exception e) {
            String ErrorFlow="SA_frag_Throw Feature Variables INIT Exception";
            utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
            //Throw Feature Variables INIT Exception
            throw new RuntimeException(e);
        }
        try{
            process_featureState(GUIobj_SA.isFeatureActivated(context),context);
        if (utilityHelpers.ispermissionpending(context, feat_SMSAlarm.permissions)) {
            process_featureState(false, context);
        }
    }
    catch (Exception e) {
        //Throw feature state Exception
        String ErrorFlow="SA_frag_Throw feature state Exception";
        utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
        throw new RuntimeException(e);
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
                start_length=GUIobj_SA.getSilExceptList().size();
                LoadContactsActivityResultLauncher.launch(intent);
            }
        });

        //checkLogFormat();


        FeatTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(GUIobj_SA.isFeatureActivated(context)){
                    if(!utilityHelpers.ispermissionpending(context,feat_SMSAlarm.permissions)) {
                        int favID=GUIobj_SA.findFavourite();
                        if(favID!=255) {
                            class_Buddy favContact = GUIobj_SA.getContact(favID);
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("smsto:" + favContact.getBuddy_PhNo()));

                            //intent.setData(Uri.parse(favContact.getBuddy_PhNo()));
                            intent.putExtra(Intent.EXTRA_TEXT, favContact.getBuddy_Message());
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(context, R.string.no_favourites_added, Toast.LENGTH_SHORT).show();
                        }
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
                noti_alertbuilder.setMessage(R.string.provide_dnd_permissions).setPositiveButton(R.string.continue_menu, noti_alert_dialogClickListener)
                        .setNegativeButton(R.string.cancel_menu, noti_alert_dialogClickListener);
                AlertDialog alertDialog = noti_alertbuilder.create();
                alertDialog.show();
            } catch (Exception e) {
                String ErrorFlow="SA_frag_ Exception on dialog  ";
                utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
               // Log.e(TAG, " Exception on dialog  " + e);
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
                TapToEnable.setText(R.string.Allfeat_Tap2Permission);
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
    private void animateScalePan(View v,float scale,long startDelay) {
        AnimatorSet animSetXY = new AnimatorSet();

        ObjectAnimator scaleanimY = ObjectAnimator.ofFloat(v,
                "ScaleY",1f, scale);
        scaleanimY.setRepeatCount(ValueAnimator.INFINITE);
        //scaleanimY.setRepeatMode(ValueAnimator.REVERSE);
        ObjectAnimator scaleanimX = ObjectAnimator.ofFloat(v,
                "ScaleX", 1f, scale);
        ObjectAnimator fade = ObjectAnimator.ofFloat(v,
                "alpha", 1f, 0f);
        fade.setRepeatCount(ValueAnimator.INFINITE);


        scaleanimX.setRepeatCount(ValueAnimator.INFINITE);
        //scaleanimX.setRepeatMode(ValueAnimator.REVERSE);
        animSetXY.playTogether(scaleanimX, scaleanimY,fade);
        animSetXY.setInterpolator(new LinearInterpolator());
        animSetXY.setDuration(1500);
        animSetXY.setStartDelay(startDelay);
        animSetXY.start();

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
            String ErrorFlow="SA_frag_ Exception on dialog  ";
            utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
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
                    ArrayList<class_Buddy> outputList=data.getParcelableArrayListExtra("outputList");
                    ContactstoSendSMS =data.getParcelableArrayListExtra("diffList");
                    GUIobj_SA.setSMSAlarmList(getContext(), data.getParcelableArrayListExtra("outputList"));
                    if(ContactstoSendSMS.size()>0){
                        requestDialogShareSMS();
                    }
                }
            });
    private void process_featureState(boolean state,Context context) {
        GUIobj_SA.setFeatureActivated(context,state);

        manageContacts.setEnabled(state);
        if(state){
            adjustInterfaceButton(EnabledColor,EnabledText);
            TapToEnable.setText(R.string.long_press_to_send_emergency);

            manageContacts.setColorFilter(Color.parseColor(settingscolor_enabled));
            manageContacts_text.setTextColor(Color.parseColor(settingscolor_enabled));
        }
        else{
            adjustInterfaceButton(DisabledColor,DisabledText);
            TapToEnable.setText(R.string.Allfeat_Tap2Permission);

            manageContacts.setColorFilter(Color.parseColor(settingscolor_disabled));
            manageContacts_text.setTextColor(Color.parseColor(settingscolor_disabled));
        }

    }


    private void requestDialogShareSMS() {
        try {
            //start a dialog box
            AlertDialog.Builder noti_alertbuilder = new AlertDialog.Builder(requireContext());
            noti_alertbuilder.setMessage(R.string.requestShareSMSAlarm).setPositiveButton(R.string.accept_menu, shareSMS_dialogClickListener)
                    .setNegativeButton(R.string.reject_menu, shareSMS_dialogClickListener);
            AlertDialog alertDialog = noti_alertbuilder.create();
            alertDialog.show();
        } catch (Exception e) {
            String ErrorFlow="SA_frag_ requestDialogShareSMS";
            utilityHelpers.saveErrorToMemory(requireContext(),ErrorFlow);
        }

    }

    DialogInterface.OnClickListener shareSMS_dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Context context=requireContext();
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage = getString(R.string.share_message_addedContact);
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=com.QE.free" + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    //smsReceiver.DND_OverridePermission=false;
                    break;
            }
        }
    };



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

