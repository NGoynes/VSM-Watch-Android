package com.example.vsmwatchandroidapplication.ui.logging

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.vsmwatchandroidapplication.R
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.InputStream

class TemperatureLog : AppCompatActivity() {

    private lateinit var rows: List<List<String>>

    private lateinit var tempData: TextView
    private var longString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temperature_log)

        tempData = findViewById(R.id.temperatureLog)

        readCSV()

        for( i in 0 until rows.size) {
            for (j in 0 until rows[i].size) {
                if (j != rows[i].size - 1)
                    longString += rows[i][j] + ", "
                else
                    longString += rows[i][j]
            }
            longString += "\n"
        }
        tempData.text = longString
    }

    private fun readCSV() {
        val file: InputStream = resources.openRawResource(R.raw.temperaturestream)
        rows = csvReader().readAll(file)
    }
}