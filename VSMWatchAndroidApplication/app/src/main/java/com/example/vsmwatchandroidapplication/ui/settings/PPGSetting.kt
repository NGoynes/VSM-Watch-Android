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
import com.example.vsmwatchandroidapplication.R
import com.example.vsmwatchandroidapplication.fragman
import com.example.vsmwatchandroidapplication.ppgSensor
import com.example.vsmwatchandroidapplication.sf


class PPGSetting : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ppg_setting)
        val spinner = findViewById<View>(R.id.spinner) as Spinner
        spinner.setOnItemSelectedListener(PPGSpinnerActivity());
        val categories: MutableList<String> = ArrayList()
        categories.add("ADPD107")
        categories.add("ADPD108")
        categories.add("ADPD185")
        categories.add("ADPD188")
        categories.add("ADPD4000")
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
class PPGSpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val item = parent.getItemAtPosition(pos).toString()
        when(item){
            "ADPD107" -> ppgSensor = PPGLcfgID.LCFG_ID_ADPD107
            "ADPD108" -> ppgSensor = PPGLcfgID.LCFG_ID_ADPD108
            "ADPD185" -> ppgSensor = PPGLcfgID.LCFG_ID_ADPD185
            "ADPD188" -> ppgSensor = PPGLcfgID.LCFG_ID_ADPD188
            "ADPD4000" -> ppgSensor = PPGLcfgID.LCFG_ID_ADPD4000

        }
        // Showing selected spinner item
        Toast.makeText(parent.context, "Selected: $item", Toast.LENGTH_LONG).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
}



