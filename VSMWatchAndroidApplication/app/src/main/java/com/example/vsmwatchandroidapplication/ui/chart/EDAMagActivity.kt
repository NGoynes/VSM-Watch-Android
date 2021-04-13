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

class EDAMagActivity : AppCompatActivity() {

    private var edaSeriesMag = LineGraphSeries<DataPoint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eda_mag)

        val edaMagChart: GraphView = findViewById((R.id.edaMagChartInd))

        readHealthData()

        //EDA MAG PLOT
        edaSeriesMag.color = Color.rgb(255, 51, 0)
        edaSeriesMag.isDrawBackground = true
        edaSeriesMag.backgroundColor = Color.argb(200, 233, 179, 179)
        edaSeriesMag.thickness = 10
        edaMagChart.title = "EDA MAGNITUDE"
        edaMagChart.gridLabelRenderer.horizontalAxisTitle = "Time (s)"
        edaMagChart.gridLabelRenderer.verticalAxisTitle = "Imp. Mag. (Ohms)"
        edaMagChart.gridLabelRenderer.numVerticalLabels = 3
        edaMagChart.viewport.isYAxisBoundsManual = true
        edaMagChart.viewport.isXAxisBoundsManual = true
        edaMagChart.viewport.setMinY(edaSeriesMag.lowestValueY)
        edaMagChart.viewport.setMaxY(edaSeriesMag.highestValueY)
        edaMagChart.viewport.setMinX(edaSeriesMag.lowestValueX)
        edaMagChart.viewport.setMaxX(edaSeriesMag.highestValueX)
        edaMagChart.viewport.isScrollable = true
        edaMagChart.addSeries(edaSeriesMag)
    }

    private fun readHealthData() {
        //read eda
        var file = resources.openRawResource(R.raw.eda)
        var rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            edaSeriesMag.appendData(DataPoint(time, rows[i][3].toDouble()),true, rows.size)
        }
    }
}