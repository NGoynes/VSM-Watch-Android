package com.example.vsmwatchandroidapplication.ui.settings

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.analog.study_watch_sdk.core.enums.PPGLcfgID
import com.example.vsmwatchandroidapplication.*

class ECGSetting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ecg_setting)
        val spinner = findViewById<View>(R.id.spinner) as Spinner
        val spinnerSample = findViewById<View>(R.id.spinnerSampling) as Spinner
        val spinnerRange = findViewById<View>(R.id.spinnerRange) as Spinner
        spinner.setOnItemSelectedListener(ECGSpinnerActivity());
        spinnerSample.setOnItemSelectedListener(ECGSpinnerActivity());
        spinnerRange.setOnItemSelectedListener(ECGSpinnerActivity());
        val categories: MutableList<String> = ArrayList()
        categories.add("1")
        categories.add("2")
        categories.add("3")
        categories.add("4")
        categories.add("5")
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
class ECGSpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val item = parent.getItemAtPosition(pos).toString()
        when(item){
            "1" -> ecgDec = 1
            "2" -> ecgDec = 2
            "3" -> ecgDec = 3
            "4" -> ecgDec = 4
            "5" -> ecgDec = 5
            "100Hz" -> ecgSamp = 0x64L
            "250Hz" -> ecgSamp = 0xFAL
            "300Hz" -> ecgSamp = 0x12CL
            "500Hz" -> ecgSamp = 0x1F4L
            "1000Hz" -> ecgSamp = 0x3E8L
            "15 s" -> ecgRange = 15
            "30 s" -> ecgRange = 30
            "45 s" -> ecgRange = 45
            "60 s" -> ecgRange = 60
            "90 s" -> ecgRange = 90
            "360 s" -> ecgRange = 360

        }
        // Showing selected spinner item
        Toast.makeText(parent.context, "Selected: $item", Toast.LENGTH_LONG).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
}