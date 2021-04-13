package com.example.vsmwatchandroidapplication

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
//import com.analog.study_watch_sdk.core.SDK
import com.example.vsmwatchandroidapplication.ui.chart.ChartViewModel
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    var ppgSeries1 = LineGraphSeries<DataPoint>()
    var latPPGSeries1 = String()
    var ppgSeries2 = LineGraphSeries<DataPoint>()
    var latPPGSeries2 = String()
    var ecgSeries = LineGraphSeries<DataPoint>()
    var latEcgSeries = String()
    var edaSeriesMag = LineGraphSeries<DataPoint>()
    var latEdaSeries = String()
    var edaSeriesPhase = LineGraphSeries<DataPoint>()
    var accSeriesX = LineGraphSeries<DataPoint>()
    var latAccSeriesX = String()
    var accSeriesY = LineGraphSeries<DataPoint>()
    var latAccSeriesY = String()
    var accSeriesZ = LineGraphSeries<DataPoint>()
    var latAccSeriesZ = String()
    var accSeriesMag = LineGraphSeries<DataPoint>()
    var tempSeries = LineGraphSeries<DataPoint>()
    var latTempSeries = String()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(setOf(
//                R.id.navigation_dashboard, R.id.navigation_chart, R.id.navigation_logging, R.id.navigation_settings))
//        setupActionBarWithNavController(navController, appBarConfiguration)
        val bar = setSupportActionBar(findViewById(R.id.my_toolbar))
        //supportActionBar?.setDisplayShowTitleEnabled(false)

        navView.setupWithNavController(navController)

        readHealthData()
    }

    fun readHealthData() {
        //read ppg
        var file: InputStream = resources.openRawResource(R.raw.adpd)
        var rows: List<List<String>> = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            ppgSeries1.appendData(DataPoint(time, rows[i][2].toDouble()),true, rows.size)
            ppgSeries2.appendData(DataPoint(time, rows[i][4].toDouble()),true, rows.size)
        }
        latPPGSeries1 = rows[rows.size-1][2]
        latPPGSeries2 = rows[rows.size-1][4]

        //read ecg
        file = resources.openRawResource(R.raw.ecg)
        rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            ecgSeries.appendData(DataPoint(time, rows[i][2].toDouble()),true, rows.size)
        }
        latEcgSeries = rows[rows.size-1][2]
        //read eda
        file = resources.openRawResource(R.raw.eda)
        rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            edaSeriesMag.appendData(DataPoint(time, rows[i][3].toDouble()),true, rows.size)
            edaSeriesPhase.appendData(DataPoint(time, rows[i][4].toDouble()),true, rows.size)
        }
        latEdaSeries = rows[rows.size-1][3]

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
        latAccSeriesX = rows[rows.size-1][1]
        latAccSeriesY = rows[rows.size-1][2]
        latAccSeriesZ = rows[rows.size-1][3]

        //read temp
        file = resources.openRawResource(R.raw.temp)
        rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            tempSeries.appendData(DataPoint(time, rows[i][1].toDouble()),true, rows.size)

        }
        latTempSeries = rows[rows.size-1][1]
    }
}
