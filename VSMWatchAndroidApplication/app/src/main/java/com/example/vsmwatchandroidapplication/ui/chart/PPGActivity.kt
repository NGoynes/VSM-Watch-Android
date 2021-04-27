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

class PPGActivity : AppCompatActivity() {

    var ppgSeries1 = LineGraphSeries<DataPoint>()
    var ppgSeries2 = LineGraphSeries<DataPoint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ppg)

        val ppgChart: GraphView = findViewById((R.id.ppgChartInd))

        readHealthData()

        //PPG PLOT
        ppgSeries1.color = Color.rgb(255, 51, 0)
        ppgSeries1.isDrawBackground = true
        ppgSeries1.backgroundColor = Color.argb(150, 255, 133, 102)
        ppgSeries1.thickness = 10
        ppgSeries1.title = "S1"
        ppgSeries2.color = Color.rgb(51, 153, 255)
        ppgSeries2.isDrawBackground = true
        ppgSeries2.backgroundColor = Color.argb(150, 128, 191, 255)
        ppgSeries2.thickness = 10
        ppgSeries2.title = "S2"
        ppgChart.title = "PPG"
        ppgChart.gridLabelRenderer.horizontalAxisTitle = "Time (s)"
        ppgChart.gridLabelRenderer.verticalAxisTitle = "PPG"
        ppgChart.gridLabelRenderer.numVerticalLabels = 3
        ppgChart.legendRenderer.isVisible = true
        ppgChart.viewport.isYAxisBoundsManual = true
        ppgChart.viewport.isXAxisBoundsManual = true
        ppgChart.viewport.setMinY(Double.min(ppgSeries1.lowestValueY, ppgSeries2.lowestValueY))
        ppgChart.viewport.setMaxY(Double.max(ppgSeries1.highestValueY, ppgSeries2.highestValueY))
        ppgChart.viewport.setMinX(Double.min(ppgSeries1.lowestValueX, ppgSeries2.lowestValueX))
        ppgChart.viewport.setMaxX(Double.max(ppgSeries1.highestValueX, ppgSeries2.highestValueX))
        ppgChart.viewport.isScrollable = true
        ppgChart.addSeries(ppgSeries2)
        ppgChart.addSeries(ppgSeries1)
    }

    private fun readHealthData() {
        //read ppg
        var file: InputStream = resources.openRawResource(R.raw.adpd)
        var rows: List<List<String>> = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            ppgSeries1.appendData(DataPoint(time, rows[i][2].toDouble()), true, rows.size)
            ppgSeries2.appendData(DataPoint(time, rows[i][4].toDouble()), true, rows.size)
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