package com.example.vsmwatchandroidapplication.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.vsmwatchandroidapplication.MainActivity
import com.example.vsmwatchandroidapplication.R
import com.example.vsmwatchandroidapplication.ui.logging.LoggingFragment
import com.example.vsmwatchandroidapplication.ui.logging.TemperatureLog
import com.example.vsmwatchandroidapplication.ui.settings.SettingsFragment
import com.example.vsmwatchandroidapplication.watchSdk
import com.github.doyaaaaaken.kotlincsv.client.CsvFileReader
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.io.FileReader
import java.io.InputStream
var ppgOn = true
var edaOn = false
var ecgOn = false
var tempOn = false


class DashboardFragment : Fragment() {


    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity)?.supportActionBar?.title = "Dashboard"
        (activity as MainActivity).checkBattery()
        val latTempSeries = (activity as MainActivity).latTempSeries
        val latAccSeriesX = (activity as MainActivity).latAccSeriesX
        val latAccSeriesY = (activity as MainActivity).latAccSeriesY
        val latAccSeriesZ = (activity as MainActivity).latAccSeriesZ
        val latPPGSeries1 = (activity as MainActivity).latPPGSeries1
        val latPPGSeries2 = (activity as MainActivity).latPPGSeries2

        val latEcgSeries = (activity as MainActivity).latEcgSeries
        val latEdaSeries = (activity as MainActivity).latEdaSeries
        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)


        val PPGsw: Switch = root.findViewById(R.id.dbppg_switch)
        val PPGtxt: TextView = root.findViewById(R.id.dbppg_data)
        PPGsw.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if(onSwitch) {
                (activity as MainActivity).readPPG()
                ppgOn = true
            }
            else {
                PPGtxt.setText("----")
                (activity as MainActivity).stopPPG()
                ppgOn = false
            }

        }

        val EDAsw: Switch = root.findViewById(R.id.dbeda_switch)
        val EDAtxt: TextView = root.findViewById(R.id.dbeda_data)

        EDAsw.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if(onSwitch) {
                (activity as MainActivity).readEDA()
                edaOn = true
            }
            else {
                EDAtxt.setText("----")
                (activity as MainActivity).stopEDA()
                edaOn = false
            }
        }

         val ECGsw: Switch = root.findViewById(R.id.dbecg_switch)
         val ECGtxt: TextView = root.findViewById(R.id.dbecg_data)

        ECGsw.setOnCheckedChangeListener { compoundButton, onSwitch ->

            if(onSwitch) {
                (activity as MainActivity).readECG()
                ecgOn = true
            }
             else {
                (activity as MainActivity).stopECG()
                ECGtxt.setText("----")
                ecgOn = false

            }
         }


        val tempsw: Switch = root.findViewById(R.id.dbtemp_switch)
        val temptxt: TextView = root.findViewById(R.id.dbtemp_data)
        tempsw.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if (onSwitch) {
                (activity as MainActivity).readTemp()
                tempOn = true
            }
            else {
                temptxt.setText("----")
                (activity as MainActivity).stopTemp()
                tempOn = false

            }
        }


        val Accsw: Switch = root.findViewById(R.id.dbAcc_switch)
        val Acctxt: TextView = root.findViewById(R.id.dbAcc_data)
        Accsw.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if(onSwitch)
                Acctxt.setText("x:" + latAccSeriesX + ",y:" + latAccSeriesY + ",z:" + latAccSeriesZ)
            else
                Acctxt.setText("----")
        }
        val ScanButton: Button = root.findViewById(R.id.ScanButton)
        ScanButton.setOnClickListener {
            val intent: Intent = Intent(context?.applicationContext, ScanFragment::class.java)
            startActivity(intent)
        }
        return root
    }

}