package com.example.vsmwatchandroidapplication.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.vsmwatchandroidapplication.R
import com.example.vsmwatchandroidapplication.cf
import com.example.vsmwatchandroidapplication.fragman
import com.example.vsmwatchandroidapplication.sf
import com.example.vsmwatchandroidapplication.ui.chart.ChartFragment

class PPGSetting : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ppg_setting)
    }
    override fun onDestroy() {
        super.onDestroy()
        fragman!!
                .beginTransaction()
                .show(sf as SettingsFragment)
                .commit()
    }
}