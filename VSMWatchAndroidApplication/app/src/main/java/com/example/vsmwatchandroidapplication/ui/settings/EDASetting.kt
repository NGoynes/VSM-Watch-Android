package com.example.vsmwatchandroidapplication.ui.settings

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.vsmwatchandroidapplication.*

class EDASetting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eda_setting)
        val spinner = findViewById<View>(R.id.spinner) as Spinner
        val spinnerSample = findViewById<View>(R.id.spinnerSampling) as Spinner
        val spinnerRange = findViewById<View>(R.id.spinnerRange) as Spinner
        spinner.setOnItemSelectedListener(EDASpinnerActivity());
        spinnerSample.setOnItemSelectedListener(EDASpinnerActivity());
        spinnerRange.setOnItemSelectedListener(EDASpinnerActivity());
        val categories: MutableList<String> = ArrayList()
        categories.add("1")
        categories.add("2")
        categories.add("3")
        categories.add("4")
        categories.add("5")
        val samples: MutableList<String> = ArrayList()
        samples.add("4Hz")
        samples.add("8Hz")
        samples.add("16Hz")
        samples.add("25Hz")
        samples.add("30Hz")
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
        val dataAdapterSampling = ArrayAdapter(this, android.R.layout.simple_spinner_item, samples)
        dataAdapterSampling.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSample.adapter = dataAdapterSampling
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
class EDASpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val item = parent.getItemAtPosition(pos).toString()
        when(item){
            "1" -> edaDec = 1
            "2" -> edaDec = 2
            "3" -> edaDec = 3
            "4" -> edaDec = 4
            "5" -> edaDec = 5
            "4Hz" -> edaSamp = 0x04L
            "8Hz" -> edaSamp = 0x08L
            "16Hz" -> edaSamp = 0x10L
            "25Hz" -> edaSamp = 0x19L
            "30Hz" -> edaSamp = 0x1EL
            "15 s" -> edaRange = 15
            "30 s" -> edaRange = 30
            "45 s" -> edaRange = 45
            "60 s" -> edaRange = 60
            "90 s" -> edaRange = 90
            "360 s" -> edaRange = 360

        }
        // Showing selected spinner item
        Toast.makeText(parent.context, "Selected: $item", Toast.LENGTH_LONG).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
}
