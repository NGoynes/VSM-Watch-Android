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


class TempSetting : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ppg_setting)
        val spinner = findViewById<View>(R.id.spinner) as Spinner
        val spinnerUnit = findViewById<View>(R.id.spinnerUnit) as Spinner
        spinner.setOnItemSelectedListener(TempSpinnerActivity());
        spinnerUnit.setOnItemSelectedListener(TempSpinnerActivity());
        val ranges: MutableList<String> = ArrayList()
        ranges.add("15 s")
        ranges.add("30 s")
        ranges.add("45 s")
        ranges.add("60 s")
        ranges.add("90 s")
        ranges.add("360 s")
        val unit: MutableList<String> = ArrayList()
        unit.add ("Celsius")
        unit.add ("Fahrenheit")
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ranges)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = dataAdapter
        val dataAdapterUnit = ArrayAdapter(this, android.R.layout.simple_spinner_item, unit)
        dataAdapterUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnit.adapter = dataAdapterUnit
    }
    override fun onDestroy() {
        super.onDestroy()
        fragman!!
                .beginTransaction()
                .show(sf as SettingsFragment)
                .commit()
    }
}
class TempSpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val item = parent.getItemAtPosition(pos).toString()
        when(item){
            "15 s" -> tempRange = 15
            "30 s" -> tempRange = 30
            "45 s" -> tempRange = 45
            "60 s" -> tempRange = 60
            "90 s" -> tempRange = 90
            "360 s" -> tempRange = 360
            "Celsius" -> tempCel = true
            "Fahrenheit" -> tempCel = false
        }
        // Showing selected spinner item
        Toast.makeText(parent.context, "Selected: $item", Toast.LENGTH_LONG).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
}