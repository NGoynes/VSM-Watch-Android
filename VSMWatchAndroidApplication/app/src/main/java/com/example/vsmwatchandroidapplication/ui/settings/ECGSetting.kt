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
        spinner.setOnItemSelectedListener(ECGSpinnerActivity());
        val categories: MutableList<String> = ArrayList()
        categories.add("1")
        categories.add("2")
        categories.add("3")
        categories.add("4")
        categories.add("5")
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = dataAdapter
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

        }
        // Showing selected spinner item
        Toast.makeText(parent.context, "Selected: $item", Toast.LENGTH_LONG).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
}