package com.example.vsmwatchandroidapplication.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.vsmwatchandroidapplication.R
import com.example.vsmwatchandroidapplication.fragman
import com.example.vsmwatchandroidapplication.sf

class SystemSetting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_setting)
    }
    override fun onDestroy() {
        super.onDestroy()
        fragman!!
                .beginTransaction()
                .show(sf as SettingsFragment)
                .commit()
    }
}