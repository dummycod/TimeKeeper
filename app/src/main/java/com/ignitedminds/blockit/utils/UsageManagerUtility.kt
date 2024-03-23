package com.ignitedminds.blockit.utils

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.ignitedminds.blockit.constants.Constant
import com.ignitedminds.blockit.utils.TimeUtility.Companion.startOfHour
import com.ignitedminds.blockit.utils.TimeUtility.Companion.timestampTo12hr
import com.ignitedminds.blockit.utils.DataStructures.Companion.AppIntervalDuration
import com.ignitedminds.blockit.utils.TimeUtility.Companion.getEndOfHour
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UsageManagerUtility(private val context : Context, private val startTime : Long, private val endTime: Long) {

   // val usageData: HashMap<String, LinkedHashMap<String, Long>> = LinkedHashMap()
    val appIntervalData = AppIntervalDuration()

    init {
        getAllAppsIntervalsAndDurations()
    }

    private fun getAllAppsIntervalsAndDurations() {

        val mUsageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val eventStats =
            mUsageStatsManager.queryEvents(startTime, endTime)

        var mStartTime = 0L
        var mEndTime = 0L
        var packageName = ""

        while (eventStats.hasNextEvent()) {

            val event = UsageEvents.Event()
            eventStats.getNextEvent(event)

            if (Utility.isSystemPackage(event.packageName)||event.packageName.isEmpty())
                continue

            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                mStartTime = event.timeStamp
                mEndTime =0L
                packageName = event.packageName
            } else if (event.eventType == UsageEvents.Event.ACTIVITY_PAUSED) {
                mEndTime = event.timeStamp

                if(mStartTime==0L)
                    mStartTime = startOfHour(mEndTime)

                val intervalDurations = getIntervalsAndDurations(mStartTime, mEndTime)
                appIntervalData.addMultipleIntervalData(event.packageName,intervalDurations)
                //addAppsIntervalsAndDurationsToStore(event.packageName, durations)
            }
        }

        //Yet To Be Tested...
        if(mEndTime==0L&&packageName.isNotEmpty()){
            mEndTime = getEndOfHour(mStartTime)
            val intervalDurations = getIntervalsAndDurations(mStartTime, mEndTime)
            appIntervalData.addMultipleIntervalData(packageName,intervalDurations)
        }

        appIntervalData.addPackagesWithNoUsage()
    }


    private fun getIntervalsAndDurations(startTime: Long, endTime: Long): List<Pair<String, Long>> {
        val intervalDurationList = ArrayList<Pair<String, Long>>()
        var mStartTime = startTime
        var mEndTime = endTime

        while (TimeUtility.isNextHour(mStartTime, mEndTime)) {
            mEndTime = getEndOfHour(mStartTime)
            val intervalDurationObj = getIntervalDurationObj(mStartTime, mEndTime)
            intervalDurationList.add(intervalDurationObj)
            mStartTime = mEndTime + 1
            mEndTime = endTime
        }

        val intervalDuration = getIntervalDurationObj(mStartTime, mEndTime)
        intervalDurationList.add(intervalDuration)

        return intervalDurationList
    }

    private fun getIntervalDurationObj(startTime: Long, endTime: Long): Pair<String, Long> {
        val usageTime = endTime - startTime
        return Pair(timestampTo12hr(startTime), usageTime)
    }

}