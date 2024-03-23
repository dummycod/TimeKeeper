package com.ignitedminds.blockit.utils


import android.app.AppOpsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.util.Patterns
import android.webkit.URLUtil
import androidx.appcompat.app.AppCompatActivity
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.application.App.Companion.appContext
import com.ignitedminds.blockit.constants.Constant
import java.net.MalformedURLException
import java.net.URL


class Utility {
    companion object {

        fun isValidUrl(urlString: String?): Boolean {
            try {
                val url = URL(urlString)
                return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString)
                    .matches()
            } catch (ignored: MalformedURLException) {
            }
            return false
        }

        fun getApplicationName(packageName: String): String {
            val packageManager = appContext.packageManager
            return try {
                val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
                packageManager.getApplicationLabel(applicationInfo).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                "unknown"
            }
        }


        fun getApplicationIcon(packageName: String): Drawable {
            val packageManager = appContext.packageManager
            return try {
                val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
                packageManager.getApplicationIcon(applicationInfo)
            } catch (e: PackageManager.NameNotFoundException) {
                appContext.getDrawable(R.drawable.ic_launcher_background)!!
            }
        }

        fun getAllPackages() : List<String>{
            val nonSystemPackageList = ArrayList<String>()
            val pm: PackageManager = appContext.packageManager
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            for (packageInfo in packages) {
                if(!isSystemPackage(packageInfo.packageName)){
                    nonSystemPackageList.add(packageInfo.packageName)
                }
            }
            return nonSystemPackageList
        }

        fun isSystemPackage(packageName: String): Boolean {
            for (name in Constant.whitelistedSystemPackage) {
                if (packageName==name)
                    return false
            }
            return isBlacklistedSystemPackage(packageName)
        }


        private fun isBlacklistedSystemPackage(packageName: String): Boolean {
            if (packageName == appContext.packageName)
                return true
            return try {
                val pm = appContext.packageManager
                val pkgInfo = pm.getPackageInfo(packageName, 0)
                pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM !== 0
            } catch (e: Exception) {
                false
            }
        }

        fun checkUsageStatsPermission(context : Context): Boolean {
            val appOps = context.getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
            val uid = Process.myUid()
            val mode = if (Build.VERSION.SDK_INT >= 29) {
                appOps.unsafeCheckOp(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    uid,
                    "com.ignitedminds.blockit"
                )
            } else {
                appOps.checkOp(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    uid,
                    "com.ignitedminds.blockit"
                )
            }

            return mode == AppOpsManager.MODE_ALLOWED
        }

        fun drawOverOtherAppsPermission(context : Context) : Boolean{
            return if (Build.VERSION.SDK_INT >= 29) {
                Settings.canDrawOverlays(context)
            }else{
                true
            }
        }

        fun isNetworkConnected(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork?.isConnectedOrConnecting == true
        }
    }
}

