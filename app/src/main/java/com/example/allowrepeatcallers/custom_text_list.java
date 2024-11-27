package com.example.allowrepeatcallers;


import static android.content.ContentValues.TAG;
import static com.example.quietexceptional.R.menu.popupmenu_ctl_list;
import static com.example.quietexceptional.R.menu.popupmenu_loadedlist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quietexceptional.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class custom_text_list extends AppCompatActivity {

    ListView listview;
    TextView textView_emptylist;
    String featureIdentifier;
    ArrayList<String> CustomTextList;
    ArrayList<class_Buddy> diffList;
    Context context;
    Boolean changesmade=false;

    @Override
    protected void onResume() {
        super.onResume();
        if(CustomTextList.size()==0){
            textView_emptylist.setVisibility(View.VISIBLE);
            textView_emptylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createDialog(context);
                }
            });
        }
        else{
            textView_emptylist.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getApplicationContext();
        getSupportActionBar().setTitle( "Custom Text");
        ArrayList<String> CustomTextListIP = getIntent().getStringArrayListExtra("CustomTextList_Parcel");
        CustomTextList=new ArrayList<String>();
        setContentView(R.layout.custom_text_list);
        textView_emptylist=findViewById(R.id.textView_emptylist);
        if(CustomTextListIP!=null){
            CustomTextList=CustomTextListIP;
        }
        if(CustomTextList.size()==0){
            textView_emptylist.setVisibility(View.VISIBLE);
            textView_emptylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createDialog(context);
                }
            });
        }
        else{
            textView_emptylist.setVisibility(View.GONE);
        }

        featureIdentifier = getIntent().getStringExtra("FEATURE_IDENTIFIER");
        refreshlistview(CustomTextList);

        FloatingActionButton AddButton = findViewById(R.id.add_fab);
        Button saveButton=findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savenExit();
            }
        });
        AddButton.setOnClickListener(view -> {

            createDialog(context);
        });

        listview.setOnItemClickListener((adapterView, view, i, l) -> popupmenu(i,featureIdentifier));

    }

    private void popupmenu(int i,String featureIdentifier) {
        PopupMenu popupmenu = new PopupMenu(getApplicationContext(),listview);

        popupmenu.getMenuInflater().inflate(popupmenu_ctl_list,popupmenu.getMenu());

        popupmenu.show();


        popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.delete) {
                    CustomTextList.remove(i);
                    refreshlistview(CustomTextList);
                }
                changesmade=true;
                return false;
            }
        });
    }

    private int findFavourite(ArrayList<class_Buddy> contactsList) {
        for(int i=0;i<contactsList.size();i++){
            if(contactsList.get(i).isFavourite()){
                return i;
            }
        }
        return  255;
    }

    private void refreshlistview(ArrayList<String> CustomTextList){

        listview = (ListView) findViewById(R.id.listview);
        if(CustomTextList!=null) {
            CTL_Adapter ctl_adapter = new CTL_Adapter(this, R.layout.ctl_row, CustomTextList);

            listview.setAdapter(ctl_adapter);
        }
        //setContentView(listview);
    }

    @Override
    public void onBackPressed() {

        if(changesmade) {
            postQuestionDialog("Exit Without Saving?");
        }
        else{
            super.onBackPressed();
        }
       //
    }
    private void postQuestionDialog(String description) {
        try {
            //start a dialog box
            AlertDialog.Builder TS_alertbuilder = new AlertDialog.Builder(this,R.style.AlertDialogStyle);
            TS_alertbuilder.setMessage(description).setPositiveButton(R.string.Exit, TSdialogListener).setNegativeButton(R.string.SavenExit, TSdialogListener);
            AlertDialog alertDialog = TS_alertbuilder.create();
            alertDialog.show();
        } catch (Exception e) {

            Log.e(TAG, " Exception on dialog  " + e);
            Toast.makeText(getApplicationContext(), " Exception on dialog ", Toast.LENGTH_SHORT).show();
        }
    }

    private void createDialog(Context context) {
        try {
            //start a dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(custom_text_list.this,R.style.AlertDialogStyle);
            final EditText textInput=new EditText(context);
            builder.setTitle("Enter Custom Text");
            builder.setMessage("Device pings upon detecting this text in a notification");
            textInput.setInputType(InputType.TYPE_CLASS_TEXT);
            String HintText="Enter text here";
            textInput.setHint(HintText);
            builder.setView(textInput);
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String input= textInput.getText().toString();
                    if(!input.isEmpty()) {
                        CustomTextList.add(textInput.getText().toString());
                        textView_emptylist.setVisibility(View.GONE);
                        refreshlistview(CustomTextList);
                    }
                    else{
                        Toast.makeText(context, " Text is empty ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel_menu, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            String ErrorFlow=" Settings_configure_Exception on dialog  ";
            utilityHelpers.saveErrorToMemory(context,ErrorFlow);
            Log.e(TAG, " Exception on dialog  " + e);
            Toast.makeText(context, " Exception on dialog ", Toast.LENGTH_SHORT).show();
        }
    }

    DialogInterface.OnClickListener TSdialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    savenExit();
                    break;
            }
        }
    };
    private void savenExit(){
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("outputList", CustomTextList);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

}


