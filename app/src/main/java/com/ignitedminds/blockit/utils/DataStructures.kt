package com.ignitedminds.blockit.utils

import com.ignitedminds.blockit.application.App
import com.ignitedminds.blockit.data.local.sharedprefs.TodayAppTimeElapsedPref
import com.ignitedminds.blockit.utils.TimeUtility.Companion.endOfTheDay
import com.ignitedminds.blockit.utils.TimeUtility.Companion.startOfTheDay
import com.ignitedminds.blockit.utils.TimeUtility.Companion.yesterdayTimestamp

class DataStructures {

    companion object {
        //[PackageName->[Time1->Duration1,Time2->Duration2]]
        class AppIntervalDuration {
            private val appIntervalDuration = HashMap<String, LinkedHashMap<String, Long>>()

            private fun addSingleIntervalData(
                packageName: String,
                time12HrInterval: String,
                duration: Long
            ) {
                if (appIntervalDuration[packageName] == null) {
                    appIntervalDuration[packageName] = LinkedHashMap()
                }
                val interval = appIntervalDuration[packageName]!!

                if (interval[time12HrInterval] == null)
                    interval[time12HrInterval] = 0L
                interval[time12HrInterval] = interval[time12HrInterval]?.plus(duration)!!
            }

            fun addMultipleIntervalData(
                packageName: String,
                intervalsDurations: List<Pair<String, Long>>
            ) {
                for ((time12HrInterval, duration) in intervalsDurations) {
                    addSingleIntervalData(packageName, time12HrInterval, duration)
                }
            }

            fun getAllPackageAndTotalDuration(): LinkedHashMap<String, Long> {
                var result = LinkedHashMap<String, Long>()

                for ((packageName, intervalDuration) in appIntervalDuration) {
                    var totalDuration = 0L
                    for ((interval, duration) in intervalDuration) {
                        totalDuration += duration
                    }
                    result[packageName] = totalDuration
                }
                result = JavaUtility.sortByValue(result)
                return result
            }

            fun getSinglePackageTotalDuration(packageName: String): Long {
                val intervalDurations = appIntervalDuration[packageName] ?: LinkedHashMap()
                var totalDuration = 0L
                for ((interval, duration) in intervalDurations) {
                    totalDuration += duration
                }
                return totalDuration
            }

            fun getSinglePackageReadableSession(packageName : String) : List<String>{
               val appSessions = appIntervalDuration[packageName]!!
                val sessionsData= mutableListOf<String>()
                for((time, duration) in appSessions){
                    if(time=="12AM"&&duration==0L)
                        continue
                    val data = "$time : ${TimeUtility.millisecondsToTime(duration)}"
                    sessionsData.add(data)
                }
                return sessionsData
            }

            private fun getAllPackagesWithUsage() : List<String>{
                val packages = ArrayList<String>()
                val allPackages = appIntervalDuration.keys.toList()
                for(pack in allPackages){
                    if(appIntervalDuration[pack]!!.size>0){
                        packages.add(pack)
                    }
                }
                return packages
            }

            //Confusing yet...
            fun addAppWeeklyDataToMap(result: HashMap<String, Long>): HashMap<String, Long> {
                val allAppsUsage = getAllPackageAndTotalDuration()
                for ((packageName, duration) in allAppsUsage) {

                    if (result[packageName] == null)
                        result[packageName] = duration
                    else
                        result[packageName] = result[packageName]?.plus(duration)!!
                }
                return result
            }

            fun addPackagesWithNoUsage(){
                val packagesWithUsage = getAllPackagesWithUsage()
                val remainingApps = Utility.getAllPackages() - packagesWithUsage.toSet()

                for(packageName in remainingApps){
                    addSingleIntervalData(packageName,"12AM",0L)
                }
            }

        }

        class AppCombinedData {
            val appCombinedData = LinkedHashMap<String, String>()

            fun toReadable(appIntervalData: AppIntervalDuration) {

                val packageAndCombinedDuration = appIntervalData.getAllPackageAndTotalDuration()

                val todayAppTimeElapsedPref = TodayAppTimeElapsedPref(App.appContext)
                for ((packageName, totalDuration) in packageAndCombinedDuration) {
                    todayAppTimeElapsedPref.setTimeElapsed(packageName,(totalDuration/1000))

                    val timeInWords = TimeUtility.millisecondsToTime(totalDuration)
                    appCombinedData[packageName] = timeInWords
                }
            }

            fun getTotalTimeForToday(appIntervalData: AppIntervalDuration) : String{
                val packageAndCombinedDuration = appIntervalData.getAllPackageAndTotalDuration()
                var totalDuration = 0L
                for ((packageName, appDuration) in packageAndCombinedDuration) {
                    totalDuration+=appDuration
                }
                return TimeUtility.millisecondsToTime(totalDuration)
            }

        }

        class DayStartEnd(private val timestamp: Long) {
            var startTime: Long = 0L
            var endTime: Long = 0L

            init {
                startTime = startOfTheDay(timestamp)
                endTime = endOfTheDay(timestamp)
            }

            fun toYesterday(): DayStartEnd {
                return DayStartEnd(yesterdayTimestamp(timestamp))
            }
        }
    }
}