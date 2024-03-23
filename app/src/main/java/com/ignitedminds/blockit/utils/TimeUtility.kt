package com.ignitedminds.blockit.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class TimeUtility {

    companion object {
        fun startOfTheDay(): Long {
            val cal = Calendar.getInstance()
            cal.apply {
                timeZone = TimeZone.getDefault()
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                return timeInMillis
            }
        }

        fun startOfTheDay(timestamp : Long) : Long{
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis;
        }

        fun endOfTheDay(timestamp: Long) : Long{
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            cal.set(Calendar.MILLISECOND, 59)
            return cal.timeInMillis;
        }

        fun yesterdayTimestamp(timestamp: Long) : Long{
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp
            cal.add(Calendar.DATE,-1)
            return cal.timeInMillis;
        }

        fun startOfHour(time: Long) : Long{
            val cal = Calendar.getInstance()
            cal.apply {
                timeInMillis = time
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                return timeInMillis
            }
        }


        fun getEndOfHour(startTime : Long) : Long{
            val startOfHour = startOfHour(startTime)
            return startOfHour + 3599999L
        }

        fun timestampTo12hr(timestamp: Long) : String{
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp
            var hour = cal.get(Calendar.HOUR_OF_DAY)
            var time = ""
            time = if(hour>=12){
                if(hour==12){
                    "${hour} PM"
                }else
                    "${hour-12} PM"
            }else{
                if(hour==0){
                    hour = 12
                }
                "$hour AM"
            }
            return time
        }


        fun isNextHour(a: Long, b: Long): Boolean{
            val cal1 = Calendar.getInstance()
            cal1.timeInMillis = a
            val cal2 = Calendar.getInstance()
            cal2.timeInMillis = b

            val hour1 = cal1.get(Calendar.HOUR_OF_DAY)
            val hour2 = cal2.get(Calendar.HOUR_OF_DAY)

            return hour1 != hour2
        }

        fun millisecondsToTime(time: Long) : String{
            var tempTime = time/1000
            val seconds = (tempTime%3600)%60
            tempTime /= 60
            val minutes = tempTime%60
            val hours = tempTime/60
            var result = ""
            if(hours>0){
                result+="$hours hr "
            }
            if(minutes>0){
                result+="$minutes m "
            }

            if(seconds>0&&!result.contains("hr")){
                result+="$seconds s"
            }
            if(result.isBlank())
                result = "0s"
            return result
        }

        @SuppressLint("SimpleDateFormat")
        fun getDay(timestamp: Long) : String{
            val date = Date()
            date.time = timestamp
            return SimpleDateFormat("EE").format(date)
        }

        fun hourToSeconds(hour : Int) : Long{
            return hour*3600L
        }

        fun secondsToHours(seconds : Long) : Int {
            return (seconds/3600).toInt()
        }

        fun minutesToSeconds(min : Int) : Long{
            return min*60L
        }

        fun secondsToMinutes(seconds : Long) : Int{
            val remainingMinutes = seconds%3600
            return (remainingMinutes/60).toInt()
        }
    }
}