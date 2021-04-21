package com.example.vsmwatchandroidapplication.ui.logging

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.analog.study_watch_sdk.StudyWatch
import com.analog.study_watch_sdk.core.SDK
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback
import com.example.vsmwatchandroidapplication.*
import kotlinx.android.synthetic.main.activity_scan.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange


@SuppressLint("UseSwitchCompatOrMaterialCode")
class LoggingFragment : Fragment() {

    private lateinit var switchStartLog: Switch
    private lateinit var switchPPG: Switch
    private lateinit var switchECG: Switch
    private lateinit var switchEDA: Switch
    private lateinit var switchAccelerometer: Switch
    private lateinit var switchPedometer: Switch
    private lateinit var switchTemperature: Switch

    private var allowLogging: Boolean = false

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_logging, container, false)

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
            if (dashboardPPGSwitch) {
                allowLogging = true
                loggingPPGSwitch = true
            }
            else {
                Toast.makeText(context?.applicationContext, "PPG is Not Checked in Dashboard", Toast.LENGTH_SHORT).show()
                switchPPG.isChecked = false
            }
        }

        // Checks if ECG in Dashboard is checked
        switchECG.setOnCheckedChangeListener{ _, _ ->
            if (dashboardECGSwitch) {
                allowLogging = true
                loggingECGSwitch = true
            }
            else {
                Toast.makeText(context?.applicationContext, "ECG is Not Checked in Dashboard", Toast.LENGTH_SHORT).show()
                switchECG.isChecked = false
            }
        }

        // Checks if EDA in Dashboard is checked
        switchEDA.setOnCheckedChangeListener{ _, _ ->
            if (dashboardEDASwitch) {
                allowLogging = true
                loggingEDASwitch = true
            }
            else {
                Toast.makeText(context?.applicationContext, "EDA is Not Checked in Dashboard", Toast.LENGTH_SHORT).show()
                switchEDA.isChecked = false
            }
        }

        // Checks if Accelerometer in Dashboard is checked
        switchAccelerometer.setOnCheckedChangeListener { _, _ ->
            if (dashboardAccelSwitch) {
                allowLogging = true
                loggingAccelerometerSwitch = true
            }
            else {
                Toast.makeText(context?.applicationContext, "Accelerometer is Not Checked in Dashboard", Toast.LENGTH_SHORT).show()
                switchAccelerometer.isChecked = false
            }
        }

        // Checks if Temperature in Dashboard is checked
        switchTemperature.setOnCheckedChangeListener { _, _ ->
            if (dashboardTempSwitch) {
                allowLogging = true
                loggingTempSwitch = true
            }
            else {
                Toast.makeText(context?.applicationContext, "Temperature is Not Checked in Dashboard", Toast.LENGTH_SHORT).show()
                switchTemperature.isChecked = false
            }
        }

        switchStartLog.setOnCheckedChangeListener { _, _ ->
            if (watchConnection && allowLogging) {
                Toast.makeText(context?.applicationContext, "Now Logging", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context?.applicationContext, "No Watch Connected", Toast.LENGTH_SHORT).show()
                switchStartLog.isChecked = false
            }
        }

        val DriveButton: Button = root.findViewById(R.id.DriveButton)
        DriveButton.setOnClickListener{
            //export()
        }
        return root
    }

    fun buttonShareText() {
        val intentShare = Intent(Intent.ACTION_SEND)
        intentShare.type = "text/plain"
        intentShare.putExtra(Intent.EXTRA_SUBJECT, "Logging Files")
        intentShare.putExtra(
            Intent.EXTRA_TEXT,
            ".csv of logged data"
        )
        startActivity(Intent.createChooser(intentShare, "Shared the text ..."))
    }

    fun record(readData: String, vitalName: String) {
        //generate data
        val data = StringBuilder()
        val fileName: String = vitalName + "Data.csv"

        data.append(readData)
        try {
            //saving the file into device
            val out: FileOutputStream = requireActivity().applicationContext.openFileOutput(fileName, Context.MODE_PRIVATE)
            out.write(data.toString().toByteArray())
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun export(dataFile: String) {
        val context: Context = requireActivity().applicationContext
        val fileLocation = File(context.filesDir, dataFile)
        val path: Uri = FileProvider.getUriForFile(context, "com.example.exportcsv.fileprovider", fileLocation)
        val fileIntent = Intent(Intent.ACTION_SEND)
        fileIntent.type = "text/csv"
        fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data")
        fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        fileIntent.putExtra(Intent.EXTRA_STREAM, path)
        startActivity(Intent.createChooser(fileIntent, "Send mail"))
    }
}