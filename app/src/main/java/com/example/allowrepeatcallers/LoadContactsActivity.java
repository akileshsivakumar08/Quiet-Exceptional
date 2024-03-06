package com.example.allowrepeatcallers;


import static com.example.allowrepeatcallers.R.menu.popupmenu_loadedlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class LoadContactsActivity extends AppCompatActivity {

    ListView listview;
    feat_PrioContacts obj_LoadContacts;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getApplicationContext();
        getSupportActionBar().setTitle( "Your Contacts");
        obj_LoadContacts=new feat_PrioContacts(context);
        setContentView(R.layout.activity_load_contacts);
        refreshlistview(obj_LoadContacts.getSilExceptList());
        FloatingActionButton AddButton = findViewById(R.id.add_fab);

        AddButton.setOnClickListener(view -> {

            Intent in = new Intent (Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            getResult.launch(in);
        });

        listview.setOnItemClickListener((adapterView, view, i, l) -> popupmenu(i));


    }

    private void popupmenu(int i) {
        PopupMenu popupmenu = new PopupMenu(getApplicationContext(),listview);
        popupmenu.getMenuInflater().inflate(popupmenu_loadedlist,popupmenu.getMenu());
        popupmenu.show();
        popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                obj_LoadContacts.deletebuddy(i,context);
                refreshlistview(obj_LoadContacts.getSilExceptList());
                return false;
            }
        });
    }

    private void refreshlistview(ArrayList<class_Buddy> obj_BuddyLocal){

        listview = (ListView) findViewById(R.id.listview);
        PersonAdapter personAdapter = new PersonAdapter(this, R.layout.list_row, obj_BuddyLocal);

        listview.setAdapter(personAdapter);
        //setContentView(listview);
    }
    private class_Buddy contactPicked(Intent data) {
        Cursor cursor;
        class_Buddy buddy = null;
        try {
            String phoneNo;
            Uri uri = data.getData ();
            //cursor = getContentResolver ().query (uri, null, null,null,null);
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
            cursor = getContentResolver().query(uri, projection, null, null, null);
            boolean isIt=cursor.moveToFirst ();
            int phoneIndex = cursor.getColumnIndex (ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex= (cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String msg="Emergency";
            phoneNo = cursor.getString (phoneIndex);
            phoneNo=phoneNo.replaceAll("[^0-9]", "");
            String disp_name=cursor.getString(nameIndex);
            buddy = new class_Buddy(disp_name,phoneNo,msg);

        } catch (Exception e) {
            e.printStackTrace ();

        }
        return buddy;
    }

    ActivityResultLauncher<Intent> getResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                int resultCode=result.getResultCode();
                if (resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    storeData(data);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Failed To pick contact", Toast.LENGTH_SHORT).show();
                }
            });

    private void storeData(Intent data) {
        class_Buddy buddy;
        buddy=contactPicked(data);

        boolean check=obj_LoadContacts.isnumberinList(buddy.getBuddy_PhNo());
        if (!check) {
            obj_LoadContacts.addToPrioContactsList(buddy,context);

            refreshlistview(obj_LoadContacts.getSilExceptList());
        }
        else{
            Toast.makeText(this, "Contact already exists", Toast.LENGTH_SHORT).show();
        }
    }


}


