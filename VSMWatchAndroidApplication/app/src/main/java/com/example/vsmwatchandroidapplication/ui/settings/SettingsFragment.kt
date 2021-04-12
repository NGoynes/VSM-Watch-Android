package com.example.vsmwatchandroidapplication.ui.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.vsmwatchandroidapplication.R
import android.content.Intent
import android.content.IntentFilter
import android.nfc.Tag
import android.util.Log
import com.example.vsmwatchandroidapplication.MainActivity

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel


    override fun onCreateView(
            //context: Context,
            // intent: Intent
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?

    ): View? {
        settingsViewModel =
                ViewModelProvider(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        settingsViewModel.text.observe(viewLifecycleOwner, Observer {
        })
        (activity as MainActivity)?.supportActionBar?.title = "Setting"
        val batterytxt: TextView = root.findViewById(R.id.batteryView)
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context?.registerReceiver(null, ifilter)
        }
        val batteryPct: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }
        batterytxt.setText(batteryPct.toString() + "%")
        return root
    }


}