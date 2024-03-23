package com.ignitedminds.blockit.data.local.sharedprefs

import android.content.Context
import android.content.SharedPreferences

class TodayAppTimeElapsedPref(context: Context) {
    private val TODAY_USAGE_TIME = "TIME_KEEPER"

    private val sharedPreference: SharedPreferences =
        context.getSharedPreferences(TODAY_USAGE_TIME, Context.MODE_PRIVATE)

    fun setTimeElapsed(packageName : String,timeElapsed : Long){
        sharedPreference.edit().putLong(packageName,timeElapsed).apply()
    }

    fun updateTimeElapsedData(packageName : String,timeElapsed : Long){
        var time = sharedPreference.getLong(packageName, 0)
        time+=timeElapsed
        sharedPreference.edit().putLong(packageName,time).apply()
    }

    fun getTimeElapsed(packageName: String) : Long{
        val time = sharedPreference.getLong(packageName, 0)
        return time
    }

    fun resetPref(){
        sharedPreference.edit().clear().apply()
    }
}