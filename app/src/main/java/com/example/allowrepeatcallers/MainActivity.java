package com.example.allowrepeatcallers;

import static android.app.AlertDialog.THEME_HOLO_DARK;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quietexceptional.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1 ;
    ImageView infoButton;
    public final int SoftwareType=1;
    public DrawerLayout drawerLayout;
    MyPagerAdapter adapter;
    ImageView share;
    ImageView rateapp;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public static int currentFragmentPosition;
    public static final String MEMCODE_ERRORMEMORY = "ERRORMEMORY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        NotificationManager tap_notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if ((!isNotificationServiceEnabled()) || (!tap_notificationManager.isNotificationPolicyAccessGranted())) {
            Intent intent = new Intent(context, startup_permissions.class);
            startActivity(intent);
            finish();
        }
        else {

            TabLayout pagerTabLayout;
            pagerTabLayout = findViewById(R.id.tablayout);
            utilityHelpers.saveIntToMemory(context, "SOFTWARETYPE", SoftwareType);
            infoButton = (ImageView) findViewById(R.id.infoButton);
            share = (ImageView) findViewById(R.id.share);
            rateapp = (ImageView) findViewById(R.id.rateapp);


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
                    if (id == R.id.privacy) {
                        Log.i(TAG, "Navigation Item Privacy");
                        String url = "https://sites.google.com/view/quietexceptional/privacy-policy?authuser=9";

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    } else if (id == R.id.terms) {
                        Log.i(TAG, "Navigation Item Terms");
                        String url = "https://sites.google.com/view/quietexceptional/terms-and-conditions?authuser=9";

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    } else if (id == R.id.troubleshoot) {
                        Log.i(TAG, "Navigation Item Troubleshoot");

                        String loadedErrors = utilityHelpers.loadStringFromMemory(context, MEMCODE_ERRORMEMORY);
                        if (loadedErrors.equals("null")) {
                            loadedErrors = "No Errors Found. Rate App?";
                            postQuestionDialog(loadedErrors);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("plain/text");
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"quietexceptional@gmail.com"});
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Exceptional Bugs from troubleshooting");
                            intent.putExtra(Intent.EXTRA_TEXT, loadedErrors);
                            startActivity(intent);
                        }
                    } else if (id==R.id.Permissions) {
                        Log.i(TAG, "Navigation Item Permission Summary");
                        String url = "https://sites.google.com/view/quietexceptional/permission-summary";

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }


                    return true;

                }
            });


            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // getSupportActionBar().setHomeButtonEnabled(true);


            infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String description = "";
                    Log.i(TAG, "Menu Item InfoButton");
                    switch (currentFragmentPosition) {
                        case 0:
                            description = feat_AlertMissedCalls.getDescription();
                            break;
                        case 1:
                            description = feat_SMSAlarm.getDescription();
                            break;
                    }
                    postInfoDialog(description);
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Log.i(TAG, "Menu Item Share");
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                        String shareMessage = "\nLet me recommend you this cool app\n\n";
                        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=com.QE.free" + "\n\n";
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                        startActivity(Intent.createChooser(shareIntent, "choose one"));
                    } catch (Exception e) {
                        //e.toString();
                    }
                }
            });

            rateapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"Menu Item RateApp");
                    Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.QE.free"));
                    startActivity(i);
                }
            });
            Boolean appAlreadyLaunched=utilityHelpers.loadBooleanFromMemory(context,"APPALREADYLAUNCHED");
            if(!appAlreadyLaunched){
                appAlreadyLaunched=true;
                utilityHelpers.saveBooleanToMemory(context,"APPALREADYLAUNCHED",appAlreadyLaunched);
                postInfoDialog(getString(R.string.welcome_dialog));
            }

        }
    }


    private boolean isNotificationServiceEnabled() {
        // Check if the notification listener service is enabled
        Set<String> enabledListenerServices = NotificationManagerCompat.getEnabledListenerPackages(getApplicationContext());
        return enabledListenerServices.contains(getApplicationContext().getPackageName());
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
            AlertDialog.Builder noti_alertbuilder = new AlertDialog.Builder(this,R.style.AlertDialogStyle);
            noti_alertbuilder.setMessage(description);
            AlertDialog alertDialog = noti_alertbuilder.create();
            alertDialog.show();
        } catch (Exception e) {

            Log.e(TAG, " Exception on dialog  " + e);
            Toast.makeText(getApplicationContext(), " Exception on dialog ", Toast.LENGTH_SHORT).show();
        }
    }
    private void postQuestionDialog(String description) {
        try {
            //start a dialog box
            AlertDialog.Builder TS_alertbuilder = new AlertDialog.Builder(this,R.style.AlertDialogStyle);
            TS_alertbuilder.setMessage(description).setPositiveButton(R.string.continue_menu, TSdialogListener).setNegativeButton(R.string.cancel_menu, TSdialogListener);
            AlertDialog alertDialog = TS_alertbuilder.create();
            alertDialog.show();
        } catch (Exception e) {

            Log.e(TAG, " Exception on dialog  " + e);
            Toast.makeText(getApplicationContext(), " Exception on dialog ", Toast.LENGTH_SHORT).show();
        }
    }

    DialogInterface.OnClickListener TSdialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.QE.free"));
                    startActivity(i);
                    //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:

                    break;
            }
        }
    };

}