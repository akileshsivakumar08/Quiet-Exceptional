package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ImageView infoButton;
    public final int SoftwareType=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context=getApplicationContext();
        setContentView(R.layout.activity_main);

        TabLayout pagerTabLayout;
        pagerTabLayout=findViewById(R.id.tablayout);
        utilityHelpers.saveIntToMemory(context,"SOFTWARETYPE",SoftwareType);
        //infoButton=findViewById(R.id.infoButton);
        if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)== PackageManager.PERMISSION_GRANTED)){
            utilityHelpers.checkLogFormat(context);
        }

        ViewPager2 viewPager = findViewById(R.id.pager);
        MyPagerAdapter adapter = new MyPagerAdapter(this);
        viewPager.setAdapter(adapter);



        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(pagerTabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                    }
                });
        tabLayoutMediator.attach();



    }

    /*public void changeLanguage(View view) {
        // Toggle between English and German
        Configuration config = getResources().getConfiguration();
        if(config.getLocales().get(0).getLanguage().contains("en")){
            config.setLocale(new Locale("de"));
        }
        else{
            config.setLocale(new Locale("en"));
        }

        //getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        Context context = getApplicationContext().createConfigurationContext(config);
        //config.updateFrom(config);
        //getResources();
        super.attachBaseContext(context);

        // Restart the activity to apply the new language
        //recreate();
    }*/
  /*  @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }*/


}