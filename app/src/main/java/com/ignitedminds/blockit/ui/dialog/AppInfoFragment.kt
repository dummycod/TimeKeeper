package com.ignitedminds.blockit.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.adapters.DailySessionAdapter
import com.ignitedminds.blockit.constants.Constant
import com.ignitedminds.blockit.data.local.nosql.LockedAppDB
import com.ignitedminds.blockit.data.local.nosql.TimeLimitDB
import com.ignitedminds.blockit.databinding.FragmentAppInfoBinding
import com.ignitedminds.blockit.listeners.LockedStatusListener
import com.ignitedminds.blockit.utils.ChartUtility.Companion.initializeChart
import com.ignitedminds.blockit.utils.TimeUtility
import com.ignitedminds.blockit.utils.UIUtility
import com.ignitedminds.blockit.utils.Utility


class AppInfoFragment(
    private val usageData: HashMap<String, Long>,
    private val packageName: String,
    private val sessions: List<String>,
    private val lockedStatusListener: LockedStatusListener
) : Fragment() {

    private var dominantColor = 0
    private lateinit var binding: FragmentAppInfoBinding
    private var totalDurations: Long = 0

    private var timeLimitHour =0
    private var timeLimitMinute =0

    private lateinit var lockedAppDB: LockedAppDB
    private lateinit var timeLimitDB: TimeLimitDB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_app_info, container, false)

        totalDurations = usageData.values.reduce { acc, l -> acc + l }

        initialize()
        return binding.root
    }


    private fun initialize() {

        lockedAppDB = LockedAppDB(requireContext())
        timeLimitDB = TimeLimitDB(requireContext())

        binding.apply {
            appName.text = Utility.getApplicationName(packageName)
            appIcon.setImageDrawable(Utility.getApplicationIcon(packageName))
            dominantColor =
                UIUtility.getDominantColorFromImage(Utility.getApplicationIcon(packageName))

            if (totalDurations == 0L) {
                noData.visibility = View.VISIBLE
            }

            if (lockedAppDB.isLocked(packageName)) {
                lockStatusSwitch.isChecked = true
            }

            val timeLimit = timeLimitDB.getTimeLimit(packageName)

            timeLimitHour = TimeUtility.secondsToHours(timeLimit)
            timeLimitMinute = TimeUtility.secondsToMinutes(timeLimit)

            initializeChart(chart, dominantColor, usageData)
            initializeTimeLimitData()
            initializeSessionRv()
            setOnClickListeners()
        }
    }

    private fun initializeTimeLimitData() {
        setTimeToTimePicker(timeLimitHour, timeLimitMinute)
    }

    private fun setOnClickListeners() {
        binding.timePicker.setOnClickListener {
            val materialTimePicker = MaterialTimePicker.Builder()
                .setTitleText("Please Enter A Daily Limit")
                .setHour(timeLimitHour)
                .setMinute(timeLimitMinute)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .build()

            materialTimePicker.addOnPositiveButtonClickListener {

                setTimeToTimePicker(materialTimePicker.hour, materialTimePicker.minute)

                val totalTime =
                    TimeUtility.hourToSeconds(materialTimePicker.hour) + TimeUtility.minutesToSeconds(
                        materialTimePicker.minute
                    )
                timeLimitDB.setTimeLimit(packageName, totalTime)
            }
            materialTimePicker.show(childFragmentManager, "App Info")
        }

        setupOnAppLockedSwitch()
    }

    private fun setTimeToTimePicker(hour: Int, minute: Int) {
        var timeText = ""

        if (hour == 0 && minute == 0) {
            binding.timePicker.text = "No Time Limit"
            return
        }
        timeText += if (hour == 0) "" else "$hour hr"
        timeText += if (minute == 0) "" else " $minute min"

        binding.timePicker.text = timeText
    }

    private fun setupOnAppLockedSwitch() {
        binding.lockStatusSwitch.setOnCheckedChangeListener { p0, checked ->
            if (checked) {
                lockedAppDB.lockApplication(packageName)
                Toast.makeText(requireContext(), "Application Locked!", Toast.LENGTH_SHORT).show()
            } else {
                lockedAppDB.unlockApplication(packageName)
                Toast.makeText(requireContext(), "Application Unlocked!", Toast.LENGTH_SHORT).show()
            }
            Constant.logger(packageName)
            lockedStatusListener.onLockedStatusChanged(packageName,checked)
        }
    }


    private fun initializeSessionRv() {
        if (sessions.isEmpty()) {
            binding.noSessionData.visibility = View.VISIBLE
            binding.sessionList.visibility = View.GONE
        } else {
            binding.noSessionData.visibility = View.GONE
            binding.sessionList.visibility = View.VISIBLE
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            binding.sessionList.layoutManager = layoutManager
            binding.sessionList.adapter = DailySessionAdapter(sessions)
        }

    }

}