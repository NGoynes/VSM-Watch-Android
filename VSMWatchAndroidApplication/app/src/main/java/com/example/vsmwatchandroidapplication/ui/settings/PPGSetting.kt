package com.example.vsmwatchandroidapplication.ui.settings

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.analog.study_watch_sdk.core.enums.PPGLcfgID
import com.example.vsmwatchandroidapplication.*


class PPGSetting : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ppg_setting)
        val spinner = findViewById<View>(R.id.spinner) as Spinner
        val spinnerSample = findViewById<View>(R.id.spinnerSampling) as Spinner
        val spinnerRange = findViewById<View>(R.id.spinnerRange) as Spinner
        spinner.setOnItemSelectedListener(PPGSpinnerActivity());
        spinnerSample.setOnItemSelectedListener(PPGSpinnerActivity());
        spinnerRange.setOnItemSelectedListener(PPGSpinnerActivity());
        val categories: MutableList<String> = ArrayList()
        categories.add("ADPD107")
        categories.add("ADPD108")
        categories.add("ADPD185")
        categories.add("ADPD188")
        categories.add("ADPD4000")
        val samples: MutableList<String> = ArrayList()
        samples.add("100Hz")
        samples.add("250Hz")
        samples.add("300Hz")
        samples.add("500Hz")
        samples.add("1000Hz")
        val ranges: MutableList<String> = ArrayList()
        ranges.add("15 s")
        ranges.add("30 s")
        ranges.add("45 s")
        ranges.add("60 s")
        ranges.add("90 s")
        ranges.add("360 s")
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = dataAdapter
        val dataAdapterSample = ArrayAdapter(this, android.R.layout.simple_spinner_item, samples)
        dataAdapterSample.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSample.adapter = dataAdapterSample
        val dataAdapterRange = ArrayAdapter(this, android.R.layout.simple_spinner_item, ranges)
        dataAdapterRange.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRange.adapter = dataAdapterRange
    }
    override fun onDestroy() {
        super.onDestroy()
        fragman!!
                .beginTransaction()
                .show(sf as SettingsFragment)
                .commit()
    }
}
class PPGSpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val item = parent.getItemAtPosition(pos).toString()
        when(item){
            "ADPD107" -> ppgSensor = PPGLcfgID.LCFG_ID_ADPD107
            "ADPD108" -> ppgSensor = PPGLcfgID.LCFG_ID_ADPD108
            "ADPD185" -> ppgSensor = PPGLcfgID.LCFG_ID_ADPD185
            "ADPD188" -> ppgSensor = PPGLcfgID.LCFG_ID_ADPD188
            "ADPD4000" -> ppgSensor = PPGLcfgID.LCFG_ID_ADPD4000
            "100Hz" -> ppgSamp = 0x64L
            "250Hz" -> ppgSamp = 0xFAL
            "300Hz" -> ppgSamp = 0x12CL
            "500Hz" -> ppgSamp = 0x1F4L
            "1000Hz" -> ppgSamp = 0x3E8L
            "15 s" -> {
                ppgRange = 15
                adxlRange = 15
            }
            "30 s" -> {
                ppgRange = 30
                adxlRange = 30
            }
            "45 s" -> {
                ppgRange = 45
                adxlRange = 45
            }
            "60 s" -> {
                ppgRange = 60
                adxlRange = 60
            }
            "90 s" -> {
                ppgRange = 90
                adxlRange = 90
            }
            "360 s" -> {
                ppgRange = 360
                adxlRange = 360
            }
        }
        // Showing selected spinner item
        Toast.makeText(parent.context, "Selected: $item", Toast.LENGTH_LONG).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
}



