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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.analog.study_watch_sdk.core.enums.PPGLcfgID
import com.example.vsmwatchandroidapplication.*
import com.example.vsmwatchandroidapplication.ui.chart.ChartFragment
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

    private var log = watchSdk!!.fsApplication

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity)?.supportActionBar?.title = "Dashboard"
        (activity as MainActivity).checkBattery()
        /*val latTempSeries = (activity as MainActivity).latTempSeries
        val latAccSeriesX = (activity as MainActivity).latAccSeriesX
        val latAccSeriesY = (activity as MainActivity).latAccSeriesY
        val latAccSeriesZ = (activity as MainActivity).latAccSeriesZ
        val latPPGSeries1 = (activity as MainActivity).latPPGSeries1
        val latPPGSeries2 = (activity as MainActivity).latPPGSeries2

        val latEcgSeries = (activity as MainActivity).latEcgSeries
        val latEdaSeries = (activity as MainActivity).latEdaSeries*/
        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        EDAsw = root.findViewById(R.id.dbeda_switch)
        ECGsw = root.findViewById(R.id.dbecg_switch)
        tempsw = root.findViewById(R.id.dbtemp_switch)
        PPGsw = root.findViewById(R.id.dbppg_switch)
        PPGtxt = root.findViewById(R.id.dbppg_data)
        PPGsw?.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if(onSwitch) {
                resetVal()
                EDAsw?.isChecked = false
                ECGsw?.isChecked = false
                tempsw?.isChecked = false
                readPPG()
                ppgOn = true

            }
            else {
                stopPPG()
                Log.d("Connection", "DATA :: PPG is OFF")

                ppgOn = false
            }

        }

        EDAtxt = root.findViewById(R.id.dbeda_data)

        EDAsw?.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if(onSwitch) {
                resetVal()
                ECGsw?.isChecked = false
                PPGsw?.isChecked = false
                tempsw?.isChecked = false
                readEDA()
                edaOn = true

            }
            else {
                stopEDA()
                Log.d("Connection", "DATA :: EDA is OFF")

                edaOn = false
            }
        }

        ECGtxt = root.findViewById(R.id.dbecg_data)

        ECGsw?.setOnCheckedChangeListener { compoundButton, onSwitch ->

            if(onSwitch) {
                resetVal()
                EDAsw?.isChecked = false
                PPGsw?.isChecked = false
                tempsw?.isChecked = false

                readECG()
                ecgOn = true

            }
             else {
                stopECG()
                Log.d("Connection", "DATA :: ECG is OFF")

                ecgOn = false

            }
         }


        temptxt = root.findViewById(R.id.dbtemp_data)
        tempsw?.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if (onSwitch) {
                resetVal()
                EDAsw?.isChecked = false
                ECGsw?.isChecked = false
                PPGsw?.isChecked = false
                readTemp()
                tempOn = true

            }
            else {
                stopTemp()
                Log.d("Connection", "DATA :: Temp is OFF")

                tempOn = false
            }
        }

        val Accsw: Switch = root.findViewById(R.id.dbAcc_switch)
        val Acctxt: TextView = root.findViewById(R.id.dbAcc_data)
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
        val ScanButton: Button = root.findViewById(R.id.ScanButton)
        ScanButton.setOnClickListener {
            val intent: Intent = Intent(context?.applicationContext, ScanFragment::class.java)
            startActivity(intent)
        }
        return root
    }

    // PPG
    private fun readPPG() {
        if (watchSdk != null) {
            val ppg = watchSdk!!.ppgApplication
            ppg.setLibraryConfiguration(PPGLcfgID.LCFG_ID_ADPD108)
            ppg.setPPGCallback{PPGDataPacket ->
                runOnUiThread {
                    PPGtxt?.setText(PPGDataPacket.payload.hr.toFloat().toString())

                    if(isLoggingOn && ppgOn) {
                        (lf as LoggingFragment).recordVital(PPGDataPacket.payload.timestamp, PPGDataPacket.payload.hr)
                    }
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

    // Temperature
    private fun readTemp() {
        if (watchSdk != null) {
            val temps = watchSdk!!.temperatureApplication
            temps.setCallback { TemperatureDataPacket ->
                val celsius = TemperatureDataPacket.payload.temperature1.toFloat()/10
                runOnUiThread {
                    temptxt?.text = celsius.toString() + "C"

                    (lf as LoggingFragment).recordVital(TemperatureDataPacket.payload.timestamp, celsius)
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
