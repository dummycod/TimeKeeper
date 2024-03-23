package com.ignitedminds.blockit.data

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.util.Util
import com.ignitedminds.blockit.utils.Utility

class PackageInfoStore {

    data class PackageInfo(val name: String, val icon: Drawable)

    companion object {
        private val packageDataStore = HashMap<String, PackageInfo>()

        fun initialize() {

            if (packageDataStore.isNotEmpty())
                return

            val packageList = Utility.getAllPackages()

            for (packageName in packageList) {
                val appName = Utility.getApplicationName(packageName)
                val appDrawable = Utility.getApplicationIcon(packageName)
                val packageInfo = PackageInfo(appName, appDrawable)
                packageDataStore[packageName] = packageInfo
            }
        }

        fun getPackageInfo(packageName: String): PackageInfo {
            return packageDataStore[packageName]!!
        }
    }
}