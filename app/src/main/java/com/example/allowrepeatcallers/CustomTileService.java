package com.example.allowrepeatcallers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class CustomTileService extends TileService {
    Tile tile;
    BroadcastReceiver workOverBroadcast;
    feat_tempdnd obj_tempDND;
    @Override
    public void onClick() {

        Context context=getApplicationContext();
        obj_tempDND=new feat_tempdnd(context);
        // Handle click action
        tile = getQsTile();
        if (tile != null) {
            Intent intent = new Intent("com.example.allowrepeatcallers.TIMERWORK_OVER");
            // Change tile state (active/inactive)
            if(tile.getState()==Tile.STATE_ACTIVE){
                tile.setState(Tile.STATE_INACTIVE);
                obj_tempDND.setFeatureActivated(context,false);
                obj_tempDND.stoptempdnd(context);
                intent.putExtra("TDND_State","STOP_CUSTOMTILETAP");
                context.sendBroadcast(intent);
            }
            else{
                tile.setState(Tile.STATE_ACTIVE);
                obj_tempDND.setFeatureActivated(context,true);
                obj_tempDND.starttempDND(context);
                intent.putExtra("TDND_State","START_CUSTOMTILETAP");
                context.sendBroadcast(intent);
            }
            tile.updateTile();
            // Perform your action here
            // Example: Open an activity, trigger an action, etc.
        }

    }
    public void updateTileState(boolean isActive) {
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setState(isActive ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            tile.updateTile();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.allowrepeatcallers.TIMERWORK_OVER");
        getApplicationContext().registerReceiver(workOverBroadcast, intentFilter, Context.RECEIVER_EXPORTED);
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        workOverBroadcast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String enable=intent.getStringExtra("TDND_State");
                if(enable.equals("STOP_WORKOVER")){
                    tile.setState(Tile.STATE_INACTIVE);
                    obj_tempDND.setFeatureActivated(context,false);
                }
                else{
                    tile.setState(Tile.STATE_ACTIVE);
                    obj_tempDND.setFeatureActivated(context,true);
                }
                // Toast.makeText(context, "SMS SENT!!", Toast.LENGTH_SHORT).show();

            }
        };
        // Optional: Update the tile state when the tile becomes visible
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        // Optional: Clean up resources if needed when the tile is no longer visible
    }
    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("isActive")) {
            boolean isActive = intent.getBooleanExtra("isActive", false);
            updateTileState(isActive);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
