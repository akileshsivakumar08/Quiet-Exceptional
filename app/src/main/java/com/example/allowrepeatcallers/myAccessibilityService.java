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

import com.example.quietexceptional.R;


public class myAccessibilityService extends AccessibilityService {
    feat_QuickSwitch obj_QS;
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        obj_QS=new feat_QuickSwitch(getApplicationContext());
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
            if (obj_QS.isFeatureActivated(context)){
                int action, keycode;

            action = event.getAction();
            keycode = event.getKeyCode();
            MediaPlayer mediaPlayer = new MediaPlayer();
            AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if ((feat_QuickSwitch.upcounter == 0) || (feat_QuickSwitch.downcounter == 0)) {
                    feat_QuickSwitch.Current_MediaVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                }

                if (!am.isMusicActive() && (am.getMode() != AudioManager.MODE_RINGTONE) && (am.getMode() != AudioManager.MODE_IN_COMMUNICATION) && (am.getMode() != AudioManager.MODE_IN_CALL)) {

                    switch (keycode) {
                        case KEYCODE_VOLUME_UP:
                            if (action == ACTION_UP) {
                                feat_QuickSwitch.currentTime = currentTimeMillis();
                                feat_QuickSwitch.timediff = feat_QuickSwitch.currentTime - feat_QuickSwitch.oldtime;
                                if ((feat_QuickSwitch.timediff < 1000) && (feat_QuickSwitch.upcounter != 0) && (AudioManager.RINGER_MODE_NORMAL != am.getRingerMode())) {
                                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                    am.setStreamVolume(AudioManager.STREAM_MUSIC, feat_QuickSwitch.Current_MediaVolume, 0);
                                    feat_QuickSwitch.upcounter = 0;
                                }
                                feat_QuickSwitch.upcounter = feat_QuickSwitch.upcounter + 1;
                                feat_QuickSwitch.oldtime = feat_QuickSwitch.currentTime;

                            }
                    }
                    switch (keycode) {
                        case KEYCODE_VOLUME_DOWN:
                            if (action == ACTION_UP) {
                                feat_QuickSwitch.currentTime = currentTimeMillis();
                                feat_QuickSwitch.timediff = feat_QuickSwitch.currentTime - feat_QuickSwitch.oldtime;
                                if ((feat_QuickSwitch.timediff < 1000) && (feat_QuickSwitch.downcounter != 0)) {
                                    if (AudioManager.RINGER_MODE_NORMAL == am.getRingerMode()) {


                                        VibratorManager vibratorManager = null;


                                        Vibrator vibrator;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                            vibratorManager = (VibratorManager) getApplicationContext().getSystemService(getApplicationContext().VIBRATOR_MANAGER_SERVICE);
                                            vibrator = vibratorManager.getDefaultVibrator();

                                            VibrationEffect effect = VibrationEffect.createPredefined(EFFECT_TICK);
                                            //vibrator.vibrate(effect);

                                            Toast.makeText(this, R.string.set_to_vibrate, Toast.LENGTH_SHORT).show();
                                        }
                                        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                                        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                    } else if (AudioManager.RINGER_MODE_VIBRATE == am.getRingerMode()) {
                                       // am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                        //am.setStreamVolume(AudioManager.STREAM_RING, 0, 2);
                                        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                                        Toast.makeText(this, R.string.set_to_silent, Toast.LENGTH_SHORT).show();

                                    }

                                    feat_QuickSwitch.downcounter = -1;
                                }
                                feat_QuickSwitch.downcounter = feat_QuickSwitch.downcounter + 1;
                                feat_QuickSwitch.oldtime = feat_QuickSwitch.currentTime;

                            }
                    }
                } else if (am.getMode() == AudioManager.MODE_IN_CALL) {
                    switch (keycode) {
                        case KEYCODE_VOLUME_UP:
                            if (action == ACTION_UP) {
                                feat_QuickSwitch.currentTime = currentTimeMillis();
                                feat_QuickSwitch.timediff = feat_QuickSwitch.currentTime - feat_QuickSwitch.oldtime;
                                if ((feat_QuickSwitch.timediff < 500) && (feat_QuickSwitch.call_upcounter != 0)) {
                                    feat_QuickSwitch.call_upcounter = 0;
                                }
                                feat_QuickSwitch.call_upcounter = feat_QuickSwitch.call_upcounter + 1;
                                feat_QuickSwitch.oldtime = feat_QuickSwitch.currentTime;

                            }
                        case KEYCODE_VOLUME_DOWN:
                            if (action == ACTION_UP) {
                                feat_QuickSwitch.currentTime = currentTimeMillis();
                                feat_QuickSwitch.timediff = feat_QuickSwitch.currentTime - feat_QuickSwitch.oldtime;
                                if ((feat_QuickSwitch.timediff < 500) && (feat_QuickSwitch.call_downcounter != 0)) {
                                    am.setSpeakerphoneOn(false);
                                    feat_QuickSwitch.call_downcounter = 0;
                                }
                                feat_QuickSwitch.call_downcounter = feat_QuickSwitch.call_downcounter + 1;
                                feat_QuickSwitch.oldtime = feat_QuickSwitch.currentTime;

                            }
                    }
                }
            }

        }
        }

            return super.onKeyEvent(event);
        }
    }
