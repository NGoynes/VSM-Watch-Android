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

class EDAPhaseActivity : AppCompatActivity() {

    private var edaSeriesPhase = LineGraphSeries<DataPoint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eda_phase)

        val edaPhaseChart: GraphView = findViewById((R.id.edaPhaseChartInd))

        readHealthData()

        //EDA PHASE PLOT
        edaSeriesPhase.color = Color.rgb(51, 153, 255)
        edaSeriesPhase.isDrawBackground = true
        edaSeriesPhase.backgroundColor = Color.argb(150, 128, 191, 255)
        edaSeriesPhase.thickness = 10
        edaPhaseChart.title = "EDA PHASE"
        edaPhaseChart.gridLabelRenderer.horizontalAxisTitle = "Time (s)"
        edaPhaseChart.gridLabelRenderer.verticalAxisTitle = "Imp. Phase (Rad)"
        edaPhaseChart.gridLabelRenderer.numVerticalLabels = 3
        edaPhaseChart.viewport.isYAxisBoundsManual = true
        edaPhaseChart.viewport.isXAxisBoundsManual = true
        edaPhaseChart.viewport.setMinY(edaSeriesPhase.lowestValueY)
        edaPhaseChart.viewport.setMaxY(edaSeriesPhase.highestValueY)
        edaPhaseChart.viewport.setMinX(edaSeriesPhase.lowestValueX)
        edaPhaseChart.viewport.setMaxX(edaSeriesPhase.highestValueX)
        edaPhaseChart.viewport.isScrollable = true
        edaPhaseChart.addSeries(edaSeriesPhase)
    }

    private fun readHealthData() {
        //read eda
        var file = resources.openRawResource(R.raw.eda)
        var rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            edaSeriesPhase.appendData(DataPoint(time, rows[i][4].toDouble()),true, rows.size)
        }
    }
}