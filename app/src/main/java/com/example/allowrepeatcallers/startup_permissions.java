package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quietexceptional.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Set;


public class startup_permissions extends AppCompatActivity {
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1 ;
    ImageView infoButton;
    Button NotiPermissions;
    TextView NeedForPermissions2;
    Button continuebtn;
    String DisabledColor="#636363";
    String ContinueDisabledColor="#636363";
    String EnabledColor="#013220";

    public ActionBarDrawerToggle actionBarDrawerToggle;
    public static final String MEMCODE_ERRORMEMORY = "ERRORMEMORY";

    @Override
    protected void onResume() {
        super.onResume();
        Context context=getApplicationContext();
        checkButtonStates(context);

    }

    private void checkButtonStates(Context context) {
        try {
            NotificationManager tap_notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (isNotificationServiceEnabled()) {

                setButtonState(NotiPermissions, true);
            } else {
                setButtonState(NotiPermissions, false);
            }
            if ((isNotificationServiceEnabled())) {
                continuebtn.setEnabled(true);
                continuebtn.setBackgroundColor(Color.parseColor(EnabledColor));
            } else {
                continuebtn.setEnabled(false);
                continuebtn.setBackgroundColor(Color.parseColor(ContinueDisabledColor));
            }
        }
        catch (Exception e){
            Log.e(TAG,"startup_Permissions: Check Button States Error");
            String ErrorFlow="startup_Permissions: Check Button States Error";
            utilityHelpers.saveErrorToMemory(context,ErrorFlow);
            //Throw GUI INIT Exception
            throw new RuntimeException(e);
        }
    }

    private void setButtonState(Button Btn, boolean state) {
        Btn.setEnabled(!state);
        if(state) {
            Btn.setBackgroundColor(Color.parseColor(EnabledColor));
        }else{
            Btn.setBackgroundColor(Color.parseColor(DisabledColor));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_permissions);
        Context context = getApplicationContext();

       // if ((!isNotificationServiceEnabled()) || (!tap_notificationManager.isNotificationPolicyAccessGranted())) {
        if ((!isNotificationServiceEnabled()) ) {
            try{
            Log.i(TAG, "StartupPermissions onCreate: Notification Service not enabled");

            NotiPermissions = (Button) findViewById(R.id.NotiPermissions);
            NeedForPermissions2 = (TextView) findViewById(R.id.NeedForPermissions2);
            continuebtn = (Button) findViewById(R.id.continuebtn);
            checkButtonStates(context);
            NotiPermissions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    Toast.makeText(getApplicationContext(), " Select Quiet Exceptional from the list ", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            });

            continuebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            NeedForPermissions2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://sites.google.com/view/quietexceptional/permission-summary";

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
        }
            catch (Exception e){
                Log.e(TAG,"StartupPermissions onCreate: Notification Service not enabled");
                String ErrorFlow="StartupPermissions onCreate: Notification Service not enabled";
                utilityHelpers.saveErrorToMemory(context,ErrorFlow);
                //Throw GUI INIT Exception
                throw new RuntimeException(e);
            }
        } else {
            Log.i(TAG, "StartupPermissions onCreate: Notification Service enabled");
        }
    }

    private boolean isNotificationServiceEnabled() {
        // Check if the notification listener service is enabled
        Set<String> enabledListenerServices = NotificationManagerCompat.getEnabledListenerPackages(getApplicationContext());
        return enabledListenerServices.contains(getApplicationContext().getPackageName());
    }



}