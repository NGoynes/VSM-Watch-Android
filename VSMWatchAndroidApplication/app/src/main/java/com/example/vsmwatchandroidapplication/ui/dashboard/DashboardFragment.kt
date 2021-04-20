package com.example.vsmwatchandroidapplication.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.vsmwatchandroidapplication.*

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

        if(savedInstanceState == null) {
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

            // PPG Switch and text
            PPGsw = root.findViewById(R.id.dbppg_switch)
            PPGtxt = root.findViewById(R.id.dbppg_data)
            if (dashboardPPGSwitch == null) {
                dashboardPPGSwitch = PPGsw
                dashboardPPGSwitch!!.isChecked = PPGsw.isChecked
            }
            else {
                PPGsw.isChecked = dashboardPPGSwitch!!.isChecked
            }
            PPGsw.setOnCheckedChangeListener { _, onSwitch ->
                if(onSwitch) {
                    PPGtxt.setText("Filler")
                    dashboardPPGSwitch!!.isChecked = onSwitch
                }
                else
                    PPGtxt.setText("----")
                    dashboardPPGSwitch!!.isChecked = onSwitch
            }

            // EDA Switch and text
            EDAsw = root.findViewById(R.id.dbeda_switch)
            EDAtxt = root.findViewById(R.id.dbeda_data)
            if (dashboardEDASwitch == null) {
                dashboardEDASwitch = EDAsw
                dashboardEDASwitch!!.isChecked = EDAsw.isChecked
            }
            else {
                EDAsw.isChecked = dashboardEDASwitch!!.isChecked
            }
            EDAsw.setOnCheckedChangeListener { _, onSwitch ->
                if(onSwitch) {
                    EDAtxt.setText(latEdaSeries)
                    dashboardEDASwitch!!.isChecked = onSwitch
                }
                else {
                    EDAtxt.setText("----")
                    dashboardEDASwitch!!.isChecked = onSwitch
                }
            }

            // ECG Switch and text
            ECGsw = root.findViewById(R.id.dbecg_switch)
            ECGtxt = root.findViewById(R.id.dbecg_data)
            if (dashboardECGSwitch == null) {
                dashboardECGSwitch = ECGsw
                dashboardECGSwitch!!.isChecked = ECGsw.isChecked
            }
            else {
                ECGsw.isChecked = dashboardECGSwitch!!.isChecked
            }
            ECGsw.setOnCheckedChangeListener { _, onSwitch ->
                if(onSwitch) {
                    ECGtxt.setText("Filler")
                    dashboardECGSwitch!!.isChecked = onSwitch
                }
                else {
                    ECGtxt.setText("----")
                    dashboardECGSwitch!!.isChecked = onSwitch
                }
            }

            // Temperature Switch and text
            tempsw = root.findViewById(R.id.dbtemp_switch)
            temptxt = root.findViewById(R.id.dbtemp_data)
            if (dashboardTempSwitch == null) {
                dashboardTempSwitch = tempsw
                dashboardTempSwitch!!.isChecked = tempsw.isChecked
            }
            else {
                tempsw.isChecked = dashboardTempSwitch!!.isChecked
            }
            tempsw.setOnCheckedChangeListener { _, onSwitch ->
                if(onSwitch) {
                    temptxt.setText(latTempSeries + "C")
                    dashboardTempSwitch!!.isChecked = onSwitch
                }
                else {
                    temptxt.setText("----")
                    dashboardTempSwitch!!.isChecked = onSwitch
                }
            }

            // Accelerometer Switch and text
            Accsw = root.findViewById(R.id.dbAcc_switch)
            Acctxt = root.findViewById(R.id.dbAcc_data)
            if (dashboardAccelSwitch == null) {
                dashboardAccelSwitch = Accsw
                dashboardAccelSwitch!!.isChecked = Accsw.isChecked
            }
            else {
                Accsw.isChecked = dashboardAccelSwitch!!.isChecked
            }
            Accsw.setOnCheckedChangeListener { _, onSwitch ->
                if(onSwitch) {
                    Acctxt.text = "x:" + latAccSeriesX + ",y:" + latAccSeriesY + ",z:" + latAccSeriesZ
                    dashboardAccelSwitch!!.isChecked = onSwitch
                }
                else {
                    Acctxt.text = "----"
                    dashboardAccelSwitch!!.isChecked = onSwitch
                }
            }
            val ScanButton: Button = root.findViewById(R.id.ScanButton)
            ScanButton.setOnClickListener {
                val intent: Intent = Intent(context?.applicationContext, ScanFragment::class.java)
                startActivity(intent)
            }
        }
        else {
            println("Not null saved instance")
        }

        return root
    }
}