package com.ignitedminds.blockit.services

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.ignitedminds.blockit.constants.Constant
import com.ignitedminds.blockit.data.local.nosql.LockedAppDB
import com.ignitedminds.blockit.data.local.nosql.TimeLimitDB
import com.ignitedminds.blockit.data.local.sharedprefs.SettingsPrefs
import com.ignitedminds.blockit.data.local.sharedprefs.TimekeeperSharedPref
import com.ignitedminds.blockit.data.local.sharedprefs.TodayAppTimeElapsedPref
import com.ignitedminds.blockit.notification.LockNotification
import com.ignitedminds.blockit.ui.LockScreen
import com.ignitedminds.blockit.utils.Utility
import kotlinx.coroutines.*
import java.lang.Runnable

class LockService : Service() {

    private lateinit var job: Job
    private var running = true
    private var prevPackageName = ""
    private var prevEventType = -1
    private var prevTimestamp = System.currentTimeMillis()
    private lateinit var lockedAppDB: LockedAppDB

    private lateinit var timekeeperSharedPref: TimekeeperSharedPref
    private lateinit var todayAppTimeElapsedPref: TodayAppTimeElapsedPref
    private lateinit var settingsPrefs : SettingsPrefs
    private lateinit var timeLimitDB: TimeLimitDB

    private lateinit var handler: Handler
    private var seconds = 0

    override fun onCreate() {
        super.onCreate()
        handler = Handler(Looper.getMainLooper())
        timekeeperSharedPref = TimekeeperSharedPref(this)
        todayAppTimeElapsedPref = TodayAppTimeElapsedPref(this)
        settingsPrefs = SettingsPrefs(this)
        timeLimitDB = TimeLimitDB(this)
        lockedAppDB = LockedAppDB(this)
        job = startJob()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val status = intent?.getBooleanExtra("STATUS", false)
        if (!status!!) {
            stopForeground(true)
            stopSelf()
        } else {
            val lockNotification = LockNotification.createLockNotification()
            startForeground(1, lockNotification)
            Constant.logger("SERVICE STARTED")
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun startJob(): Job {
        val scope = CoroutineScope(Dispatchers.IO)
        return scope.launch {

            val usageStatManager =
                getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

            var startPackageName = ""
            var endPackageName = ""

            var startTimestamp = System.currentTimeMillis()
            var endTimestamp = System.currentTimeMillis()


            while (running) {
                val time = System.currentTimeMillis()
                val stats2 = usageStatManager.queryEvents(time - 2000, time)
                val event = UsageEvents.Event()

                if (timekeeperSharedPref.isNewDay(time)) {
                    timekeeperSharedPref.updateDay(time)
                    todayAppTimeElapsedPref.resetPref()
                    stopTimer()
                    seconds = 0
                }


                while (stats2.hasNextEvent()) {
                    stats2.getNextEvent(event)

                    if (Utility.isSystemPackage(event.packageName))
                        continue

                    if (isPastEvent(event))
                        continue


                    if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {


                        //Checking if the user reached the time limit
                        val timeLimit = timeLimitDB.getTimeLimit(event.packageName)
                        val timeElapsed = todayAppTimeElapsedPref.getTimeElapsed(event.packageName)
                        val isLocked = lockedAppDB.isLocked(event.packageName)

                        Constant.logger("timeElapsed $timeElapsed timeLimit $timeLimit")
                        if (isLocked && timeElapsed >= timeLimit) {
                            val intent = Intent(this@LockService, LockScreen::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        } else if(isLocked) {
                            runTimer(timeLimit - timeElapsed)
                        }


                        if (event.packageName == startPackageName && event.timeStamp < startTimestamp) {
                            startTimestamp = event.timeStamp
                        } else if (event.packageName != startPackageName) {
                            startPackageName = event.packageName
                            startTimestamp = event.timeStamp
                        }

                        prevPackageName = event.packageName
                        prevTimestamp = event.timeStamp
                        prevEventType = UsageEvents.Event.ACTIVITY_RESUMED


                    } else if (event.eventType == UsageEvents.Event.ACTIVITY_PAUSED && event.packageName == startPackageName) {
                        stopTimer()
                        seconds = 0

                        if (event.packageName == endPackageName && event.timeStamp > endTimestamp) {
                            endTimestamp = event.timeStamp
                        } else if (event.packageName != endPackageName) {
                            endPackageName = event.packageName
                            endTimestamp = event.timeStamp
                        }


                        prevPackageName = event.packageName
                        prevTimestamp = event.timeStamp
                        prevEventType = UsageEvents.Event.ACTIVITY_PAUSED

                        val totalTime = ((endTimestamp - startTimestamp) / 1000)

                        //Adding the time spent when app is paused!
                        todayAppTimeElapsedPref.updateTimeElapsedData(event.packageName, totalTime)

                        Constant.logger("${event.timeStamp} $endPackageName Ended at ${totalTime}")
                        startPackageName = ""
                    }

                }

                if (event.packageName == null)
                    continue

                delay(1000)
            }
        }
    }

    private fun runTimer(timeLimit: Long) {


        handler.post(object : Runnable {
            override fun run() {
                seconds++
                if (seconds < timeLimit) {
                    Constant.logger("TimeElapsed $seconds Timelimit $timeLimit")
                    handler.postDelayed(this, 1000)
                } else {
                    val intent = Intent(this@LockService, LockScreen::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
        })
    }

    private fun stopTimer(){
        handler.removeCallbacksAndMessages(null)
    }

    private fun isPastEvent(event: UsageEvents.Event): Boolean {
        return event.packageName == prevPackageName
                && event.timeStamp == prevTimestamp
                && event.eventType == prevEventType
    }

    override fun onDestroy() {
        super.onDestroy()
        settingsPrefs.updateLockerEnabled(false)
        running = false
        GlobalScope.launch {
            job.cancelAndJoin()
        }
    }
}

//Bug for the sessions at 12..
