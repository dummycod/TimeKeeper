package com.ignitedminds.blockit.data

import android.content.Context
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.application.App
import com.ignitedminds.blockit.constants.Constant
import com.ignitedminds.blockit.data.local.nosql.WeeklyDataDB
import com.ignitedminds.blockit.data.uibinding.AppNameIcon
import com.ignitedminds.blockit.utils.TimeUtility
import com.ignitedminds.blockit.utils.UsageManagerUtility
import com.ignitedminds.blockit.utils.DataStructures.Companion.AppIntervalDuration
import com.ignitedminds.blockit.utils.DataStructures.Companion.AppCombinedData
import com.ignitedminds.blockit.utils.Utility

class UsageDataStore(private val context: Context) {

    private val combinedUsageToday = AppCombinedData()
    private lateinit var appIntervalDataToday: AppIntervalDuration


    fun getTotalDurationForToday() : String{
        return combinedUsageToday.getTotalTimeForToday(appIntervalDataToday)
    }


    //What if no 3 apps??? Case To Be Covered!
    fun getTop3Apps(): List<AppNameIcon> {
        val weeklyDB = WeeklyDataDB(context)
        val appNameIconList = mutableListOf<AppNameIcon>()
        val packageList = weeklyDB.getTop3TimeKillers()
        var count = 1
        for (packageName in packageList) {
            val appName = Utility.getApplicationName(packageName)
            val name = "#$count " + if (appName.length > 14) "${
                appName.subSequence(
                    0,
                    14
                )
            }..." else appName
            val icon = Utility.getApplicationIcon(packageName)
            val appNameIcon = AppNameIcon(name, icon)
            appNameIconList.add(appNameIcon)
            count++
        }

        while (appNameIconList.size < 3) {
            val noAppNameIcon = AppNameIcon(
                " #$count No App",
                App.Companion.appContext.resources.getDrawable(R.drawable.no_image)
            )
            appNameIconList.add(noAppNameIcon)
            count++
        }
        return appNameIconList
    }

    fun getTodayData(): LinkedHashMap<String, String> {
        return combinedUsageToday.appCombinedData
    }

    fun getTodaySessionDataOfApplication(packageName: String): List<String> {
        return appIntervalDataToday.getSinglePackageReadableSession(packageName)
    }

    fun getSinglePackageWeeklyData(packageName: String): LinkedHashMap<String, Long> {
        val weeklyDB = WeeklyDataDB(context)
        return weeklyDB.getSinglePackageWeeklyChartData(packageName)
    }

    fun initializeWeeklyUsageData() {
        val weeklyDB = WeeklyDataDB(context)
        weeklyDB.initializeDBWithWeekData()
    }

    fun initializeTodayUsageData() {
        initializeAppIntervalDataToday()
        initializeAppCombinedDataToday()
        Constant.logger("Here")
    }

    // For Displaying Data in List.
    private fun initializeAppCombinedDataToday() {
        initializeAppIntervalDataToday()
        combinedUsageToday.toReadable(appIntervalDataToday)
    }

    //For the session Record
    private fun initializeAppIntervalDataToday() {
        val startTime = TimeUtility.startOfTheDay()
        val endTime = System.currentTimeMillis()
        val usageManagerUtility = UsageManagerUtility(context, startTime, endTime)
        //usageManagerUtility.appIntervalData.addPackagesWithNoUsage()
        appIntervalDataToday = usageManagerUtility.appIntervalData
    }
}