package com.ignitedminds.blockit.data.local.sharedprefs

import android.content.Context
import android.content.SharedPreferences

class SettingsPrefs(context: Context) {

    private val SETTINGS = "SETTINGS"
    private val LockerEnabled = "LockerEnabled"
    private val isFirstTime = "FirstTime"

    private val sharedPreference: SharedPreferences =
        context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)

    fun isLockerEnabled(): Boolean {
        return sharedPreference.getBoolean(LockerEnabled, false)
    }

    fun updateLockerEnabled(enabled : Boolean){
        sharedPreference.edit().putBoolean(LockerEnabled,enabled).apply()
    }

    fun isFirstTime() : Boolean{
        return sharedPreference.getBoolean(isFirstTime, true)
    }

    fun setFirstTime(){
        sharedPreference.edit().putBoolean(isFirstTime,false).apply()
    }

    fun resetPrefs(){
        sharedPreference.edit().clear().apply()
    }
}