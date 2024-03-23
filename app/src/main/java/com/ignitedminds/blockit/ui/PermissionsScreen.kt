package com.ignitedminds.blockit.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.google.android.material.switchmaterial.SwitchMaterial
import com.ignitedminds.blockit.R
import com.ignitedminds.blockit.utils.Utility

class PermissionsScreen : AppCompatActivity() {

    private lateinit var usageSwitch : SwitchMaterial
    private lateinit var overlaySwitch : SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions_screen)

        usageSwitch = findViewById(R.id.usage_switch)
        overlaySwitch = findViewById(R.id.overlay_switch)

        setOnCheckListeners()

    }

    override fun onResume() {
        super.onResume()

        val usagePermission = Utility.checkUsageStatsPermission(this)
        val overlayPermission = Utility.drawOverOtherAppsPermission(this)

        usageSwitch.isChecked = Utility.checkUsageStatsPermission(this)
        overlaySwitch.isChecked = Utility.drawOverOtherAppsPermission(this)

        if(usagePermission&&overlayPermission){
            val intent = Intent(this,Dashboard::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun setOnCheckListeners(){
        usageSwitch.setOnClickListener{
            openUsageManagerSettings()
        }

        overlaySwitch.setOnClickListener{
            openOverlaySettings()
        }
    }

    private fun openOverlaySettings(){
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        startActivity(intent)
    }

    private fun openUsageManagerSettings(){
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }
}