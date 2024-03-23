package com.ignitedminds.blockit.data.local.sharedprefs

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.ignitedminds.blockit.utils.TimeUtility

class TimekeeperSharedPref(context: Context) {

    private val TIME_KEEPER = "TIME_KEEPER"
    private val TODAY_TIMESTAMP = "TODAY_TIMESTAMP"

    private val sharedPreference: SharedPreferences =
        context.getSharedPreferences(TIME_KEEPER, MODE_PRIVATE)

    private fun getLastSavedTime(): Long {
        val time = sharedPreference.getLong(TODAY_TIMESTAMP, 0)
        return time
    }

    fun isNewDay(timestamp: Long) : Boolean{
        val startOfDayTimestamp = TimeUtility.startOfTheDay(timestamp)
        return startOfDayTimestamp > getLastSavedTime()
    }

    fun updateDay(timestamp : Long){
        val startOfDayTimestamp = TimeUtility.startOfTheDay(timestamp)
        sharedPreference.edit().putLong(TODAY_TIMESTAMP,startOfDayTimestamp).apply()
    }
}