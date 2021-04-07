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

class AccActivity : AppCompatActivity() {

    var accSeriesX = LineGraphSeries<DataPoint>()
    var accSeriesY = LineGraphSeries<DataPoint>()
    var accSeriesZ = LineGraphSeries<DataPoint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acc)

        val accChart: GraphView = findViewById((R.id.accChartInd))

        readHealthData()

        //ACC PLOT
        accSeriesX.color = Color.rgb(255, 51, 0)
        accSeriesX.thickness = 5
        accSeriesX.title = "X"
        accSeriesY.color = Color.rgb(51, 153, 255)
        accSeriesY.thickness = 5
        accSeriesY.title = "Y"
        accSeriesZ.color = Color.rgb(0, 204, 0)
        accSeriesZ.thickness = 5
        accSeriesZ.title = "Z"
        accChart.title = "ACCELEROMETER"
        accChart.gridLabelRenderer.horizontalAxisTitle = "Time (s)"
        accChart.gridLabelRenderer.verticalAxisTitle = "Accelerometer"
        accChart.gridLabelRenderer.numVerticalLabels = 3
        accChart.legendRenderer.isVisible = true
        accChart.viewport.isYAxisBoundsManual = true
        accChart.viewport.isXAxisBoundsManual = true
        accChart.viewport.setMinY(Double.min(Double.min(accSeriesX.lowestValueY, accSeriesY.lowestValueY), accSeriesZ.lowestValueY))
        accChart.viewport.setMaxY(Double.max(Double.max(accSeriesX.highestValueY, accSeriesY.highestValueY), accSeriesZ.highestValueY))
        accChart.viewport.setMinX(Double.min(Double.min(accSeriesX.lowestValueX, accSeriesY.lowestValueX), accSeriesZ.lowestValueX))
        accChart.viewport.setMaxX(Double.max(Double.max(accSeriesX.highestValueX, accSeriesY.highestValueX), accSeriesY.highestValueX))
        accChart.viewport.isScrollable = true
        accChart.addSeries(accSeriesX)
        accChart.addSeries(accSeriesY)
        accChart.addSeries(accSeriesZ)
    }

    private fun readHealthData() {
        //read acc
        var file = resources.openRawResource(R.raw.adxl)
        var rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            accSeriesX.appendData(DataPoint(time, rows[i][1].toDouble()),true, rows.size)
            accSeriesY.appendData(DataPoint(time, rows[i][2].toDouble()),true, rows.size)
            accSeriesZ.appendData(DataPoint(time, rows[i][3].toDouble()),true, rows.size)
        }
    }
}