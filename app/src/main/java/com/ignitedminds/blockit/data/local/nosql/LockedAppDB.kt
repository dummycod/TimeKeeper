package com.ignitedminds.blockit.data.local.nosql

import android.content.Context
import com.snappydb.DBFactory
import java.lang.Exception

class LockedAppDB(private val context: Context) {

    private val APP_LOCKER = "APPLOCKER"

    fun lockApplication(packageName: String){
        val lockDB = DBFactory.open(context,APP_LOCKER)
        lockDB.putInt(packageName,0)
        lockDB.close()
    }

    fun isLocked(packageName: String) : Boolean{
        val lockDB = DBFactory.open(context,APP_LOCKER)
        return try {
            lockDB.get(packageName)
            true
        }catch (e: Exception){
            false
        }finally {
            lockDB.close()
        }
    }

    fun unlockApplication(packageName: String){
        val lockDB = DBFactory.open(context,APP_LOCKER)
        lockDB.del(packageName)
        lockDB.close()
    }

    fun getAllLockedApplications() : ArrayList<String>{
        val lockDB = DBFactory.open(context,APP_LOCKER)
        val arrayList  = lockDB.findKeys("com")
        val res = ArrayList<String>()
        for(v in arrayList){
            res.add(v)
        }
        lockDB.close()
        return res
    }
}