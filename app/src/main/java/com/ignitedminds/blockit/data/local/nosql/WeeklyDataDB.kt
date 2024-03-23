package com.ignitedminds.blockit.data.local.nosql

import android.content.Context
import com.ignitedminds.blockit.utils.DataStructures.Companion.AppIntervalDuration
import com.ignitedminds.blockit.utils.DataStructures.Companion.DayStartEnd
import com.ignitedminds.blockit.utils.JavaUtility
import com.ignitedminds.blockit.utils.TimeUtility.Companion.getDay
import com.ignitedminds.blockit.utils.UsageManagerUtility
import com.snappydb.DBFactory

//Will Contain Last 6 Days Data at a time
class WeeklyDataDB(private val context: Context) {
    private val weeklyData = "WEEKLY_DATA"

    fun initializeDBWithWeekData() {
        val weeklyDB = DBFactory.open(context, weeklyData)
        weeklyDB.destroy()
        var currentDay = DayStartEnd(System.currentTimeMillis())

        for (day in (0..5)) {
            val yesterday = currentDay.toYesterday()
            val appIntervalData =
                UsageManagerUtility(context, yesterday.startTime, yesterday.endTime).appIntervalData

            putNewDayData(yesterday.startTime,appIntervalData)

            currentDay = yesterday
        }
    }

    fun getSinglePackageWeeklyChartData(packageName: String): LinkedHashMap<String, Long> {
        val weeklyDuration = LinkedHashMap<String, Long>()
        val weeklyDB = DBFactory.open(context, weeklyData)
        val startOfWeekdays = weeklyDB.findKeys("0").map { it.toLong() }
        val sortedStartOfWeekdays = startOfWeekdays.sorted()
        for (day in sortedStartOfWeekdays) {
            val appIntervalData = weeklyDB.getObject("0$day", AppIntervalDuration::class.java)
            val dayOfTheWeek = getDay(day)
            weeklyDuration[dayOfTheWeek] =
                appIntervalData.getSinglePackageTotalDuration(packageName)
        }
        weeklyDB.close()
        return weeklyDuration
    }


    fun getTop3TimeKillers(): List<String> {
        val allAppsWeekDuration = getAllAppsWeeklyDuration()
        val sortedKeys = JavaUtility.sortByValue(allAppsWeekDuration).keys.toTypedArray()
        val size = sortedKeys.size
        return if (size >= 3) {
            listOf(sortedKeys[0], sortedKeys[1], sortedKeys[2])
        } else if (size == 2)
            listOf(sortedKeys[0], sortedKeys[1])
        else if (size == 1)
            listOf(sortedKeys[0])
        else
            listOf()
    }

    private fun putNewDayData(startWeekTimeStamp: Long, appIntervalData: AppIntervalDuration) {
        removeOutdatedData()
        val weeklyDB = DBFactory.open(context, weeklyData)
        weeklyDB.put("0$startWeekTimeStamp", appIntervalData)
        weeklyDB.close()
    }

    private fun removeOutdatedData() {
        val weeklyDB = DBFactory.open(context, weeklyData)
        val startOfWeeks = weeklyDB.findKeys("0").map { it.toLong() }
        if (startOfWeeks.size < 6) {
            weeklyDB.close()
            return
        }
        val minimumValue = startOfWeeks.minOrNull() ?: return
        weeklyDB.del("0$minimumValue")
        weeklyDB.close()
    }


    private fun getAllAppsWeeklyDuration(): HashMap<String, Long> {
        var result = HashMap<String, Long>()
        val weeklyDB = DBFactory.open(context, weeklyData)
        val daysOfWeekTimestamps = weeklyDB.findKeys("0").map { it.toLong() }
        for (timestamp in daysOfWeekTimestamps) {
            val appIntervalData = weeklyDB.getObject("0$timestamp", AppIntervalDuration::class.java)
            result = appIntervalData.addAppWeeklyDataToMap(result)
        }
        weeklyDB.close()
        return result
    }

}