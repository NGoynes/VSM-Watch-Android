        package com.example.vsmwatchandroidapplication.ui.chart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.vsmwatchandroidapplication.R
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.io.InputStream
import java.lang.Double.max
import java.lang.Double.min

        class ChartFragment : Fragment() {

    private lateinit var chartViewModel: ChartViewModel
    private var ppgSeries1 = LineGraphSeries<DataPoint>()
    private var ppgSeries2 = LineGraphSeries<DataPoint>()
    private var ecgSeries = LineGraphSeries<DataPoint>()
    private var edaSeriesMag = LineGraphSeries<DataPoint>()
    private var edaSeriesPhase = LineGraphSeries<DataPoint>()
    private var accSeriesX = LineGraphSeries<DataPoint>()
    private var accSeriesY = LineGraphSeries<DataPoint>()
    private var accSeriesZ = LineGraphSeries<DataPoint>()
    private var accSeriesMag = LineGraphSeries<DataPoint>()
    private var tempSeries = LineGraphSeries<DataPoint>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        chartViewModel =
                ViewModelProvider(this).get(ChartViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_chart, container, false)


        val ppgChart: GraphView = root.findViewById((R.id.ppgChart))
        val ecgChart: GraphView = root.findViewById((R.id.ecgChart))
        val edaPhaseChart: GraphView = root.findViewById((R.id.edaPhaseChart))
        val edaMagChart: GraphView = root.findViewById((R.id.edaMagChart))
        val accChart: GraphView = root.findViewById((R.id.accChart))
        val tempChart: GraphView = root.findViewById((R.id.tempChart))

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
        ppgChart.viewport.setMinY(min(ppgSeries1.lowestValueY, ppgSeries2.lowestValueY))
        ppgChart.viewport.setMaxY(max(ppgSeries1.highestValueY, ppgSeries2.highestValueY))
        ppgChart.viewport.setMinX(min(ppgSeries1.lowestValueX, ppgSeries2.lowestValueX))
        ppgChart.viewport.setMaxX(max(ppgSeries1.highestValueX, ppgSeries2.highestValueX))
        ppgChart.viewport.isScrollable = true
        ppgChart.addSeries(ppgSeries2)
        ppgChart.addSeries(ppgSeries1)

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
        ecgChart.viewport.setMaxX(ecgSeries.highestValueX / 1.5)
        ecgChart.viewport.isScrollable = true
        ecgChart.addSeries(ecgSeries)

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
        accChart.viewport.setMinY(min(min(accSeriesX.lowestValueY, accSeriesY.lowestValueY), accSeriesZ.lowestValueY))
        accChart.viewport.setMaxY(max(max(accSeriesX.highestValueY, accSeriesY.highestValueY), accSeriesZ.highestValueY))
        accChart.viewport.setMinX(min(min(accSeriesX.lowestValueX, accSeriesY.lowestValueX), accSeriesZ.lowestValueX))
        accChart.viewport.setMaxX(max(max(accSeriesX.highestValueX, accSeriesY.highestValueX), accSeriesY.highestValueX))
        accChart.viewport.isScrollable = true
        accChart.addSeries(accSeriesX)
        accChart.addSeries(accSeriesY)
        accChart.addSeries(accSeriesZ)

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

        return root
    }

    private fun readHealthData() {
        //read ppg
        var file: InputStream = resources.openRawResource(R.raw.adpd)
        var rows: List<List<String>> = csvReader().readAll(file)
        var end = rows.size - 1
        rows = rows.slice(3..end)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            ppgSeries1.appendData(DataPoint(time, rows[i][2].toDouble()),true, rows.size)
            ppgSeries2.appendData(DataPoint(time, rows[i][4].toDouble()),true, rows.size)
        }

        //read ecg
        file = resources.openRawResource(R.raw.ecg)
        rows = csvReader().readAll(file)
        end = rows.size - 1
        rows = rows.slice(3..end)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            ecgSeries.appendData(DataPoint(time, rows[i][2].toDouble()),true, rows.size)
        }

        //read eda
        file = resources.openRawResource(R.raw.eda)
        rows = csvReader().readAll(file)
        end = rows.size - 1
        rows = rows.slice(3..end)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            edaSeriesMag.appendData(DataPoint(time, rows[i][3].toDouble()),true, rows.size)
            edaSeriesPhase.appendData(DataPoint(time, rows[i][4].toDouble()),true, rows.size)
        }

        //read acc
        file = resources.openRawResource(R.raw.adxl)
        rows = csvReader().readAll(file)
        end = rows.size - 1
        rows = rows.slice(3..end)
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
        end = rows.size - 1
        rows = rows.slice(3..end)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            tempSeries.appendData(DataPoint(time, rows[i][1].toDouble()),true, rows.size)
        }
    }
}