package com.ignitedminds.blockit.data.local.nosql

import android.content.Context
import com.snappydb.DBFactory
import java.lang.Exception

//TODO: Convert This To Shared Preference!

class TimeLimitDB(private val context : Context) {

    private val TIME_LIMIT = "TIMELIMIT"

    fun setTimeLimit(packageName : String, timeInMinutes : Long){
        val timeLimitDB = DBFactory.open(context,TIME_LIMIT)
        timeLimitDB.putLong(packageName,timeInMinutes)
        timeLimitDB.close()
    }

    fun removeLimit(packageName: String){
        val timeLimitDB = DBFactory.open(context,TIME_LIMIT)
        timeLimitDB.del(packageName)
        timeLimitDB.close()
    }

    fun getTimeLimit(packageName: String) : Long{
        val timeLimitDB = DBFactory.open(context,TIME_LIMIT)
        return try {
            timeLimitDB.getLong(packageName)
        }catch (e: Exception){
            0
        }finally {
            timeLimitDB.close()
        }
    }
}