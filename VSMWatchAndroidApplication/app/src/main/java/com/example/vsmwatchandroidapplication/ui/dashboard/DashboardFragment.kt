package com.example.vsmwatchandroidapplication.ui.dashboard

import android.content.Intent
import android.os.Bundle
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
import com.example.vsmwatchandroidapplication.ui.logging.TemperatureLog
import com.github.doyaaaaaken.kotlincsv.client.CsvFileReader
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.io.FileReader
import java.io.InputStream

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
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
            if(onSwitch)
                PPGtxt.setText("S1:" + latPPGSeries1 + ",S2:" + latPPGSeries2)
            else
                PPGtxt.setText("----")
        }

        val EDAsw: Switch = root.findViewById(R.id.dbeda_switch)
        val EDAtxt: TextView = root.findViewById(R.id.dbeda_data)

        EDAsw.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if(onSwitch)
                EDAtxt.setText(latEdaSeries)
            else
                EDAtxt.setText("----")
        }

         val ECGsw: Switch = root.findViewById(R.id.dbecg_switch)
         val ECGtxt: TextView = root.findViewById(R.id.dbecg_data)

        ECGsw.setOnCheckedChangeListener { compoundButton, onSwitch ->
             if(onSwitch)
                 ECGtxt.setText(latEcgSeries)
             else
                 ECGtxt.setText("----")
         }


        val tempsw: Switch = root.findViewById(R.id.dbtemp_switch)
        val temptxt: TextView = root.findViewById(R.id.dbtemp_data)
        tempsw.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if(onSwitch)
                temptxt.setText(latTempSeries + "C")
            else
                temptxt.setText("----")
        }



        val Accsw: Switch = root.findViewById(R.id.dbAcc_switch)
        val Acctxt: TextView = root.findViewById(R.id.dbAcc_data)
        Accsw.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if(onSwitch)
                Acctxt.setText("x:" + latAccSeriesX + ",y:" + latAccSeriesY + ",z:" + latAccSeriesZ)
            else
                Acctxt.setText("----")
        }
        return root
    }

}