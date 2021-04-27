package com.example.vsmwatchandroidapplication.ui.logging

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream
import com.example.vsmwatchandroidapplication.*
import com.example.vsmwatchandroidapplication.ui.dashboard.ecgOn
import com.example.vsmwatchandroidapplication.ui.dashboard.edaOn
import com.example.vsmwatchandroidapplication.ui.dashboard.ppgOn
import com.example.vsmwatchandroidapplication.ui.dashboard.tempOn
import java.lang.Exception
import java.lang.StringBuilder


@SuppressLint("UseSwitchCompatOrMaterialCode")

var isLoggingOn: Boolean = false

class LoggingFragment : Fragment() {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchStartLog: Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchPPG: Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchECG: Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchEDA: Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchAccelerometer: Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchPedometer: Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchTemperature: Switch

    // Data for PPG, ECG, Temp
    private var ecg_ppg_tempSeconds: Double = 0.0
    private val ecg_ppg_tempData = StringBuilder()

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_logging, container, false)

        val f: String = context!!.filesDir.path

        (activity as MainActivity).supportActionBar?.title = "Logging"
        (activity as MainActivity).checkBattery()

        // Initialize Switches
        switchStartLog = root.findViewById(R.id.switch_startLogging)
        switchPPG = root.findViewById(R.id.switch_PPG)
        switchECG = root.findViewById(R.id.switch_ECG)
        switchEDA = root.findViewById(R.id.switch_EDA)
        switchAccelerometer = root.findViewById(R.id.switch_Accelerometer)
        switchPedometer = root.findViewById(R.id.switch_Pedometer)
        switchTemperature = root.findViewById(R.id.switch_Temperature)

        // Checks if PPG in Dashboard is checked
        switchPPG.setOnCheckedChangeListener { _, _ ->
            if (ppgOn) {
                //allowLogging = true
            }
            else {
                Toast.makeText(context?.applicationContext, "PPG is Not Checked in Dashboard", Toast.LENGTH_SHORT).show()
                switchPPG.isChecked = false
            }
        }

        // Checks if ECG in Dashboard is checked
        switchECG.setOnCheckedChangeListener{ _, _ ->
            if (ecgOn) {
                //allowLogging = true
            }
            else {
                Toast.makeText(context?.applicationContext, "ECG is Not Checked in Dashboard", Toast.LENGTH_SHORT).show()
                switchECG.isChecked = false
            }
        }

        // Checks if EDA in Dashboard is checked
        switchEDA.setOnCheckedChangeListener{ _, _->
            if (edaOn) {
                //allowLogging = true
            }
            else {
                Toast.makeText(context?.applicationContext, "EDA is Not Checked in Dashboard", Toast.LENGTH_SHORT).show()
                switchEDA.isChecked = false
            }
        }

        // Checks if Accelerometer in Dashboard is checked
        switchAccelerometer.setOnCheckedChangeListener { _, _ ->
            /*if (dashboardAccelSwitch) {
                allowLogging = true
                loggingAccelerometerSwitch = true
            }
            else {
                Toast.makeText(context?.applicationContext, "Accelerometer is Not Checked in Dashboard", Toast.LENGTH_SHORT).show()
                switchAccelerometer.isChecked = false
            }*/
        }

        // Checks if Temperature in Dashboard is checked
        switchTemperature.setOnCheckedChangeListener { _, _ ->
            if (tempOn) {
                //allowLogging = true
            }
            else {
                Toast.makeText(context?.applicationContext, "Temperature is Not Checked in Dashboard", Toast.LENGTH_SHORT).show()
                switchTemperature.isChecked = false
            }
        }

        switchStartLog.setOnCheckedChangeListener { _, isLoggingChecked ->
            if ( (switchPPG.isChecked || switchECG.isChecked || switchEDA.isChecked || switchTemperature.isChecked) && isLoggingChecked) {
                Toast.makeText(context?.applicationContext, "Now Logging", Toast.LENGTH_SHORT).show()
                isLoggingOn = true
            }
            else if ( (switchPPG.isChecked || switchECG.isChecked || switchEDA.isChecked || switchTemperature.isChecked) && !isLoggingChecked) {
                Toast.makeText(context?.applicationContext, "Stopped Logging", Toast.LENGTH_SHORT).show()
                isLoggingOn = false
            }
            else {
                Toast.makeText(context?.applicationContext, "Select Measurement", Toast.LENGTH_SHORT).show()
                switchStartLog.isChecked = false
            }
        }

        val DriveButton: Button = root.findViewById(R.id.DriveButton)
        DriveButton.setOnClickListener{
            //export("ECG")
            val directory = File(f)
            val files = directory.listFiles()

//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.type = "*/*"
//            startActivityForResult(intent, REQUEST_CODE)

            val intent = Intent(context?.applicationContext, ShareLog::class.java)
            intent.putExtra("files", files)
            startActivity(intent)
        }
        return root
    }

    fun recordVital(timestamp: Long, data: Int) {
        ecg_ppg_tempSeconds += timestamp.toDouble() / (1e9).toDouble()
        ecg_ppg_tempData.append(ecg_ppg_tempSeconds)
        ecg_ppg_tempData.append(",")
        ecg_ppg_tempData.append(data)
        ecg_ppg_tempData
    }

    fun recordVital(timestamp: Long, data: Float) {
        ecg_ppg_tempSeconds += timestamp.toDouble() / (1e9).toDouble()
        ecg_ppg_tempData.append(ecg_ppg_tempSeconds)
        ecg_ppg_tempData.append(",")
        ecg_ppg_tempData.append(data)
        ecg_ppg_tempData
    }

    fun writeToFile(vital: String, dataFile: String) {

        when(vital) {
            "PPG" -> {
                val out: FileOutputStream = requireActivity().applicationContext.openFileOutput(dataFile, Context.MODE_PRIVATE)
                out.write(ecg_ppg_tempData.toString().toByteArray())
                out.close()
            }
            "ECG" -> {
                val out: FileOutputStream = requireActivity().applicationContext.openFileOutput(dataFile, Context.MODE_PRIVATE)
                out.write(ecg_ppg_tempData.toString().toByteArray())
                out.close()
            }
            "EDA" -> {
                /*val out: FileOutputStream = requireActivity().applicationContext.openFileOutput(dataFile, Context.MODE_PRIVATE)
                out.write(ecg_ppg_tempData.toString().toByteArray())
                out.close()*/
            }
            "Accelerometer" -> {
                /*val out: FileOutputStream = requireActivity().applicationContext.openFileOutput(dataFile, Context.MODE_PRIVATE)
                out.write(ecg_ppg_tempData.toString().toByteArray())
                out.close()*/
            }
            "Temperature" -> {
                val out: FileOutputStream = requireActivity().applicationContext.openFileOutput(dataFile, Context.MODE_PRIVATE)
                out.write(ecg_ppg_tempData.toString().toByteArray())
                out.close()
            }
        }
    }

    @SuppressLint("NewApi")
    fun export(dataFile: String) {
        //val currentDateTime = LocalDateTime.now()
        //val fileName: String = dataFile + "Data_" + currentDateTime + ".csv"

        try {
            val context: Context = requireActivity().applicationContext
            val fileLocation = File(context.filesDir, dataFile)
            val path: Uri = FileProvider.getUriForFile(context, "com.example.exportcsv.fileprovider", fileLocation)
            val fileIntent = Intent(Intent.ACTION_SEND)
            fileIntent.type = "text/csv"
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            fileIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            fileIntent.putExtra(Intent.EXTRA_STREAM, path)
            val chooser = Intent.createChooser(fileIntent, "Share File")
            val resInfoList: List<ResolveInfo> = context?.packageManager.queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(packageName, path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                context.grantUriPermission(packageName, path, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            //fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data")
            //startActivity(Intent.createChooser(fileIntent, "Send mail"))
            startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}