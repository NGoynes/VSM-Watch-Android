package com.example.vsmwatchandroidapplication.ui.dashboard

import android.annotation.SuppressLint
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
import org.jetbrains.anko.support.v4.runOnUiThread
import kotlin.math.log
import com.example.vsmwatchandroidapplication.*
import com.example.vsmwatchandroidapplication.ui.logging.LoggingFragment

var ppgOn = true
var edaOn = false
var ecgOn = false
var tempOn = false

@SuppressLint("UseSwitchCompatOrMaterialCode")

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    lateinit var PPGsw: Switch
    lateinit var PPGtxt: TextView
    lateinit var EDAsw: Switch
    lateinit var EDAtxt: TextView
    lateinit var ECGsw: Switch
    lateinit var ECGtxt: TextView
    lateinit var tempsw: Switch
    lateinit var temptxt: TextView
    lateinit var Accsw: Switch
    lateinit var Acctxt: TextView

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState == null) {
            (activity as MainActivity).supportActionBar?.title = "Dashboard"
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

            // Initialize Switches
            EDAsw = root.findViewById(R.id.dbeda_switch)
            ECGsw = root.findViewById(R.id.dbecg_switch)
            tempsw = root.findViewById(R.id.dbtemp_switch)
            PPGsw = root.findViewById(R.id.dbppg_switch)
            Accsw = root.findViewById(R.id.dbAcc_switch)

            // PPG Switch and Text
            PPGtxt = root.findViewById(R.id.dbppg_data)
            PPGsw.setOnCheckedChangeListener { _, onSwitch ->
                if (onSwitch) {
                    dashboardPPGSwitch = onSwitch

                    resetVal()
                    EDAsw.isChecked = false
                    ECGsw.isChecked = false
                    tempsw.isChecked = false

                    dashboardECGSwitch = false
                    dashboardEDASwitch = false
                    dashboardTempSwitch = false
                    dashboardAccelSwitch = false

                    readPPG()
                    ppgOn = true

                } else {
                    dashboardPPGSwitch = onSwitch

                    stopPPG()
                    Log.d("Connection", "DATA :: PPG is OFF")

                    ppgOn = false
                }

            }

            // EDA Switch and Text
            EDAtxt = root.findViewById(R.id.dbeda_data)
            EDAsw.setOnCheckedChangeListener { _, onSwitch ->
                if (onSwitch) {
                    dashboardEDASwitch = onSwitch

                    resetVal()
                    ECGsw.isChecked = false
                    PPGsw.isChecked = false
                    tempsw.isChecked = false

                    dashboardPPGSwitch = false
                    dashboardECGSwitch = false
                    dashboardTempSwitch= false
                    dashboardAccelSwitch = false

                    readEDA()
                    edaOn = true

                } else {
                    dashboardEDASwitch = onSwitch

                    stopEDA()
                    Log.d("Connection", "DATA :: EDA is OFF")

                    edaOn = false
                }
            }

            // ECG Switch and Text
            ECGtxt = root.findViewById(R.id.dbecg_data)
            ECGsw.setOnCheckedChangeListener { _, onSwitch ->

                if (onSwitch) {
                    dashboardECGSwitch

                    resetVal()
                    EDAsw.isChecked = false
                    PPGsw.isChecked = false
                    tempsw.isChecked = false

                    dashboardPPGSwitch = false
                    dashboardEDASwitch = false
                    dashboardTempSwitch = false
                    dashboardAccelSwitch = false

                    readECG()
                    ecgOn = true

                } else {
                    dashboardECGSwitch = onSwitch

                    stopECG()
                    Log.d("Connection", "DATA :: ECG is OFF")

                    ecgOn = false

                }
            }

            // Temperature Switch and Text
            temptxt = root.findViewById(R.id.dbtemp_data)
            tempsw.setOnCheckedChangeListener { _, onSwitch ->
                if (onSwitch) {
                    dashboardTempSwitch = onSwitch

                    resetVal()
                    EDAsw.isChecked = false
                    ECGsw.isChecked = false
                    PPGsw.isChecked = false

                    dashboardPPGSwitch = false
                    dashboardECGSwitch = false
                    dashboardEDASwitch = false
                    dashboardAccelSwitch = false

                    readTemp()
                    tempOn = true

                } else {
                    dashboardTempSwitch = onSwitch

                    stopTemp()
                    Log.d("Connection", "DATA :: Temp is OFF")

                    tempOn = false
                }
            }

            // Accelerometer Switch and Text
            Acctxt= root.findViewById(R.id.dbAcc_data)
            Accsw.setOnCheckedChangeListener { _, onSwitch ->
                if (onSwitch) {
                    dashboardAccelSwitch = onSwitch

                    Acctxt.setText("x:" + latAccSeriesX + ",y:" + latAccSeriesY + ",z:" + latAccSeriesZ)
                    EDAsw.isChecked = false
                    ECGsw.isChecked = false
                    PPGsw.isChecked = false
                    tempsw.isChecked = false

                    dashboardPPGSwitch = false
                    dashboardECGSwitch = false
                    dashboardEDASwitch = false
                    dashboardTempSwitch = false
                } else {
                    dashboardAccelSwitch = onSwitch

                    Acctxt.setText("----")
                }
            }
            val ScanButton: Button = root.findViewById(R.id.ScanButton)
            ScanButton.setOnClickListener {
                val intent: Intent = Intent(context?.applicationContext, ScanFragment::class.java)
                startActivity(intent)
            }
        }
        return root
    }

    private fun readPPG() {
        if (watchSdk != null) {
            val ppg = watchSdk!!.ppgApplication
            ppg.setPPGCallback { PPGDataPacket ->
                runOnUiThread {
                    PPGtxt.setText(PPGDataPacket.payload.hr.toString())
                }
//                Log.d("Connection", "DATA :: ${PPGDataPacket.payload.hr}")
//                Log.d("Connection", "DATA :: ${PPGDataPacket.payload.streamData.get(1).ppgData}")
//                Log.d("Connection", "DATA :: ${PPGDataPacket.payload.streamData.get(2).ppgData}")
//                Log.d("Connection", "DATA :: ${PPGDataPacket.payload.streamData.get(3).ppgData}")
            }
            ppg.writeLibraryConfiguration(arrayOf(longArrayOf(0x0, 0x4)))

            ppg.startSensor()
            ppg.subscribeStream()

        }

    }

    private fun stopPPG() {
        if (watchSdk != null) {
            val ppg = watchSdk!!.ppgApplication
            resetVal()

            ppg.stopSensor()
            ppg.stopAndUnsubscribeStream()
        }
    }

    private fun readECG() {
        if (watchSdk != null) {
            val eda = watchSdk!!.ecgApplication
            eda.setCallback { ECGdata ->

                Log.d("Connection", "DATA :: ${ECGdata.payload.ecgInfo}")
                runOnUiThread {
                    ECGtxt.text = ECGdata.payload.ecgInfo.toString()
                    LoggingFragment().record(ECGdata.payload.ecgInfo.toString(), "ECG")
                }
            }

            eda.startSensor()
            eda.subscribeStream()
            resetVal()
        }

    }

    private fun stopECG() {
        if (watchSdk != null) {

            val ecg = watchSdk!!.ecgApplication

            LoggingFragment().export("ECGData.csv")

            ecg.stopSensor()
            ecg.stopAndUnsubscribeStream()
            ecg.setTimeout(5)
            resetVal()

        }
    }

    private fun stopTemp() {
        if (watchSdk != null) {

            val temp = watchSdk!!.temperatureApplication
            resetVal()

            temp.stopSensor()
            temp.stopAndUnsubscribeStream()

        }
    }

    private fun readEDA() {
        if (watchSdk != null) {
            val eda = watchSdk!!.edaApplication
            eda.setCallback { EDADataPacket ->
                runOnUiThread {
                    EDAtxt.setText(EDADataPacket.payload.streamData.get(0).realData.toString())
                }
                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(0).imaginaryData}")
                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(0).realData}")
//                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(1).imaginaryData}")
//                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(1).realData}")
//                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(2).imaginaryData}")
//                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(2).realData}")
//                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(3).imaginaryData}")
//                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(3).realData}")
//                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(4).imaginaryData}")
//                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(4).realData}")
//                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(5).imaginaryData}")
//                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(5).realData}")
            }

            eda.startSensor()
            eda.subscribeStream()

        }

    }

    private fun stopEDA() {
        if (watchSdk != null) {
            val eda = watchSdk!!.edaApplication

            resetVal()
            eda.stopSensor()
            eda.stopAndUnsubscribeStream()
        }

    }

    private fun readTemp() {
        if (watchSdk != null) {
            val temps = watchSdk!!.temperatureApplication
            temps.setCallback { TempuratureDataPacket ->
                val Celsius = TempuratureDataPacket.payload.temperature1.toFloat() / 10
                runOnUiThread {
                    temptxt.setText(Celsius.toString() + "C")
                }
                Log.d("Connection", "DATA :: ${Celsius}")
            }
            temps.startSensor()
            temps.subscribeStream()

        }

    }

    private fun resetVal() {
        runOnUiThread {
            EDAtxt.setText("----")
            temptxt.setText("----")
            ECGtxt.setText("----")
            PPGtxt.setText("----")

        }
    }
}