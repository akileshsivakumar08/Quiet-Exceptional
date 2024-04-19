package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import AlertMissedCalls.feat_AlertMissedCalls;


public class MainActivity extends AppCompatActivity {
    ImageView infoButton;
    public final int SoftwareType=1;
    public DrawerLayout drawerLayout;
    MyPagerAdapter adapter;
    ImageView share;
    ImageView rateapp;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public static int currentFragmentPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context=getApplicationContext();
        setContentView(R.layout.activity_main);

        TabLayout pagerTabLayout;
        pagerTabLayout=findViewById(R.id.tablayout);
        utilityHelpers.saveIntToMemory(context,"SOFTWARETYPE",SoftwareType);
        infoButton=(ImageView) findViewById(R.id.infoButton);
        share=(ImageView)  findViewById(R.id.share);
        rateapp=(ImageView) findViewById(R.id.rateapp);
        if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)== PackageManager.PERMISSION_GRANTED)){
            utilityHelpers.checkLogFormat(context);
        }

        ViewPager2 viewPager = findViewById(R.id.pager);
        adapter = new MyPagerAdapter(this);
        viewPager.setAdapter(adapter);

        pagerTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentFragmentPosition = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(pagerTabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    }
                });
        tabLayoutMediator.attach();

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.privacy)
                {

                    String url = "https://sites.google.com/view/quietexceptional/privacy-policy?authuser=9";

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                else if(id==R.id.terms)
                {

                    String url = "https://sites.google.com/view/quietexceptional/terms-and-conditions?authuser=9";

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                else if(id==R.id.troubleshoot)
                {
                    Intent intent = new Intent(context, troubleshoot.class);
                            startActivity(intent);
                        }


                return true;

            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       // getSupportActionBar().setHomeButtonEnabled(true);



        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description="";
                switch(currentFragmentPosition){
                    case 0:
                        description= feat_PrioContacts.getDescription();
                        break;
                    case 1:
                        description= feat_AlertMissedCalls.getDescription();
                        break;
                    case 2:
                        description= feat_QuickSwitch.getDescription();
                        break;
                    case 3:
                        description= feat_AllowRepeatCallers.getDescription();
                        break;
                }
                postInfoDialog(description);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage= "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });

        rateapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=Quietexceptional "));
                startActivity(i);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void postInfoDialog(String description) {
        try {
            //start a dialog box
            AlertDialog.Builder noti_alertbuilder = new AlertDialog.Builder(this);
            noti_alertbuilder.setMessage(description);
            AlertDialog alertDialog = noti_alertbuilder.create();
            alertDialog.show();
        } catch (Exception e) {

            Log.e(TAG, " Exception on dialog  " + e);
            Toast.makeText(getApplicationContext(), " Exception on dialog ", Toast.LENGTH_SHORT).show();
        }
    }

}