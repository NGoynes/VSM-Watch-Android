package com.example.vsmwatchandroidapplication.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.analog.study_watch_sdk.application.PPGApplication
import com.analog.study_watch_sdk.core.enums.EDADFTWindow
import com.analog.study_watch_sdk.core.enums.ScaleResistor
import com.example.vsmwatchandroidapplication.*
import com.example.vsmwatchandroidapplication.ui.chart.*
import com.example.vsmwatchandroidapplication.ui.logging.LoggingFragment
import com.example.vsmwatchandroidapplication.ui.logging.hasLogged
import com.example.vsmwatchandroidapplication.ui.logging.isLoggingOn
import com.google.common.base.Stopwatch
import org.jetbrains.anko.support.v4.runOnUiThread
import java.lang.Math.atan
import java.lang.Math.sqrt
import java.time.LocalDateTime
import kotlin.math.pow


var ppgOn = false
var edaOn = false
var ecgOn = false
var tempOn = false


class DashboardFragment : Fragment() {

    var PPGtxt: TextView? = null
    var ECGtxt: TextView? = null
    var EDAtxt: TextView? = null
    var temptxt: TextView? = null
    lateinit var EDAsw: Switch
    lateinit var ECGsw: Switch
    lateinit var tempsw: Switch
    lateinit var PPGsw: Switch

    val ppg: PPGApplication = watchSdk!!.ppgApplication

    private lateinit var dashboardViewModel: DashboardViewModel

    @RequiresApi(Build.VERSION_CODES.O)
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
        PPGsw.setOnCheckedChangeListener { _, onSwitch ->
            if(onSwitch) {
                // Turn off other signals
                EDAsw.isChecked = false
                ECGsw.isChecked = false
                tempsw.isChecked = false
                resetVal()

                stopECG(true)
                stopEDA(true)
                stopTemp(true)

                // Begin reading PPG
                readPPG()
                ppgOn = true

            } else {
                if(isLoggingOn) {
                    Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
                    PPGsw.isChecked = true
                } else {
                    stopPPG(false)
                    ppgOn = false
                }
            }
        }

        EDAtxt = root.findViewById(R.id.dbeda_data)
        EDAsw.setOnCheckedChangeListener { _, onSwitch ->
            if(onSwitch) {
                // Turn off other signals
                ECGsw.isChecked = false
                PPGsw.isChecked = false
                tempsw.isChecked = false
                resetVal()

                stopPPG(true)
                stopECG(true)
                stopTemp(true)

                // Begin reading EDA
                readEDA()
                edaOn = true
            } else {
                if(isLoggingOn) {
                    Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
                    EDAsw.isChecked = true
                } else {
                    stopEDA(false)
                    edaOn = false
                }
            }
        }

        ECGtxt = root.findViewById(R.id.dbecg_data)
        ECGsw.setOnCheckedChangeListener { _, onSwitch ->
            if(onSwitch) {
                // Turn off other signals
                if (EDAsw.isChecked)
                    EDAsw.isChecked = false
                    PPGsw.isChecked = false
                    tempsw.isChecked = false
                    resetVal()

                    stopPPG(true)
                    stopEDA(true)
                    stopTemp(true)

                    // Begin reading ECG
                    readECG()
                    ecgOn = true
            } else {
                if(isLoggingOn) {
                    Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
                    ECGsw.isChecked = true
                } else {
                    stopECG(false)
                    ecgOn = false
                }
            }
        }


        temptxt = root.findViewById(R.id.dbtemp_data)
        tempsw.setOnCheckedChangeListener { _, onSwitch ->
            if (onSwitch) {
                // Turn off other signals
                EDAsw.isChecked = false
                ECGsw.isChecked = false
                PPGsw.isChecked = false
                resetVal()

                stopPPG(true)
                stopEDA(true)
                stopECG(true)

                // Begin reading temperature
                readTemp()
                tempOn = true
            }
            else {
                if(isLoggingOn) {
                    Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
                    tempsw.isChecked = true
                }
                else {
                    stopTemp(false)
                    tempOn = false
                }
            }
        }

        //val Accsw: Switch = root.findViewById(R.id.dbAcc_switch)
        //val Acctxt: TextView = root.findViewById(R.id.dbAcc_data)
//        if (!isLoggingOn) {
//            Accsw.setOnCheckedChangeListener { _, onSwitch ->
//                if(onSwitch) {
//                    //Acctxt.setText("x:" + latAccSeriesX + ",y:" + latAccSeriesY + ",z:" + latAccSeriesZ)
//                    EDAsw.isChecked = false
//                    ECGsw.isChecked = false
//                    PPGsw.isChecked = false
//                    tempsw.isChecked = false
//
//                    accOn = true
//                }
//                else {
//                    Acctxt.text = "----"
//                    accOn = false
//                }
//            }
//        }
//        else {
//            Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
//        }

        val ScanButton: Button = root.findViewById(R.id.ScanButton)
        ScanButton.setOnClickListener {
            val intent: Intent = Intent(context?.applicationContext, ScanFragment::class.java)
            startActivity(intent)
        }
        return root
    }
    private fun readPPG() {
        if (watchSdk != null) {
            ppg.setLibraryConfiguration(ppgSensor)
            //ppg.writeDCBToLCFG()
            ppg.writeLibraryConfiguration(arrayOf(longArrayOf(0x00, ppgSamp)))
            val packet = ppg.readLibraryConfiguration(longArrayOf(0x00))
            Log.d("test", packet.toString())
            var ppgTimer: Stopwatch = Stopwatch.createUnstarted()
            ppg.setSyncPPGCallback{ PPGDataPacket ->
                if (!ppgTimer.isRunning) {
                    ppgTimer.start()
                }
                (cf as ChartFragment).addEntry(PPGDataPacket, ppgTimer)
                (ppgF as PPGFragment).addEntry(PPGDataPacket, ppgTimer)
                (cf as ChartFragment).addEntryADXL(PPGDataPacket, ppgTimer)
                (adxlF as ADXLFragment).addEntryADXL(PPGDataPacket, ppgTimer)

                runOnUiThread {
                    PPGtxt?.text = PPGDataPacket.payload.streamData.last().ppgData.toFloat().toString()

                    if (isLoggingOn && ppgOn) {
                        for (i in PPGDataPacket.payload.streamData) {
                            if (i != null) (lf as LoggingFragment).recordVital(i.ppgTimestamp, i.ppgData)
                        }
                    }
                }
            }
            ppg.startSensor()
            ppg.subscribeStream()
        }
    }
    @SuppressLint("NewApi")
    private fun stopPPG(forcedSwitchOff: Boolean)
    {
        if (watchSdk != null) {
            val ppg = watchSdk!!.ppgApplication

            ppg.stopSensor()
            ppg.stopAndUnsubscribeStream()

            if (!forcedSwitchOff && hasLogged) {
                val currentDateTime = LocalDateTime.now()
                val fileName = "PPGData$currentDateTime.csv"
                (lf as LoggingFragment).writeToFile("PPG", fileName)
            }
            resetVal()
        }
    }

    // ECG
    private fun readECG() {
        if (watchSdk != null) {
            val ecg = watchSdk!!.ecgApplication
            ecg.setDecimationFactor(ecgDec)
            ecg.writeDeviceConfigurationBlock(arrayOf(longArrayOf(0x00, ecgSamp)))
            ecg.writeDCBToLCFG()
            val packet = ecg.readLibraryConfiguration(longArrayOf(0x00))
            Log.d("test", packet.toString())
            var ecgTimer: Stopwatch = Stopwatch.createUnstarted()
            ecg.setCallback { ECGdata ->
                if (!ecgTimer.isRunning) {
                    ecgTimer.start()
                }
                (cf as ChartFragment).addEntry(ECGdata, ecgTimer)
                (ecgF as ECGFragment).addEntry(ECGdata, ecgTimer)

                runOnUiThread {
                    ECGtxt?.text = ECGdata.payload.ecgInfo.toString()

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
    private fun stopECG(forcedSwitchOff: Boolean)
    {
        if (watchSdk != null) {

            val ecg = watchSdk!!.ecgApplication

            ecg.stopSensor()
            ecg.stopAndUnsubscribeStream()
            ecg.setTimeout(5)

            if (!forcedSwitchOff && hasLogged) {
                val currentDateTime = LocalDateTime.now()
                val fileName = "ECGData$currentDateTime.csv"
                (lf as LoggingFragment).writeToFile("ECG", fileName)
            }

            resetVal()
        }
    }

    // EDA
    private fun readEDA() {
        if (watchSdk != null) {
            val eda = watchSdk!!.edaApplication
            eda.setDecimationFactor(edaDec)
            eda.enableDynamicScaling(ScaleResistor.SCALE_RESISTOR_100K, ScaleResistor.SCALE_RESISTOR_512K, ScaleResistor.SCALE_RESISTOR_100K)
            eda.setDiscreteFourierTransformation(EDADFTWindow.DFT_WINDOW_4)
            println(edaSamp)
            eda.writeDeviceConfigurationBlock(arrayOf(longArrayOf(0x00, edaSamp)))
            eda.writeDCBToLCFG()
            val packet = eda.readLibraryConfiguration(longArrayOf(0x00))
            Log.d("test", packet.toString())

            
            var edaTimer: Stopwatch = Stopwatch.createUnstarted()
            eda.setCallback { EDADataPacket ->
                runOnUiThread {
                    if (!edaTimer.isRunning) {
                        edaTimer.start()
                    }
                    (cf as ChartFragment).addEntryMag(EDADataPacket, edaTimer)
                    (edaMagF as EDAMagFragment).addEntryMag(EDADataPacket, edaTimer)
                    (cf as ChartFragment).addEntryPhase(EDADataPacket, edaTimer)
                    (edaPhaseF as EDAPhaseFragment).addEntryPhase(EDADataPacket, edaTimer)

                    if (isLoggingOn && edaOn) {
                        for (i in EDADataPacket.payload.streamData) {
                            if (i != null) {
                                val mag = sqrt(i.realData.toDouble().pow(2.0) + i.imaginaryData.toDouble().pow(2.0)).toFloat()
                                val phase = atan((i.imaginaryData / i.realData).toDouble()).toFloat()

                                (lf as LoggingFragment).recordVital(i.timestamp, i.realData, i.imaginaryData, mag, phase)
                            }
                        }
                    }
                }
            }

            eda.startSensor()
            eda.subscribeStream()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun stopEDA(forcedSwitchOff: Boolean)
    {
        if (watchSdk != null) {
            val eda = watchSdk!!.edaApplication
            resetVal()

            if (!forcedSwitchOff && hasLogged) {
                val currentDateTime = LocalDateTime.now()
                val fileName = "EDAData$currentDateTime.csv"
                (lf as LoggingFragment).writeToFile("EDA", fileName)
            }

            eda.stopSensor()
            eda.stopAndUnsubscribeStream()
        }

    }

    fun readTemp() {
        if (watchSdk != null) {
            val temps = watchSdk!!.temperatureApplication
            val tempTimer = Stopwatch.createUnstarted()
            temps.setCallback { TemperatureDataPacket ->
                if (!tempTimer.isRunning) {
                    tempTimer.start()
                }
                (cf as ChartFragment).addEntry(TemperatureDataPacket, tempTimer)
                (tempF as TempFragment).addEntry(TemperatureDataPacket, tempTimer)
                var Celsius = TemperatureDataPacket.payload.temperature1.toFloat()/10
                var Fahrenheit = (Celsius*(9/5)+32)
                runOnUiThread {
                    if(tempCel == true){
                        temptxt?.text = Celsius.toString() + "°C"
                    }
                    else{
                        temptxt?.text = Fahrenheit.toString() + "°F"
                    }


                    (lf as LoggingFragment).recordVital(TemperatureDataPacket.payload.timestamp, Celsius)
                }
            }

            temps.startSensor()
            temps.subscribeStream()
        }
    }
    @SuppressLint("NewApi")
    private fun stopTemp(forcedSwitchOff: Boolean)
    {
        if (watchSdk != null) {

            val temp = watchSdk!!.temperatureApplication

            temp.stopSensor()
            temp.stopAndUnsubscribeStream()

            if (!forcedSwitchOff && hasLogged) {
                val currentDateTime = LocalDateTime.now()
                val fileName = "TemperatureData$currentDateTime.csv"
                (lf as LoggingFragment).writeToFile("Temperature", fileName)
            }

            resetVal()
        }
    }

    // Reset
    private fun resetVal()
    {
        EDAtxt?.text = "----"
        temptxt?.text = "----"
        ECGtxt?.text = "----"
        PPGtxt?.text = "----"
    }
}
