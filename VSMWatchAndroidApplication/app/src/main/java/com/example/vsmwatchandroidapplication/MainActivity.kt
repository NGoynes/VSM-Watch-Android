package com.example.vsmwatchandroidapplication

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.vsmwatchandroidapplication.ui.chart.ChartViewModel
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    var ppgSeries1 = LineGraphSeries<DataPoint>()
    var ppgSeries2 = LineGraphSeries<DataPoint>()
    var ecgSeries = LineGraphSeries<DataPoint>()
    var edaSeriesMag = LineGraphSeries<DataPoint>()
    var edaSeriesPhase = LineGraphSeries<DataPoint>()
    var accSeriesX = LineGraphSeries<DataPoint>()
    var accSeriesY = LineGraphSeries<DataPoint>()
    var accSeriesZ = LineGraphSeries<DataPoint>()
    var accSeriesMag = LineGraphSeries<DataPoint>()
    var tempSeries = LineGraphSeries<DataPoint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_dashboard, R.id.navigation_chart, R.id.navigation_logging, R.id.navigation_settings))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        readHealthData()
    }

    private fun readHealthData() {
        //read ppg
        var file: InputStream = resources.openRawResource(R.raw.adpd)
        var rows: List<List<String>> = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            ppgSeries1.appendData(DataPoint(time, rows[i][2].toDouble()),true, rows.size)
            ppgSeries2.appendData(DataPoint(time, rows[i][4].toDouble()),true, rows.size)
        }

        //read ecg
        file = resources.openRawResource(R.raw.ecg)
        rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            ecgSeries.appendData(DataPoint(time, rows[i][2].toDouble()),true, rows.size)
        }

        //read eda
        file = resources.openRawResource(R.raw.eda)
        rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            edaSeriesMag.appendData(DataPoint(time, rows[i][3].toDouble()),true, rows.size)
            edaSeriesPhase.appendData(DataPoint(time, rows[i][4].toDouble()),true, rows.size)
        }

        //read acc
        file = resources.openRawResource(R.raw.adxl)
        rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            accSeriesX.appendData(DataPoint(time, rows[i][1].toDouble()),true, rows.size)
            accSeriesY.appendData(DataPoint(time, rows[i][2].toDouble()),true, rows.size)
            accSeriesZ.appendData(DataPoint(time, rows[i][3].toDouble()),true, rows.size)
            accSeriesMag.appendData(DataPoint(time, rows[i][4].toDouble()),true, rows.size)
        }

        //read temp
        file = resources.openRawResource(R.raw.temp)
        rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            tempSeries.appendData(DataPoint(time, rows[i][1].toDouble()),true, rows.size)
        }
    }
}