package com.example.allowrepeatcallers;

import static android.os.VibrationEffect.EFFECT_TICK;
import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_VOLUME_DOWN;
import static android.view.KeyEvent.KEYCODE_VOLUME_UP;
import static java.lang.System.currentTimeMillis;

import android.accessibilityservice.AccessibilityService;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class myAccessibilityService extends AccessibilityService {
    feat_quickSwitch obj_QS;
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        obj_QS=new feat_quickSwitch(getApplicationContext());
        Context context = getApplicationContext();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        int accessEnabled = 0;
        try {
            accessEnabled = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (accessEnabled != 0) {
            obj_QS.setFeatureActivated(utilityHelpers.loadBooleanFromMemory(context, "IS_QUICKSWITCH_ACTIVATED"));
            if (obj_QS.isFeatureActivated()){
                int action, keycode;

            action = event.getAction();
            keycode = event.getKeyCode();
            MediaPlayer mediaPlayer = new MediaPlayer();
            AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if ((feat_quickSwitch.upcounter == 0) || (feat_quickSwitch.downcounter == 0)) {
                    feat_quickSwitch.Current_MediaVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                }

                if (!am.isMusicActive() && (am.getMode() != AudioManager.MODE_RINGTONE) && (am.getMode() != AudioManager.MODE_IN_COMMUNICATION) && (am.getMode() != AudioManager.MODE_IN_CALL)) {

                    switch (keycode) {
                        case KEYCODE_VOLUME_UP:
                            if (action == ACTION_UP) {
                                feat_quickSwitch.currentTime = currentTimeMillis();
                                feat_quickSwitch.timediff = feat_quickSwitch.currentTime - feat_quickSwitch.oldtime;
                                if ((feat_quickSwitch.timediff < 1000) && (feat_quickSwitch.upcounter != 0) && (AudioManager.RINGER_MODE_NORMAL != am.getRingerMode())) {
                                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                    am.setStreamVolume(AudioManager.STREAM_MUSIC, feat_quickSwitch.Current_MediaVolume, 0);
                                    Toast.makeText(this, "Volume up pressed and released", Toast.LENGTH_SHORT).show();
                                    feat_quickSwitch.upcounter = 0;
                                }
                                feat_quickSwitch.upcounter = feat_quickSwitch.upcounter + 1;
                                feat_quickSwitch.oldtime = feat_quickSwitch.currentTime;

                            }
                    }
                    switch (keycode) {
                        case KEYCODE_VOLUME_DOWN:
                            if (action == ACTION_UP) {
                                feat_quickSwitch.currentTime = currentTimeMillis();
                                feat_quickSwitch.timediff = feat_quickSwitch.currentTime - feat_quickSwitch.oldtime;
                                if ((feat_quickSwitch.timediff < 1000) && (feat_quickSwitch.downcounter != 0)) {
                                    if (AudioManager.RINGER_MODE_NORMAL == am.getRingerMode()) {


                                        VibratorManager vibratorManager = null;


                                        Vibrator vibrator;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                            vibratorManager = (VibratorManager) getApplicationContext().getSystemService(getApplicationContext().VIBRATOR_MANAGER_SERVICE);
                                            vibrator = vibratorManager.getDefaultVibrator();

                                            VibrationEffect effect = VibrationEffect.createPredefined(EFFECT_TICK);
                                            //vibrator.vibrate(effect);

                                            Toast.makeText(this, "set to vibrate", Toast.LENGTH_SHORT).show();
                                        }
                                        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                                        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                    } else if (AudioManager.RINGER_MODE_VIBRATE == am.getRingerMode()) {
                                       // am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                        //am.setStreamVolume(AudioManager.STREAM_RING, 0, 2);
                                        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                                        Toast.makeText(this, "set to silent", Toast.LENGTH_SHORT).show();

                                    }

                                    feat_quickSwitch.downcounter = -1;
                                }
                                feat_quickSwitch.downcounter = feat_quickSwitch.downcounter + 1;
                                feat_quickSwitch.oldtime = feat_quickSwitch.currentTime;

                            }
                    }
                } else if (am.getMode() == AudioManager.MODE_IN_CALL) {
                    switch (keycode) {
                        case KEYCODE_VOLUME_UP:
                            if (action == ACTION_UP) {
                                feat_quickSwitch.currentTime = currentTimeMillis();
                                feat_quickSwitch.timediff = feat_quickSwitch.currentTime - feat_quickSwitch.oldtime;
                                if ((feat_quickSwitch.timediff < 500) && (feat_quickSwitch.call_upcounter != 0)) {
                                    Toast.makeText(this, "Volume up in call", Toast.LENGTH_SHORT).show();
                                    feat_quickSwitch.call_upcounter = 0;
                                }
                                feat_quickSwitch.call_upcounter = feat_quickSwitch.call_upcounter + 1;
                                feat_quickSwitch.oldtime = feat_quickSwitch.currentTime;

                            }
                        case KEYCODE_VOLUME_DOWN:
                            if (action == ACTION_UP) {
                                feat_quickSwitch.currentTime = currentTimeMillis();
                                feat_quickSwitch.timediff = feat_quickSwitch.currentTime - feat_quickSwitch.oldtime;
                                if ((feat_quickSwitch.timediff < 500) && (feat_quickSwitch.call_downcounter != 0)) {
                                    am.setSpeakerphoneOn(false);
                                    Toast.makeText(this, "Volume down in call", Toast.LENGTH_SHORT).show();
                                    feat_quickSwitch.call_downcounter = 0;
                                }
                                feat_quickSwitch.call_downcounter = feat_quickSwitch.call_downcounter + 1;
                                feat_quickSwitch.oldtime = feat_quickSwitch.currentTime;

                            }
                    }
                } /*else if(quickSwitch.myRingerisplaying==1){
                    switch (keycode) {
                        case KEYCODE_VOLUME_DOWN:
                            if (action == ACTION_UP) {

                                    am.setStreamVolume(AudioManager.STREAM_RING,0,0);

                            }
                    }
                }*/
            }

        }
        }

            return super.onKeyEvent(event);
        }
    }
