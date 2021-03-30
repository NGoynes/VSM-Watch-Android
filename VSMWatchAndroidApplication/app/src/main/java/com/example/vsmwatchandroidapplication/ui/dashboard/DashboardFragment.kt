package com.example.vsmwatchandroidapplication.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.vsmwatchandroidapplication.R
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
        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        //val lines:List<String> = File("temp.csv").readLines()
      //  readHealthData()

         val ECGsw: Switch = root.findViewById(R.id.dbecg_switch)
         val ECGtxt: TextView = root.findViewById(R.id.dbecg_data)

        ECGsw.setOnCheckedChangeListener { compoundButton, onSwitch ->
             if(onSwitch)
                 ECGtxt.setText("ON")
             else
                 ECGtxt.setText("----")
         }
        val file: InputStream = resources.openRawResource(R.raw.temp)
        val rows: List<List<String>> = csvReader().readAll(file)
        val tempVal = rows[rows.size - 1][1]

        val tempsw: Switch = root.findViewById(R.id.dbtemp_switch)
        val temptxt: TextView = root.findViewById(R.id.dbtemp_data)

        tempsw.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if(onSwitch)
                temptxt.setText(tempVal + "F")
            else
                temptxt.setText("----")
        }
        return root
    }
    private fun readHealthData() {
        val file: InputStream = resources.openRawResource(R.raw.temp)
        val rows: List<List<String>> = csvReader().readAll(file)
        val tempVal = rows[rows.size - 1][1]
    }
}