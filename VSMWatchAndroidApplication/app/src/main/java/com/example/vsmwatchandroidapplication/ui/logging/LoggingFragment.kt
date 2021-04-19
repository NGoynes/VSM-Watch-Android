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
import android.widget.CompoundButton
import android.widget.Switch
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.vsmwatchandroidapplication.MainActivity
import com.example.vsmwatchandroidapplication.ui.dashboard.ScanFragment
//import androidx.core.app.ApplicationProvider.getApplicationContext
import com.example.vsmwatchandroidapplication.R
import java.io.File
import java.io.FileOutputStream
import com.analog.study_watch_sdk.StudyWatch
import com.analog.study_watch_sdk.core.SDK
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback
import kotlinx.android.synthetic.main.activity_scan.*
import org.jetbrains.anko.alert


@SuppressLint("UseSwitchCompatOrMaterialCode")
class LoggingFragment : Fragment() {

    private lateinit var switchTemperature: Switch
    private var mainActivity = MainActivity()
    private var watchSDK = mainActivity.watchSdk
    private var isConnected = mainActivity.isConnected

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_logging, container, false)

        (activity as MainActivity)?.supportActionBar?.title = "Logging"

        switchTemperature = root.findViewById(R.id.switch_Temperature)

        switchTemperature.setOnCheckedChangeListener{ _, isChecked ->
            if (isConnected && isChecked) {
                println("CONNECTED")
            }
            else if(isChecked){
                println("NOT CONNECTED")
            }
        }
        //switchTemperature.setOnClickListener{
            //val intent: Intent = Intent(context?.applicationContext, TemperatureLog::class.java)

            //startActivity(intent)
        //}

        val DriveButton: Button = root.findViewById(R.id.DriveButton)
        DriveButton.setOnClickListener{
            export()
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
    fun export() {
        //generate data
        val data = StringBuilder()
        data.append("Time,Distance")
        try {
            //saving the file into device
            val out: FileOutputStream = requireActivity().applicationContext.openFileOutput("data.csv", Context.MODE_PRIVATE)
            out.write(data.toString().toByteArray())
            out.close()

            //exporting
            val context: Context = requireActivity().applicationContext
            val filelocation = File(context.filesDir, "data.csv")
            val path: Uri = FileProvider.getUriForFile(context, "com.example.exportcsv.fileprovider", filelocation)
            val fileIntent = Intent(Intent.ACTION_SEND)
            fileIntent.type = "text/csv"
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data")
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            fileIntent.putExtra(Intent.EXTRA_STREAM, path)
            startActivity(Intent.createChooser(fileIntent, "Send mail"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}