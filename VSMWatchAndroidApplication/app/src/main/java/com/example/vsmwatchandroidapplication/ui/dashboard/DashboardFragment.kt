package com.example.vsmwatchandroidapplication.ui.dashboard

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
import com.example.vsmwatchandroidapplication.MainActivity
import com.example.vsmwatchandroidapplication.R
import org.jetbrains.anko.support.v4.runOnUiThread
import kotlin.math.log

var ppgOn = true
var edaOn = false
var ecgOn = false
var tempOn = false


class DashboardFragment : Fragment() {

    var PPGtxt: TextView? = null
    var ECGtxt: TextView? = null
    var EDAtxt: TextView? = null
    var temptxt: TextView? = null
    var EDAsw: Switch? = null
    var ECGsw: Switch? = null
    var tempsw: Switch? = null
    var PPGsw: Switch? = null

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
                Acctxt.setText("x:" + latAccSeriesX + ",y:" + latAccSeriesY + ",z:" + latAccSeriesZ)
                EDAsw?.isChecked = false
                ECGsw?.isChecked = false
                PPGsw?.isChecked = false
                tempsw?.isChecked = false
            }
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
    fun readPPG() {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {
            val ppg = com.example.vsmwatchandroidapplication.watchSdk!!.ppgApplication
            ppg.setLibraryConfiguration(PPGLcfgID.LCFG_ID_ADPD4000)
            ppg.setPPGCallback{PPGDataPacket ->
              runOnUiThread {
                    PPGtxt?.setText(PPGDataPacket.payload.hr.toString())
             }
//                Log.d("Connection", "DATA :: ${PPGDataPacket.payload.hr}")
//                Log.d("Connection", "DATA :: ${PPGDataPacket.payload.streamData.get(1).ppgData}")
//                Log.d("Connection", "DATA :: ${PPGDataPacket.payload.streamData.get(2).ppgData}")
//                Log.d("Connection", "DATA :: ${PPGDataPacket.payload.streamData.get(3).ppgData}")
            }
            //ppg.writeLibraryConfiguration(arrayOf(longArrayOf(0x0, 0x4)))

            ppg.startSensor()
            ppg.subscribeStream()

        }

    }
    fun stopPPG()
    {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {
            val ppg = com.example.vsmwatchandroidapplication.watchSdk!!.ppgApplication
            resetVal()

            ppg.stopSensor()
            ppg.stopAndUnsubscribeStream()
        }
    }

    fun readECG() {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {
            val eda = com.example.vsmwatchandroidapplication.watchSdk!!.ecgApplication
            eda.setCallback { ECGdata ->

                Log.d("Connection", "DATA :: ${ECGdata.payload.ecgInfo}")
                runOnUiThread {
                    ECGtxt?.setText(ECGdata.payload.ecgInfo.toString())
                }
            }

            eda.startSensor()
            eda.subscribeStream()
            resetVal()

        }

    }
    fun stopECG()
    {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {

            val ecg = com.example.vsmwatchandroidapplication.watchSdk!!.ecgApplication

            ecg.stopSensor()
            ecg.stopAndUnsubscribeStream()
            ecg.setTimeout(5)
            resetVal()

        }
    }
    fun stopTemp()
    {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {

            val temp = com.example.vsmwatchandroidapplication.watchSdk!!.temperatureApplication
            resetVal()

            temp.stopSensor()
            temp.stopAndUnsubscribeStream()

        }
    }
    fun readEDA() {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {
            val eda = com.example.vsmwatchandroidapplication.watchSdk!!.edaApplication
            eda.setCallback { EDADataPacket ->
                runOnUiThread {
                    EDAtxt?.setText(EDADataPacket.payload.streamData.get(0).realData.toString())
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
    fun stopEDA()
    {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {
            val eda = com.example.vsmwatchandroidapplication.watchSdk!!.edaApplication

            resetVal()
            eda.stopSensor()
            eda.stopAndUnsubscribeStream()
        }

    }

    fun readTemp() {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {
            val temps = com.example.vsmwatchandroidapplication.watchSdk!!.temperatureApplication
            temps.setCallback { TempuratureDataPacket ->
                var Celsius = TempuratureDataPacket.payload.temperature1.toFloat()/10
                runOnUiThread {

                    temptxt?.setText(Celsius.toString() + "C")
                }
                Log.d("Connection", "DATA :: ${Celsius}")
            }
            temps.startSensor()
            temps.subscribeStream()

        }

    }
    fun resetVal()
    {
        runOnUiThread {
            EDAtxt?.setText("----")
            temptxt?.setText("----")
            ECGtxt?.setText("----")
            PPGtxt?.setText("----")

        }
    }
}