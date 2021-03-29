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

class ChartFragment : Fragment() {

    private lateinit var chartViewModel: ChartViewModel
    private var ppgSeries = LineGraphSeries<DataPoint>()
    private var ecgSeries = LineGraphSeries<DataPoint>()
    private var edaSeries = LineGraphSeries<DataPoint>()

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
        val edaChart: GraphView = root.findViewById((R.id.edaChart))

        readHealthData()

        //PPG PLOT
        ppgSeries.color = Color.rgb(233, 87, 87)
        ppgSeries.isDrawBackground = true
        ppgSeries.backgroundColor = Color.rgb(233, 179, 179)
        ppgSeries.thickness = 10
        ppgChart.title = "PPG"
        ppgChart.gridLabelRenderer.horizontalAxisTitle = "Time (s)"
        ppgChart.gridLabelRenderer.verticalAxisTitle = "Impedance Real (Ohms)"
        ppgChart.gridLabelRenderer.numVerticalLabels = 3
        ppgChart.gridLabelRenderer.numHorizontalLabels = 3
        ppgChart.viewport.isYAxisBoundsManual = true
        ppgChart.viewport.setMinY(0.0)
        ppgChart.viewport.setMaxY(200000.0)
        ppgChart.viewport.isXAxisBoundsManual = true
        ppgChart.viewport.setMinX(0.0)
        ppgChart.viewport.setMaxX(200.0)
        ppgChart.viewport.isScrollable = true
        ppgChart.addSeries(ppgSeries)

        //ECG PLOT
        ecgSeries.color = Color.rgb(233, 87, 87)
        ecgSeries.isDrawBackground = true
        ecgSeries.backgroundColor = Color.rgb(233, 179, 179)
        ecgSeries.thickness = 10
        ecgChart.title = "ECG"
        ecgChart.gridLabelRenderer.horizontalAxisTitle = "Time (s)"
        ecgChart.gridLabelRenderer.verticalAxisTitle = "Impedance Real (Ohms)"
        ecgChart.gridLabelRenderer.numVerticalLabels = 3
        ecgChart.gridLabelRenderer.numHorizontalLabels = 3
        ecgChart.viewport.isYAxisBoundsManual = true
        ecgChart.viewport.setMinY(0.0)
        ecgChart.viewport.setMaxY(200000.0)
        ecgChart.viewport.isXAxisBoundsManual = true
        ecgChart.viewport.setMinX(0.0)
        ecgChart.viewport.setMaxX(200.0)
        ecgChart.viewport.isScrollable = true
        ecgChart.addSeries(ecgSeries)

        //EDA PLOT
        edaSeries.color = Color.rgb(233, 87, 87)
        edaSeries.isDrawBackground = true
        edaSeries.backgroundColor = Color.rgb(233, 179, 179)
        edaSeries.thickness = 10
        edaChart.title = "EDA"
        edaChart.gridLabelRenderer.horizontalAxisTitle = "Time (s)"
        edaChart.gridLabelRenderer.verticalAxisTitle = "Impedance Real (Ohms)"
        edaChart.gridLabelRenderer.numVerticalLabels = 3
        edaChart.gridLabelRenderer.numHorizontalLabels = 3
        edaChart.viewport.isYAxisBoundsManual = true
        edaChart.viewport.setMinY(0.0)
        edaChart.viewport.setMaxY(200000.0)
        edaChart.viewport.isXAxisBoundsManual = true
        edaChart.viewport.setMinX(0.0)
        edaChart.viewport.setMaxX(200.0)
        edaChart.viewport.isScrollable = true
        edaChart.addSeries(edaSeries)


        return root
    }

    private fun readHealthData() {
        val file: InputStream = resources.openRawResource(R.raw.eda)
        val rows: List<List<String>> = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            ppgSeries.appendData(DataPoint(time, rows[i][1].toDouble()),true, rows.size)
            ecgSeries.appendData(DataPoint(time, rows[i][1].toDouble()),true, rows.size)
            edaSeries.appendData(DataPoint(time, rows[i][1].toDouble()),true, rows.size)
        }
    }
}