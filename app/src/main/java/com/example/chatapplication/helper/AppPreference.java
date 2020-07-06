package com.example.chatapplication.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AppPreference {
    private SharedPreferences mPreferences;
    private String PREF_NAME = "chatPref";
    private String KEY_EMAIL = "email";
    private String KEY_UID = "uid";



    private SharedPreferences.Editor editor;

    public AppPreference(Context mContext){
        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = mPreferences.edit();
    }

    public void setEmail(String email){
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public String getEmail(){
        return  mPreferences.getString(KEY_EMAIL, null);
    }


    public String getKEY_UID() {
        return  mPreferences.getString(KEY_UID, null);
    }

    public void setKEY_UID(String key) {
        editor.putString(KEY_UID, key);
        editor.commit();
    }

}
