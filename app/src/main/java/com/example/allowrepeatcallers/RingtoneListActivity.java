package com.example.allowrepeatcallers;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.quietexceptional.R;

import java.util.ArrayList;

public class RingtoneListActivity extends Activity {
    Uri selectedRingtone;
    Boolean changesmade=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone_list);

        ListView ringtoneListView = findViewById(R.id.ringtoneListView);
        Button saveButton=findViewById(R.id.saveButton);

        // Retrieve the list of ringtones
        ArrayList<String> ringtoneTitles = new ArrayList<>();
        ArrayList<Uri> ringtoneUris = new ArrayList<>();
        RingtoneManager ringtoneManager = new RingtoneManager(this);
        ringtoneManager.setType(RingtoneManager.TYPE_NOTIFICATION);

        // Get cursor for available ringtones
        android.database.Cursor cursor = ringtoneManager.getCursor();

        // Iterate through the ringtones and retrieve their titles and URIs
        while (cursor.moveToNext()) {
            // Get the ringtone URI
            Uri ringtoneUri = ringtoneManager.getRingtoneUri(cursor.getPosition());
            ringtoneUris.add(ringtoneUri);

            // Get the ringtone title
            Ringtone ringtone = ringtoneManager.getRingtone(cursor.getPosition());
            String title = ringtone.getTitle(this);
            ringtoneTitles.add(title);
        }

        // Set up the ListView to display the ringtone titles
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ringtoneTitles) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                // Reset the background color of the rows to default when not selected
                view.setBackgroundColor(Color.WHITE);

                return view;
            }
        };
        ringtoneListView.setAdapter(adapter);

        // Optionally, handle clicks on list items to play the selected ringtone
        ringtoneListView.setOnItemClickListener((parent, view, position, id) -> {
            changesmade=true;
            for (int i = 0; i < parent.getChildCount(); i++) {
                parent.getChildAt(i).setBackgroundColor(Color.WHITE);
            }
            // Play the selected ringtone
            selectedRingtone =  ringtoneUris.get(position);
            MediaPlayer mp_local;
            mp_local = MediaPlayer.create(getApplicationContext(), ringtoneUris.get(position));
            mp_local.start();
            view.setBackgroundColor(Color.parseColor("#DDE1ED"));
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savenExit();

            }
        });

    }
    @Override
    public void onBackPressed() {
if(changesmade) {
    postQuestionDialog(getString(R.string.exit_without_saving));
}
else{
    finish();
}
        //
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
    private void savenExit(){
        Intent resultIntent = new Intent();
        ArrayList<String> selectedRingtone_list=new ArrayList<>();
        selectedRingtone_list.add(selectedRingtone.toString());
        resultIntent.putStringArrayListExtra("outputURI", selectedRingtone_list);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}