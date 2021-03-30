        package com.example.vsmwatchandroidapplication.ui.chart

import android.os.Bundle
import android.renderscript.ScriptGroup
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.vsmwatchandroidapplication.R
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.mikephil.charting.charts.LineChart
import java.io.File
import android.util.Log
import java.io.InputStream

class ChartFragment : Fragment() {

    private lateinit var chartViewModel: ChartViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        chartViewModel =
                ViewModelProvider(this).get(ChartViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_chart, container, false)
        val lineChart: LineChart = root.findViewById((R.id.lineChart))
        return root
    }

}