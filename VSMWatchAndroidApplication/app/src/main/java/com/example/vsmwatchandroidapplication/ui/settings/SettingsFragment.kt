package com.example.vsmwatchandroidapplication.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.vsmwatchandroidapplication.MainActivity
import com.example.vsmwatchandroidapplication.R
import kotlinx.coroutines.selects.select

class SettingsFragment : Fragment() {

    private lateinit var vitalSettings:ListView
    private var vitalStrings:ArrayList<String> = ArrayList()

    //private lateinit var br: BroadcastReceiver
//    override fun onReceive(context: Context, intent: Intent){
//        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
//        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
//        val batteryPct = level / scale.toDouble()
//        println(batteryPct)
//    }
    override fun onCreateView(
            //context: Context,
            // intent: Intent
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?

    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        (activity as MainActivity)?.supportActionBar?.title = "Settings"
        (activity as MainActivity).checkBattery()

        vitalStrings.add("PPG")
        vitalStrings.add("ECG")
        vitalStrings.add("EDA")
        vitalStrings.add("System Information")

        vitalSettings = root.findViewById(R.id.vital_settings)

        val adapter = ArrayAdapter<String>(root.context, android.R.layout.simple_list_item_1, vitalStrings)
        vitalSettings.adapter = adapter

        vitalSettings.setOnItemClickListener{parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)

            when (selectedItem) {
                "PPG" -> {
                    val intent = Intent(context?.applicationContext, PPGSetting::class.java)
                    startActivity(intent)
                }
                "ECG" -> {
                    val intent = Intent(context?.applicationContext, ECGSetting::class.java)
                    startActivity(intent)
                }
                "EDA" -> {
                    val intent = Intent(context?.applicationContext, EDASetting::class.java)
                    startActivity(intent)
                }
                else -> {
                    val intent = Intent(context?.applicationContext, SystemSetting::class.java)
                    startActivity(intent)
                }
            }
        }

        return root
    }

//    private val batteryBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver(){
//        override fun onReceive(context: Context?, intent: Intent?){
//            if(intent?.action == "android.intent.action.BATTERY_CHANGED"){
//                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
//                Log.d(TAG, "onReceive: battery level $level")
//                batterytxt?.post{
//                    batterytxt?.text = level.toString().plus(" ").plus("%")
//                }
//            }
//        }
//    }


}