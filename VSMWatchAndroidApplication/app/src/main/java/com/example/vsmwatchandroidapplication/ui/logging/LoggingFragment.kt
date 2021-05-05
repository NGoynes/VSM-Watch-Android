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
import java.time.LocalDateTime


@SuppressLint("UseSwitchCompatOrMaterialCode")

var isLoggingOn: Boolean = false
var hasLogged: Boolean = false

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
    private lateinit var switchTemperature: Switch

    private lateinit var driveButton: Button
    private lateinit var deleteButton: Button

    // Data for PPG, ECG, Temp
    private var ecg_ppg_tempSeconds: Double = 0.0
    private val ecg_ppg_tempData = StringBuilder()

    // Data for EDA
    private var edaSeconds: Double = 0.0
    private val edaData = StringBuilder()

    @SuppressLint("UseRequireInsteadOfGet", "NewApi")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_logging, container, false)

        val f: String = context!!.filesDir.path

        (activity as MainActivity).supportActionBar?.title = "Logging"

        // Initialize Switches
        switchStartLog = root.findViewById(R.id.switch_startLogging)
        switchPPG = root.findViewById(R.id.switch_PPG)
        switchECG = root.findViewById(R.id.switch_ECG)
        switchEDA = root.findViewById(R.id.switch_EDA)
        switchTemperature = root.findViewById(R.id.switch_Temperature)

        // Checks if PPG in Dashboard is checked
        switchPPG.setOnCheckedChangeListener { _, _ ->
            if (!ppgOn) {
                Toast.makeText(context?.applicationContext, "PPG is Not Checked in Dashboard", Toast.LENGTH_SHORT).show()
                switchPPG.isChecked = false
            }
            if (isLoggingOn && ppgOn) {
                Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
                switchPPG.isChecked = true
            }
        }

        // Checks if ECG in Dashboard is checked
        switchECG.setOnCheckedChangeListener{ _, _ ->
            if (!ecgOn) {
                Toast.makeText(context?.applicationContext, "ECG is Not Checked in Dashboard", Toast.LENGTH_SHORT).show()
                switchECG.isChecked = false
            }
            if (isLoggingOn && ecgOn) {
                Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
                switchECG.isChecked = true
            }
        }

        // Checks if EDA in Dashboard is checked
        switchEDA.setOnCheckedChangeListener{ _, _->
            if (!edaOn) {
                Toast.makeText(context?.applicationContext, "EDA is Not Checked in Dashboard", Toast.LENGTH_SHORT).show()
                switchEDA.isChecked = false
            }
            if (isLoggingOn && edaOn) {
                Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
                switchEDA.isChecked = true
            }
        }

        // Checks if Temperature in Dashboard is checked
        switchTemperature.setOnCheckedChangeListener { _, _ ->
            if (!tempOn) {
                Toast.makeText(context?.applicationContext, "Temperature is Not Checked in Dashboard", Toast.LENGTH_SHORT).show()
                switchTemperature.isChecked = false
            }
            if (isLoggingOn && tempOn) {
                Toast.makeText(context?.applicationContext, "Please Turn Off Logging First", Toast.LENGTH_SHORT).show()
                switchTemperature.isChecked = true
            }
        }

        switchStartLog.setOnCheckedChangeListener { _, isLoggingChecked ->
            if ( (switchPPG.isChecked || switchECG.isChecked || switchEDA.isChecked || switchTemperature.isChecked) && isLoggingChecked) {
                Toast.makeText(context?.applicationContext, "Now Logging", Toast.LENGTH_SHORT).show()

                if (switchPPG.isChecked) {
                    ecg_ppg_tempData.append("Time (s)")
                    ecg_ppg_tempData.append(",")
                    ecg_ppg_tempData.append("PPG")
                    ecg_ppg_tempData.append('\n')
                }
                if (switchECG.isChecked) {
                    ecg_ppg_tempData.append("Time (s)")
                    ecg_ppg_tempData.append(",")
                    ecg_ppg_tempData.append("ECG")
                    ecg_ppg_tempData.append('\n')
                }
                if (switchEDA.isChecked) {
                    edaData.append("Time (s)")
                    edaData.append(",")
                    edaData.append("Real Data")
                    edaData.append(",")
                    edaData.append("Imaginary Data")
                    edaData.append(",")
                    edaData.append("Magnitude")
                    edaData.append(",")
                    edaData.append("Phase")
                    edaData.append('\n')
                }
                if (switchTemperature.isChecked) {
                    ecg_ppg_tempData.append("Time (s)")
                    ecg_ppg_tempData.append(",")
                    if(tempCel){
                        ecg_ppg_tempData.append("Temperature (C)")
                    }
                    else{
                        ecg_ppg_tempData.append("Temperature (F)")
                    }

                    ecg_ppg_tempData.append('\n')
                }

                isLoggingOn = true
                hasLogged = true
            }
            else if ( (switchPPG.isChecked || switchECG.isChecked || switchEDA.isChecked || switchTemperature.isChecked) && !isLoggingChecked) {
                isLoggingOn = false
                switchPPG.isChecked = false
                switchECG.isChecked = false
                switchEDA.isChecked = false
                switchTemperature.isChecked = false

                if (ppgOn) { // If PPG was on, log data to csv right away
                    val currentDateTime = LocalDateTime.now()
                    val fileName = "PPGData$currentDateTime.csv"
                    writeToFile("PPG", fileName)
                }
                else if (ecgOn) { // If ECG was on, log data to csv right away
                    val currentDateTime = LocalDateTime.now()
                    val fileName = "ECGData$currentDateTime.csv"
                    writeToFile("ECG", fileName)
                }
                else if (edaOn) {
                    val currentDateTime = LocalDateTime.now()
                    val fileName = "EDAData$currentDateTime.csv"
                    writeToFile("EDA", fileName)
                }
                else if (tempOn) {
                    val currentDateTime = LocalDateTime.now()
                    val fileName = "TemperatureData$currentDateTime.csv"
                    writeToFile("Temperature", fileName)
                }
            }
            else {
                Toast.makeText(context?.applicationContext, "Select Measurement", Toast.LENGTH_SHORT).show()
                switchStartLog.isChecked = false
            }
        }

        driveButton = root.findViewById(R.id.DriveButton)
        driveButton.setOnClickListener{
            val directory = File(f)
            val files = directory.listFiles()

            val intent = Intent(context?.applicationContext, ShareLog::class.java)
            intent.putExtra("files", files)
            startActivity(intent)
        }

        deleteButton = root.findViewById(R.id.logging_deleteBttn)
        deleteButton.setOnClickListener {
            val directory = File(f)
            val files = directory.listFiles()

            val intent = Intent(context?.applicationContext, DeleteLog::class.java)
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
        ecg_ppg_tempData.append('\n')
    }

    fun recordVital(timestamp: Long, data: Long) {
        ecg_ppg_tempSeconds += timestamp.toDouble() / (1e9).toDouble()
        ecg_ppg_tempData.append(ecg_ppg_tempSeconds)
        ecg_ppg_tempData.append(",")
        ecg_ppg_tempData.append(data)
        ecg_ppg_tempData.append('\n')
    }

    fun recordVital(timestamp: Long, data: Float) {
        ecg_ppg_tempSeconds += timestamp.toDouble() / (1e9).toDouble()
        ecg_ppg_tempData.append(ecg_ppg_tempSeconds)
        ecg_ppg_tempData.append(",")
        ecg_ppg_tempData.append(data)
        ecg_ppg_tempData.append('\n')
    }

    fun recordVital(timestamp: Long, real: Int, imaginary: Int, mag: Float, phase: Float) {
        edaSeconds += timestamp.toDouble()  / (1e9).toDouble()
        edaData.append(edaSeconds)
        edaData.append(",")
        edaData.append(real)
        edaData.append(",")
        edaData.append(imaginary)
        edaData.append(",")
        edaData.append(mag)
        edaData.append(",")
        edaData.append(phase)
        edaData.append('\n')
    }

    fun writeToFile(vital: String, dataFile: String) {

        when(vital) {
            "PPG" -> {
                try {
                    val out: FileOutputStream = requireActivity().applicationContext.openFileOutput(dataFile, Context.MODE_PRIVATE)
                    out.write(ecg_ppg_tempData.toString().toByteArray())
                    out.close()
                    Toast.makeText(context?.applicationContext, "PPG File Successfully Logged", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context?.applicationContext, "PPG File Logging Failed", Toast.LENGTH_SHORT).show()
                }

                // Reset data
                ecg_ppg_tempData.clear()
                ecg_ppg_tempSeconds = 0.0
                hasLogged = false
            }
            "ECG" -> {
                try {
                    val out: FileOutputStream = requireActivity().applicationContext.openFileOutput(dataFile, Context.MODE_PRIVATE)
                    out.write(ecg_ppg_tempData.toString().toByteArray())
                    out.close()
                    Toast.makeText(context?.applicationContext, "ECG File Successfully Logged", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context?.applicationContext, "ECG File Logging Failed", Toast.LENGTH_SHORT).show()
                }

                // Reset data
                ecg_ppg_tempData.clear()
                ecg_ppg_tempSeconds = 0.0
                hasLogged = false
            }
            "EDA" -> {
                try {
                    val out: FileOutputStream = requireActivity().applicationContext.openFileOutput(dataFile, Context.MODE_PRIVATE)
                    out.write(edaData.toString().toByteArray())
                    out.close()
                    Toast.makeText(context?.applicationContext, "EDA File Successfully Logged", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context?.applicationContext, "EDA File Logging Failed", Toast.LENGTH_SHORT).show()
                }

                // Reset data
                edaData.clear()
                edaSeconds = 0.0
                hasLogged = false
            }
            "Temperature" -> {
                try {
                    val out: FileOutputStream = requireActivity().applicationContext.openFileOutput(dataFile, Context.MODE_PRIVATE)
                    out.write(ecg_ppg_tempData.toString().toByteArray())
                    out.close()
                    Toast.makeText(context?.applicationContext, "Temperature File Successfully Logged", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context?.applicationContext, "Temperature File Logging Failed", Toast.LENGTH_SHORT).show()
                }

                // Reset data
                ecg_ppg_tempData.clear()
                ecg_ppg_tempSeconds = 0.0
                hasLogged = false
            }
        }
    }

    @SuppressLint("NewApi", "QueryPermissionsNeeded")
    fun export(dataFile: String) {
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
            val resInfoList: List<ResolveInfo> = context.packageManager.queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(packageName, path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                context.grantUriPermission(packageName, path, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun delete(dataFile: String) {
        val context: Context = requireActivity().applicationContext
        val file = File(context.filesDir, dataFile)
        file.delete()
    }
}