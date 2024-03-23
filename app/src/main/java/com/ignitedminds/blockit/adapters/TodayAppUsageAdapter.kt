package com.ignitedminds.blockit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.application.App
import com.ignitedminds.blockit.constants.Constant
import com.ignitedminds.blockit.data.PackageInfoStore
import com.ignitedminds.blockit.utils.Utility

class TodayAppUsageAdapter(
    private var appNameIcon: HashMap<String, String>,
    private val lockedApps: List<String>,
    private val context: Context,
    private val infoClickListener: TodayAppUsageInfoListener
) : RecyclerView.Adapter<TodayAppUsageAdapter.ViewHolder>() {

    private var packageList = appNameIcon.keys.toList()
    private var mLockedApps = ArrayList(lockedApps)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root: View = itemView
        val icon: ImageView = itemView.findViewById(R.id.app_icon)
        val name: TextView = itemView.findViewById(R.id.app_name)
        val duration: TextView = itemView.findViewById(R.id.duration)
        val lockStatus: ImageView = itemView.findViewById(R.id.lock_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.apps_usage_listitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val packageName = packageList[position]

        if (packageName in mLockedApps) {
            holder.lockStatus.visibility = View.VISIBLE
        } else {
            holder.lockStatus.visibility = View.GONE
        }

        //val packageInfo = PackageInfoStore.getPackageInfo(packageName)
        var name = Utility.getApplicationName(packageName) //packageInfo.name
        val icon = Utility.getApplicationIcon(packageName)


        holder.root.setOnClickListener {
            infoClickListener.onInfoClick(packageName)
        }

        if (name.length > 16) {
            name = "${name.subSequence(0, 16)}..."
        }
        Glide.with(context).load(icon).into(holder.icon)
        holder.name.text = name
        holder.duration.text = appNameIcon[packageName]
    }

    override fun getItemCount(): Int = appNameIcon.size

    fun updateDataSet(mAppNameIcon: HashMap<String, String>) {
        appNameIcon = mAppNameIcon
        packageList = appNameIcon.keys.toList()
        notifyDataSetChanged()
    }


    fun updateApplicationLockStatusIcon(packageName: String, isLocked: Boolean) {

        if (isLocked && packageName !in mLockedApps) {
            mLockedApps.add(packageName)
        } else if (!isLocked && packageName in mLockedApps) {
            mLockedApps.remove(packageName)
        }
        val packageIndex = packageList.indexOf(packageName)
        Constant.logger("$mLockedApps $packageIndex")
        notifyItemChanged(packageIndex)
    }

    interface TodayAppUsageInfoListener {
        fun onInfoClick(packageName: String)
    }

}