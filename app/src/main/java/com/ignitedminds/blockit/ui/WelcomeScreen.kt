package com.ignitedminds.blockit.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.data.local.sharedprefs.SettingsPrefs
import com.ignitedminds.blockit.data.local.sharedprefs.TimekeeperSharedPref
import com.ignitedminds.blockit.utils.Utility

class WelcomeScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)

        val button = findViewById<Button>(R.id.get_started)

        val settingsPrefs = SettingsPrefs(this)
        //settingsPrefs.resetPrefs()

        val timekeeperSharedPref = TimekeeperSharedPref(this)

        if (!settingsPrefs.isFirstTime()) {
            moveToNextScreen()
        }

        button.setOnClickListener {
            settingsPrefs.setFirstTime()
            moveToNextScreen()
        }
    }

    private fun moveToNextScreen() {
        if (isPermissionGranted()) {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
        } else {
            val intent = Intent(this, PermissionsScreen::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
        }
        finish()
    }


    private fun isPermissionGranted(): Boolean {
        return Utility.checkUsageStatsPermission(this) && Utility.drawOverOtherAppsPermission(this)
    }
}