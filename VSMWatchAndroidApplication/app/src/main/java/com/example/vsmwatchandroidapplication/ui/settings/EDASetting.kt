package com.example.vsmwatchandroidapplication.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.vsmwatchandroidapplication.R
import com.example.vsmwatchandroidapplication.fragman
import com.example.vsmwatchandroidapplication.sf

class EDASetting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eda_setting)
    }
    override fun onDestroy() {
        super.onDestroy()
        fragman!!
                .beginTransaction()
                .show(sf as SettingsFragment)
                .commit()
    }
}