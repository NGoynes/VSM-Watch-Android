package com.example.vsmwatchandroidapplication.ui.chart

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vsmwatchandroidapplication.R
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.io.InputStream
import java.lang.Double

class ECGActivity : AppCompatActivity() {

    private var ecgSeries = LineGraphSeries<DataPoint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ecg)

        val ecgChart: GraphView = findViewById((R.id.ecgChartInd))

        readHealthData()

        //ECG PLOT
        ecgSeries.color = Color.rgb(255, 51, 0)
        ecgSeries.isDrawBackground = true
        ecgSeries.backgroundColor = Color.argb(200, 233, 179, 179)
        ecgSeries.thickness = 10
        ecgChart.title = "ECG"
        ecgChart.gridLabelRenderer.horizontalAxisTitle = "Time (s)"
        ecgChart.gridLabelRenderer.verticalAxisTitle = "ECG"
        ecgChart.gridLabelRenderer.numVerticalLabels = 3
        ecgChart.viewport.isYAxisBoundsManual = true
        ecgChart.viewport.isXAxisBoundsManual = true
        ecgChart.viewport.setMinY(ecgSeries.lowestValueY)
        ecgChart.viewport.setMaxY(ecgSeries.highestValueY)
        ecgChart.viewport.setMinX(ecgSeries.lowestValueX)
        ecgChart.viewport.setMaxX(ecgSeries.highestValueX)
        ecgChart.viewport.isScrollable = true
        ecgChart.addSeries(ecgSeries)
    }

    private fun readHealthData() {
        //read ecg
        var file = resources.openRawResource(R.raw.ecg)
        var rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            ecgSeries.appendData(DataPoint(time, rows[i][2].toDouble()),true, rows.size)
        }
    }
}