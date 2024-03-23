package com.ignitedminds.blockit.listeners

interface LockedStatusListener {
    fun onLockedStatusChanged(packageName : String,isLocked : Boolean)
}