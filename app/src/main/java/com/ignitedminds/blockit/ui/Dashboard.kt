package com.ignitedminds.blockit.ui


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.RenderMode
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.adapters.TodayAppUsageAdapter
import com.ignitedminds.blockit.constants.Constant
import com.ignitedminds.blockit.data.PackageInfoStore
import com.ignitedminds.blockit.data.UsageDataStore
import com.ignitedminds.blockit.data.local.nosql.LockedAppDB
import com.ignitedminds.blockit.data.local.sharedprefs.SettingsPrefs
import com.ignitedminds.blockit.data.local.sharedprefs.TimekeeperSharedPref
import com.ignitedminds.blockit.databinding.ActivityDashboardBinding
import com.ignitedminds.blockit.ui.dialog.AppInfoDialog
import com.ignitedminds.blockit.ui.dialog.todoDialogs.TodoParentDialog
import com.ignitedminds.blockit.ui.dialog.SearchAppDialog
import com.ignitedminds.blockit.ui.dialog.todoDialogs.TodoParentDialog.Companion.SHOW_LIST
import com.ignitedminds.blockit.listeners.LockedStatusListener
import com.ignitedminds.blockit.services.LockService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class Dashboard : AppCompatActivity(), TodayAppUsageAdapter.TodayAppUsageInfoListener,
    LockedStatusListener {

    private lateinit var binding: ActivityDashboardBinding
    private var usageStoreLive = MutableLiveData<UsageDataStore>()
    private lateinit var todayData: LinkedHashMap<String, String>
    private lateinit var todayAppUsageAdapter: TodayAppUsageAdapter

    private lateinit var timekeeperSharedPref: TimekeeperSharedPref
    private lateinit var settingsPrefs: SettingsPrefs

    private lateinit var lockedApps: ArrayList<String>

    override fun onInfoClick(packageName: String) {
        val data = usageStoreLive.value!!.getSinglePackageWeeklyData(packageName)
        val sessions = usageStoreLive.value!!.getTodaySessionDataOfApplication(packageName)
        val appInfoBottomSheet = AppInfoDialog(data, packageName, sessions, this)
        appInfoBottomSheet.show(supportFragmentManager, "App Info Dialog..")
    }

    override fun onLockedStatusChanged(
        packageName: String,
        isLocked: Boolean
    ) {
        todayAppUsageAdapter.updateApplicationLockStatusIcon(packageName, isLocked)

        if (isLocked) {
            lockedApps.add(packageName)
        } else {
            lockedApps.remove(packageName)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        timekeeperSharedPref = TimekeeperSharedPref(this)
        settingsPrefs = SettingsPrefs(this)
        initializeView()
    }

    override fun onResume() {
        super.onResume()
        initializeUsageStore()
    }


    private fun initializeView() {

        if (settingsPrefs.isLockerEnabled()) {
            binding.lockerSwitch.isChecked = true
        }

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextColor(resources.getColor(R.color.white))

        setIconToFAB()
        setUpObservers()
        setupClickListeners()
        hardwareAccelerateProgressAnimation()
    }

    private fun setIconToFAB() {
        binding.fab.apply {
            setImageResource(R.drawable.ic_bulb)
        }
    }

    private fun initializeUsageStore() {
        showProgress()
        GlobalScope.launch(Dispatchers.Default) {
            // PackageInfoStore.initialize()
            getLockedApps()
            val usageStore = UsageDataStore(this@Dashboard)
            val currentTime = System.currentTimeMillis()

            if (timekeeperSharedPref.isNewDay(currentTime)) {
                usageStore.initializeWeeklyUsageData()
                timekeeperSharedPref.updateDay(currentTime)
                Constant.logger("Putting new Data In WEEK DB")
            }
            usageStore.initializeTodayUsageData()
            usageStoreLive.postValue(usageStore)
        }
    }

    private fun putTop3TimeKillersData() {

        val listOfApps = usageStoreLive.value!!.getTop3Apps()

        binding.apply {
            top1 = listOfApps[0]
            top2 = listOfApps[1]
            top3 = listOfApps[2]
        }
    }

    private fun putDataToScreenTimeList(data: LinkedHashMap<String, String>) {
        binding.appUsage.layoutManager = LinearLayoutManager(this)
        todayAppUsageAdapter = TodayAppUsageAdapter(
            data,
            lockedApps,
            this,
            this as TodayAppUsageAdapter.TodayAppUsageInfoListener
        )
        binding.appUsage.adapter = todayAppUsageAdapter
    }

    private fun setupClickListeners() {
        binding.apply {


            fab.setOnClickListener {
                val todoParentDialogBottomSheet = TodoParentDialog(SHOW_LIST)
                todoParentDialogBottomSheet.show(supportFragmentManager, "Create Profile Dialog..")
            }

            searchBtn.setOnClickListener {
                val searchAppDialog = SearchAppDialog(todayData, lockedApps) {
                    this@Dashboard.onInfoClick(it)
                }
                searchAppDialog.show(supportFragmentManager, "Search app dialog")
            }

            lockerSwitch.setOnCheckedChangeListener { view, checked ->
                settingsPrefs.updateLockerEnabled(checked)
                if (checked) {
                    startLockService()
                } else {
                    stopLockService()
                }
                Constant.logger("isLockEnabled ${settingsPrefs.isLockerEnabled()}")
            }
        }
    }

    private fun getLockedApps() {
        val lockedAppDB = LockedAppDB(this)
        lockedApps = lockedAppDB.getAllLockedApplications()
    }

    private fun startLockService() {
        val intent = Intent(this, LockService::class.java)
        intent.putExtra("STATUS", true)
        startService(intent)
    }

    private fun stopLockService() {
        val intent = Intent(this, LockService::class.java)
        intent.putExtra("STATUS", false)
        startService(intent)
    }

    private fun setUpObservers() {
        usageStoreLive.observe(this) {
            todayData = it!!.getTodayData()
            binding.totalTime.text = it.getTotalDurationForToday()
            putTop3TimeKillersData()
            putDataToScreenTimeList(todayData)
            hideProgress()
        }

    }

    private fun hardwareAccelerateProgressAnimation() {
        binding.apply {
          //  loading.renderMode = RenderMode.HARDWARE
        }
    }

    private fun showProgress() {
        binding.apply {
            loading.visibility = View.VISIBLE
            dataScreen.visibility = View.GONE
        }
    }

    private fun hideProgress() {
        binding.apply {
            dataScreen.visibility = View.VISIBLE
            loading.visibility = View.GONE
        }
    }
}