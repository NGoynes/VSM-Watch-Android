package com.example.vsmwatchandroidapplication.ui.logging

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
        return root
    }
}