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

        val PPGsw: Switch = root.findViewById(R.id.dbppg_switch)
        val PPGtxt: TextView = root.findViewById(R.id.dbppg_data)
        val ppgfile: InputStream = resources.openRawResource(R.raw.adpd)
        val ppgrows: List<List<String>> = csvReader().readAll(ppgfile)
        val ppgVal = ppgrows[ppgrows.size - 1][2]
        PPGsw.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if(onSwitch)
                PPGtxt.setText(ppgVal)
            else
                PPGtxt.setText("----")
        }

        val EDAsw: Switch = root.findViewById(R.id.dbeda_switch)
        val EDAtxt: TextView = root.findViewById(R.id.dbeda_data)
        val edafile: InputStream = resources.openRawResource(R.raw.eda)
        val edarows: List<List<String>> = csvReader().readAll(edafile)
        val edaVal = edarows[edarows.size - 1][3]
        EDAsw.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if(onSwitch)
                EDAtxt.setText(edaVal)
            else
                EDAtxt.setText("----")
        }

         val ECGsw: Switch = root.findViewById(R.id.dbecg_switch)
         val ECGtxt: TextView = root.findViewById(R.id.dbecg_data)
        val ecgfile: InputStream = resources.openRawResource(R.raw.eda)
        val ecgrows: List<List<String>> = csvReader().readAll(ecgfile)
        val ecgVal = ecgrows[ecgrows.size - 1][2]
        ECGsw.setOnCheckedChangeListener { compoundButton, onSwitch ->
             if(onSwitch)
                 ECGtxt.setText(ecgVal)
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
                temptxt.setText(tempVal + "C")
            else
                temptxt.setText("----")
        }


        val Accfile: InputStream = resources.openRawResource(R.raw.adxl)
        val Accrows: List<List<String>> = csvReader().readAll(Accfile)
        val AccValx = Accrows[Accrows.size - 1][1]
        val AccValy = Accrows[Accrows.size - 1][2]
        val AccValz = Accrows[Accrows.size - 1][3]
        val Accsw: Switch = root.findViewById(R.id.dbAcc_switch)
        val Acctxt: TextView = root.findViewById(R.id.dbAcc_data)
        Accsw.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if(onSwitch)
                Acctxt.setText("x:" + AccValx + ",y:" + AccValy + ",z:" + AccValz)
            else
                Acctxt.setText("----")
        }
        return root
    }

}