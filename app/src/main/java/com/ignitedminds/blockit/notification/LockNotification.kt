package com.ignitedminds.blockit.notification

import android.app.*
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.application.App.Companion.appContext
import com.ignitedminds.blockit.receiver.NotificationReceiver
import com.ignitedminds.blockit.ui.Dashboard


class LockNotification {
    companion object {

        private const val LOCK_NOTIFICATION_CHANNEL_ID = "LockNotificationChannel"
        private const val LOCK_NOTIFICATION_CHANNEL = "Lock Notification"
        private const val LOCK_NOTIFICATION_CHANNEL_DESCRIPTION =
            "This the notification for lock service. Terminating this will disable lock"

        fun createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val lockChannel = NotificationChannel(
                    LOCK_NOTIFICATION_CHANNEL_ID,
                    LOCK_NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_HIGH
                )

                lockChannel.description = LOCK_NOTIFICATION_CHANNEL_DESCRIPTION

                val manager = appContext.getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(lockChannel)
            }
        }

        fun createLockNotification(): Notification {


            val builder = NotificationCompat.Builder(appContext, LOCK_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_lock_24)
                .setContentTitle("BlockIt")
                .setContentText("Tap Here To Disable Lock!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)

            val notificationIntent = Intent(appContext, Dashboard::class.java)
            val pendingIntent = PendingIntent.getActivity(
                appContext,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            builder.setContentIntent(pendingIntent)

            return builder.build()
        }
    }
}