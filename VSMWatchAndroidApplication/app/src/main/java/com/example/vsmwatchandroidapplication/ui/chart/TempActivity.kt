package com.example.vsmwatchandroidapplication.ui.chart

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vsmwatchandroidapplication.R
import com.example.vsmwatchandroidapplication.cf
import com.example.vsmwatchandroidapplication.fragman
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.io.InputStream
import java.lang.Double

class TempActivity : AppCompatActivity() {

    private var tempSeries = LineGraphSeries<DataPoint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp)

        val tempChart: GraphView = findViewById((R.id.tempChartInd))

        readHealthData()

        //TEMP PLOT
        tempSeries.color = Color.rgb(51, 153, 255)
        tempSeries.isDrawBackground = true
        tempSeries.backgroundColor = Color.argb(150, 128, 191, 255)
        tempSeries.thickness = 10
        tempChart.title = "TEMPERATURE"
        tempChart.gridLabelRenderer.horizontalAxisTitle = "Time (s)"
        tempChart.gridLabelRenderer.verticalAxisTitle = "Temperature (C)"
        tempChart.gridLabelRenderer.numVerticalLabels = 3
        tempChart.viewport.isYAxisBoundsManual = true
        tempChart.viewport.isXAxisBoundsManual = true
        tempChart.viewport.setMinY(tempSeries.lowestValueY)
        tempChart.viewport.setMaxY(tempSeries.highestValueY)
        tempChart.viewport.setMinX(tempSeries.lowestValueX)
        tempChart.viewport.setMaxX(tempSeries.highestValueX)
        tempChart.viewport.isScrollable = true
        tempChart.addSeries(tempSeries)
    }

    private fun readHealthData() {
        //read temp
        var file = resources.openRawResource(R.raw.temp)
        var rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            tempSeries.appendData(DataPoint(time, rows[i][1].toDouble()),true, rows.size)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        fragman!!
                .beginTransaction()
                .show(cf as ChartFragment)
                .commit()
    }
}