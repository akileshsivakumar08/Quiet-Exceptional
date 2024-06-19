package com.example.allowrepeatcallers;

import android.Manifest;
import android.content.Context;

import java.util.HashMap;
import java.util.Locale;

public class LanguageManager {
    private static final String MEMCODE_CUSTOMTEXT_MISSEDCALL = "MAP_CUSTOMTEXT_MISSEDCALL";
    public static HashMap<String,String> loadedLanguages_missedCallText_map= new HashMap<String, String>();
    private static final String MEMCODE_LANGUAGES_MISSEDCALL="MAP_LANGUAGES_MISSEDCALL";

    public static String getMissedCallText(Context context){
        String languageID=Locale.getDefault().getLanguage();
        loadedLanguages_missedCallText_map=getLanguageMap(context);
        return loadedLanguages_missedCallText_map.getOrDefault(languageID,"NotFound");

    }

    private static HashMap<String, String> getLanguageMap(Context context) {
        initializeLanguageMap(context);
        return loadedLanguages_missedCallText_map;
    }

    private static void initializeLanguageMap(Context context) {
        String loadedmap= utilityHelpers.loadStringFromMemory(context,MEMCODE_LANGUAGES_MISSEDCALL);
        if(loadedmap.equals("null")){
            loadedLanguages_missedCallText_map.put("en","Missed call");
            loadedLanguages_missedCallText_map.put("de","Entgangener Anruf");
        }else {
            loadedLanguages_missedCallText_map = utilityHelpers.convertStringToLanguageHashMap(loadedmap);
        }
    }
    public static String getCustomText(Context context){
        return utilityHelpers.loadStringFromMemory(context,MEMCODE_CUSTOMTEXT_MISSEDCALL);
    }
    public static void setCustomText(Context context,String customText){
        utilityHelpers.saveStringToMemory(context,MEMCODE_CUSTOMTEXT_MISSEDCALL,customText);
    }

}
