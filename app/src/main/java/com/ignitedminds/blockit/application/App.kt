package com.ignitedminds.blockit.application

import android.app.Application
import android.content.Context
import com.ignitedminds.blockit.notification.LockNotification

class App : Application() {

    companion object{
        lateinit var appContext : Context
        lateinit var application: Application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        appContext = this
        LockNotification.createNotificationChannel()
    }
}