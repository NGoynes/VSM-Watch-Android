package com.example.vsmwatchandroidapplication.ui.logging

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.example.vsmwatchandroidapplication.R

@SuppressLint("UseSwitchCompatOrMaterialCode")
class LoggingFragment : Fragment() {

    private lateinit var switchTemperature: Switch

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_logging, container, false)

        switchTemperature = root.findViewById(R.id.switch_Temperature)

        switchTemperature.setOnClickListener{
            val intent: Intent = Intent(context?.applicationContext, TemperatureLog::class.java)

            startActivity(intent)
        }

        val DriveButton: Button = root.findViewById(R.id.DriveButton)
        DriveButton.setOnClickListener{
            buttonShareText()
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
}