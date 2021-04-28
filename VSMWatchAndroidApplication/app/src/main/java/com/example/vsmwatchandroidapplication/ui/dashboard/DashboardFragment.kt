package com.example.vsmwatchandroidapplication.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.analog.study_watch_sdk.application.PPGApplication
import com.analog.study_watch_sdk.core.enums.PPGLcfgID
import com.analog.study_watch_sdk.core.packets.stream.TemperatureDataPacket
import com.example.vsmwatchandroidapplication.*
import com.example.vsmwatchandroidapplication.ui.chart.ChartFragment
import com.example.vsmwatchandroidapplication.ui.chart.PPGFragment
import com.example.vsmwatchandroidapplication.ui.logging.LoggingFragment
import com.example.vsmwatchandroidapplication.ui.logging.isLoggingOn
import org.jetbrains.anko.support.v4.runOnUiThread
import java.time.LocalDateTime


var ppgOn = false
var edaOn = false
var ecgOn = false
var tempOn = false
var accOn = false

class DashboardFragment : Fragment() {

    var PPGtxt: TextView? = null
    var ECGtxt: TextView? = null
    var EDAtxt: TextView? = null
    var temptxt: TextView? = null
    var EDAsw: Switch? = null
    var ECGsw: Switch? = null
    var tempsw: Switch? = null
    var PPGsw: Switch? = null

    val ppg: PPGApplication = watchSdk!!.ppgApplication

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).supportActionBar?.title = "Dashboard"
        (activity as MainActivity).checkBattery()

        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        EDAsw = root.findViewById(R.id.dbeda_switch)
        ECGsw = root.findViewById(R.id.dbecg_switch)
        tempsw = root.findViewById(R.id.dbtemp_switch)
        PPGsw = root.findViewById(R.id.dbppg_switch)
        PPGtxt = root.findViewById(R.id.dbppg_data)

        if (!isLoggingOn) {
            PPGsw?.setOnCheckedChangeListener { _, onSwitch ->
                if(onSwitch) {
                    // Turn off other signals
                    EDAsw?.isChecked = false
                    ECGsw?.isChecked = false
                    tempsw?.isChecked = false
                    resetVal()

                    stopECG()
                    stopEDA()
                    stopTemp()

                    // Begin reading PPG
                    readPPG()
                    ppgOn = true

                }
                else {
                    if(isLoggingOn) {
                        Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
                        PPGsw!!.isChecked = true
                    }
                    else {
                        stopPPG()
                        ppgOn = false
                    }
                }
            }
        }
        else {
            Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
        }

        EDAtxt = root.findViewById(R.id.dbeda_data)

        if (!isLoggingOn) {
            EDAsw?.setOnCheckedChangeListener { _, onSwitch ->
                if(onSwitch) {
                    // Turn off other signals
                    ECGsw?.isChecked = false
                    PPGsw?.isChecked = false
                    tempsw?.isChecked = false
                    resetVal()

                    stopPPG()
                    stopECG()
                    stopTemp()

                    // Begin reading EDA
                    readEDA()
                    edaOn = true
                }
                else {
                    if(isLoggingOn) {
                        Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
                        EDAsw!!.isChecked = true
                    }
                    else {
                        stopEDA()
                        edaOn = false
                    }
                }
            }
        }
        else {
            Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
        }

        ECGtxt = root.findViewById(R.id.dbecg_data)

        if (!isLoggingOn) {
            ECGsw?.setOnCheckedChangeListener { _, onSwitch ->

                if(onSwitch) {
                    // Turn off other signals
                    EDAsw?.isChecked = false
                    PPGsw?.isChecked = false
                    tempsw?.isChecked = false
                    resetVal()

                    stopPPG()
                    stopEDA()
                    stopTemp()

                    // Begin reading ECG
                    readECG()
                    ecgOn = true
                }
                else {
                    if(isLoggingOn) {
                        Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
                        ECGsw!!.isChecked = true
                    }
                    else {
                        stopECG()
                        ecgOn = false
                    }
                }
            }
        }
        else {
            Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
        }


        temptxt = root.findViewById(R.id.dbtemp_data)
        tempsw?.setOnCheckedChangeListener { _, onSwitch ->
            if (onSwitch) {
                // Turn off other signals
                EDAsw?.isChecked = false
                ECGsw?.isChecked = false
                PPGsw?.isChecked = false
                resetVal()

                stopPPG()
                stopEDA()
                stopECG()

                // Begin reading temperature
                readTemp()
                tempOn = true
            }
            else {
                if(isLoggingOn) {
                    Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
                    tempsw!!.isChecked = true
                }
                else {
                    stopTemp()
                    tempOn = false
                }
            }
        }

        val Accsw: Switch = root.findViewById(R.id.dbAcc_switch)
        val Acctxt: TextView = root.findViewById(R.id.dbAcc_data)
        if (!isLoggingOn) {
            Accsw.setOnCheckedChangeListener { compoundButton, onSwitch ->
                if(onSwitch) {
                    //Acctxt.setText("x:" + latAccSeriesX + ",y:" + latAccSeriesY + ",z:" + latAccSeriesZ)
                    EDAsw?.isChecked = false
                    ECGsw?.isChecked = false
                    PPGsw?.isChecked = false
                    tempsw?.isChecked = false

                    accOn = true
                }
                else
                    Acctxt.setText("----")
                accOn = false
            }
        }
        else {
            Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
        }

        val ScanButton: Button = root.findViewById(R.id.ScanButton)
        ScanButton.setOnClickListener {
            val intent: Intent = Intent(context?.applicationContext, ScanFragment::class.java)
            startActivity(intent)
        }
        return root
    }
    fun readPPG() {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {
            ppg.setLibraryConfiguration(PPGLcfgID.LCFG_ID_ADPD4000)
            ppg.setSyncPPGCallback{PPGDataPacket ->
                (cf as ChartFragment).addEntry(PPGDataPacket)
                (ppgF as PPGFragment).addEntry(PPGDataPacket)

              runOnUiThread {
                  PPGtxt?.text = PPGDataPacket.payload.streamData.last().ppgData.toFloat().toString()
             }
            }
            ppg.startSensor()
            ppg.subscribeStream()

        }

    }
    @SuppressLint("NewApi")
    private fun stopPPG()
    {
        if (watchSdk != null) {
            val ppg = watchSdk!!.ppgApplication

            ppg.stopSensor()
            ppg.stopAndUnsubscribeStream()

            val currentDateTime = LocalDateTime.now()
            val fileName = "PPGData$currentDateTime.csv"
            (lf as LoggingFragment).writeToFile("PPG", fileName)

            resetVal()
        }
    }

    // ECG
    private fun readECG() {
        if (watchSdk != null) {
            val ecg = com.example.vsmwatchandroidapplication.watchSdk!!.ecgApplication
            ecg.setCallback { ECGdata ->
                (cf as ChartFragment).addEntry(ECGdata)

                runOnUiThread {
                    ECGtxt?.setText(ECGdata.payload.ecgInfo.toString())

                    if (isLoggingOn && ecgOn) {
                        for ( i in ECGdata.payload.streamData) {
                            if (i != null) (lf as LoggingFragment).recordVital(i.timestamp, i.ecgData)
                        }
                    }
                }
            }

            ecg.startSensor()
            ecg.subscribeStream()
            resetVal()
        }
    }
    @SuppressLint("NewApi")
    private fun stopECG()
    {
        if (watchSdk != null) {

            val ecg = watchSdk!!.ecgApplication

            ecg.stopSensor()
            ecg.stopAndUnsubscribeStream()
            ecg.setTimeout(5)

            val currentDateTime = LocalDateTime.now()
            val fileName = "ECGData$currentDateTime.csv"
            (lf as LoggingFragment).writeToFile("ECG", fileName)

            resetVal()
        }
    }

    // EDA
    private fun readEDA() {
        if (watchSdk != null) {
            val eda = watchSdk!!.edaApplication
            eda.setCallback { EDADataPacket ->
                runOnUiThread {
                    (cf as ChartFragment).addEntryMag(EDADataPacket)
                    (cf as ChartFragment).addEntryPhase(EDADataPacket)
                }
            }

            eda.startSensor()
            eda.subscribeStream()
        }
    }
    private fun stopEDA()
    {
        if (watchSdk != null) {
            val eda = watchSdk!!.edaApplication

            resetVal()
            eda.stopSensor()
            eda.stopAndUnsubscribeStream()
        }

    }

    fun readTemp() {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {
            val temps = com.example.vsmwatchandroidapplication.watchSdk!!.temperatureApplication
            temps.setCallback { TemperatureDataPacket ->
                (cf as ChartFragment).addEntry(TemperatureDataPacket())

                var Celsius = TemperatureDataPacket.payload.temperature1.toFloat()/10
                runOnUiThread {
                    temptxt?.text = Celsius.toString() + "C"

                    (lf as LoggingFragment).recordVital(TemperatureDataPacket.payload.timestamp, Celsius)
                }
                //Log.d("Connection", "DATA :: ${celsius}")
            }

            temps.startSensor()
            temps.subscribeStream()
        }
    }
    @SuppressLint("NewApi")
    private fun stopTemp()
    {
        if (watchSdk != null) {

            val temp = watchSdk!!.temperatureApplication

            temp.stopSensor()
            temp.stopAndUnsubscribeStream()

            val currentDateTime = LocalDateTime.now()
            val fileName = "TemperatureData$currentDateTime.csv"
            (lf as LoggingFragment).writeToFile("Temperature", fileName)

            resetVal()
        }
    }

    // Reset
    private fun resetVal()
    {
        runOnUiThread {
            EDAtxt?.setText("----")
            temptxt?.setText("----")
            ECGtxt?.setText("----")
            PPGtxt?.setText("----")
        }
    }
}
