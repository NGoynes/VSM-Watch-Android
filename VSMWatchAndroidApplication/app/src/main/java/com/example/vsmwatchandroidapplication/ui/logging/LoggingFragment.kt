package com.example.vsmwatchandroidapplication.ui.logging

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.RequiresApi
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

    // File output
    private lateinit var fileOut: File
    private var header = StringBuilder()

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
                    header.append("Time (s)")
                    header.append(",")
                    header.append("PPG Amplitude (LSBs)")
                    header.append('\n')

                    val currentDateTime = LocalDateTime.now()
                    val fileName = "PPGData$currentDateTime.csv"
                    fileOut = File(context!!.filesDir, fileName)
                    fileOut.createNewFile()
                    fileOut.appendText(header.toString())
                }
                if (switchECG.isChecked) {
                    header.append("Time (s)")
                    header.append(",")
                    header.append("ECG Amplitude")
                    header.append('\n')

                    val currentDateTime = LocalDateTime.now()
                    val fileName = "ECGData$currentDateTime.csv"
                    fileOut = File(context!!.filesDir, fileName)
                    fileOut.createNewFile()
                    fileOut.appendText(header.toString())
                }
                if (switchEDA.isChecked) {
                    header.append("Time (s)")
                    header.append(",")
                    header.append("Imp Real (Ohms)")
                    header.append(",")
                    header.append("Imp Img (Ohms)")
                    header.append(",")
                    header.append("Imp Magnitude (Ohms)")
                    header.append(",")
                    header.append("Imp Phase (Rad)")
                    header.append('\n')

                    val currentDateTime = LocalDateTime.now()
                    val fileName = "EDAData$currentDateTime.csv"
                    fileOut = File(context!!.filesDir, fileName)
                    fileOut.createNewFile()
                    fileOut.appendText(header.toString())
                }
                if (switchTemperature.isChecked) {
                    header.append("Time (s)")
                    header.append(",")
                    if(tempCel){
                        header.append("Temperature (C)")
                    }
                    else{
                        header.append("Temperature (F)")
                    }
                    header.append('\n')

                    val currentDateTime = LocalDateTime.now()
                    val fileName = "TemperatureData$currentDateTime.csv"
                    fileOut = File(context!!.filesDir, fileName)
                    fileOut.createNewFile()
                    fileOut.appendText(header.toString())
                }
                isLoggingOn = true
            }
            else if ( (switchPPG.isChecked || switchECG.isChecked || switchEDA.isChecked || switchTemperature.isChecked) && !isLoggingChecked) {
                isLoggingOn = false
                switchPPG.isChecked = false
                switchECG.isChecked = false
                switchEDA.isChecked = false
                switchTemperature.isChecked = false
                header.clear()

                if (ppgOn) { // If PPG was on, log data to csv right away
                    Toast.makeText(context?.applicationContext, "PPG Successfully Logged", Toast.LENGTH_SHORT).show()
                }
                else if (ecgOn) { // If ECG was on, log data to csv right away
                    Toast.makeText(context?.applicationContext, "ECG Successfully Logged", Toast.LENGTH_SHORT).show()
                }
                else if (edaOn) {
                    Toast.makeText(context?.applicationContext, "EDA Successfully Logged", Toast.LENGTH_SHORT).show()
                }
                else if (tempOn) {
                    Toast.makeText(context?.applicationContext, "Temperature Successfully Logged", Toast.LENGTH_SHORT).show()
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
        fileOut.appendText(timestamp.toString())
        fileOut.appendText(",")
        fileOut.appendText(data.toString())
        fileOut.appendText("\n")
    }

    fun recordVital(timestamp: Long, data: Long) {
        fileOut.appendText(timestamp.toString())
        fileOut.appendText(",")
        fileOut.appendText(data.toString())
        fileOut.appendText("\n")
    }

    fun recordVital(timestamp: Long, data: Float) {
        fileOut.appendText(timestamp.toString())
        fileOut.appendText(",")
        fileOut.appendText(data.toString())
        fileOut.appendText("\n")
    }

    fun recordVital(timestamp: Long, real: Int, imaginary: Int, mag: Float, phase: Float) {
        fileOut.appendText(timestamp.toString())
        fileOut.appendText(",")
        fileOut.appendText(real.toString())
        fileOut.appendText(",")
        fileOut.appendText(imaginary.toString())
        fileOut.appendText(",")
        fileOut.appendText(mag.toString())
        fileOut.appendText(",")
        fileOut.appendText(phase.toString())
        fileOut.appendText("\n")
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