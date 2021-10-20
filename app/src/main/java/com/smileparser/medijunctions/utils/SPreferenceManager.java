package com.smileparser.medijunctions.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.smileparser.medijunctions.R;

import static android.content.Context.MODE_PRIVATE;

public class SPreferenceManager {

    public static final String PREF_USERID="userid";
    public static final String PREF_TOKEN="token";
    public static final String PREF_USERDETAILS = "UserDetails";
    public static final String PREF_DEVICEID="deviceid";
    static final String PREF_GOOGLE_TOKEN="googletoken";
    static final String PREF_USERDATA="userdata";
    public static final String KEY_UPDATE_AVAILABLE_TIME = "key_update_available_time";

    private static SPreferenceManager sInstance;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private SPreferenceManager(Context context) {
        mPreferences = context.getSharedPreferences(context.getString(R.string.app_name),MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static SPreferenceManager getInstance(Context context) {
        if (sInstance == null) {

            synchronized (SPreferenceManager.class) {
                if (sInstance == null) {
                    sInstance = new SPreferenceManager(context);
                }
            }

        }

        return sInstance;
    }

    public void clear() {
        //Set<String> s = getTutorialList();
        mEditor.clear();
        mEditor.commit();
        //saveTutorial(s);
    }

    public String getStringValue(String key, String defaultVal) {
        return mPreferences.getString(key, defaultVal);
    }

    public String getStringValue(String key) {
        return mPreferences.getString(key, "");
    }

    public void setStringValue(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public void setIntValue(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public int getIntDefaultValue(String key, int defValue) {
        return mPreferences.getInt(key, defValue);
    }

    public double getIntValue(String key) {
        return mPreferences.getInt(key, 0);
    }

    public boolean getBooleanValue(String key) {
        return mPreferences.getBoolean(key, false);
    }

    public boolean getBooleanDefaultValue(String key, boolean defValue) {
        return mPreferences.getBoolean(key, defValue);
    }

    public void setBooleanValue(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public void setLongValue(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    public long getLongValue(String key, long defVal) {
        return mPreferences.getLong(key, defVal);
    }

    public void  logOut()
    {
        mEditor.remove(PREF_USERDETAILS);
        mEditor.commit();
    }


}
