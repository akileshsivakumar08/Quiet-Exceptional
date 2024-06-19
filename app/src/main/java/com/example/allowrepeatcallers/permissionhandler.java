package com.example.allowrepeatcallers;

import android.Manifest;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class permissionhandler {
    private static final String MEMCODE_REQUESTEDPERMISSIONS="MAP_REQUESTEDPERMISSIONS";
    HashMap<String,Boolean> requestedPermissions = new HashMap<String, Boolean>();

    public permissionhandler(Context context) {
        //loadhashmap from memory
        String loadedmap= utilityHelpers.loadStringFromMemory(context,MEMCODE_REQUESTEDPERMISSIONS);
        if(loadedmap.equals("null")){
            boolean initState=false;
            requestedPermissions.put(Manifest.permission.READ_PHONE_STATE, initState);
            requestedPermissions.put(Manifest.permission.READ_CONTACTS, initState);
            requestedPermissions.put(Manifest.permission.READ_CALL_LOG, initState);
            requestedPermissions.put(Manifest.permission.POST_NOTIFICATIONS, initState);
            requestedPermissions.put(Manifest.permission.READ_SMS, initState);
            saveToMemory(context,requestedPermissions);
        }else {
            requestedPermissions = utilityHelpers.convertStringToPermissionHashMap(loadedmap);
        }
    }
    private void saveToMemory(Context context,HashMap<String,Boolean> requestedPermissions){
        String String_requestedPermissions=utilityHelpers.convertHashMapToString(context,requestedPermissions);
        utilityHelpers.saveStringToMemory(context,MEMCODE_REQUESTEDPERMISSIONS,String_requestedPermissions);
    }
    private boolean isPermissionAlreadyRequested(String permission2check){
        boolean isrequested= requestedPermissions.getOrDefault(permission2check,false);
        return isrequested;
    }
    public boolean werePermissionsRequested(String[] permission2check){
        Boolean is_already_requested=true;
        for(int i=0;i<permission2check.length;i++){
            is_already_requested =isPermissionAlreadyRequested(permission2check[i]);
            if(is_already_requested){
                break;
            }
            else{
            }
        }
        return is_already_requested;
    }

    public void setPermissionRequested(Context context,Map<String,Boolean> permissionRequested){
        for (Map.Entry<String, Boolean> iterator : permissionRequested.entrySet()){
            String Value=iterator.getKey();
            Boolean state= true;
            requestedPermissions.put(Value,state);
        }
        saveToMemory(context,requestedPermissions);
    }


}
