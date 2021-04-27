package com.example.vsmwatchandroidapplication.ui.chart

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.analog.study_watch_sdk.application.EDAApplication
import com.analog.study_watch_sdk.core.packets.stream.EDADataPacket
import com.example.vsmwatchandroidapplication.R
import com.example.vsmwatchandroidapplication.cf
import com.example.vsmwatchandroidapplication.fragman
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_chart.*
import java.io.InputStream
import java.lang.Double
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt

class EDAPhaseActivity : AppCompatActivity() {

    private lateinit var edaPhaseChart: LineChart
    private var thread: Thread = Thread()
    private var prevX = 0
    private val eda: EDAApplication = com.example.vsmwatchandroidapplication.watchSdk!!.edaApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eda_phase)

        edaPhaseChart = findViewById((R.id.edaPhaseChartInd))

        // enable description text
        edaPhaseChart.description.isEnabled = true
        edaPhaseChart.description.text = "EDA Phase Sensor Stream"
        edaPhaseChart.description.textColor = Color.WHITE

        // enable touch gestures
        edaPhaseChart.setTouchEnabled(true)

        // enable scaling and dragging
        edaPhaseChart.isDragEnabled = true
        edaPhaseChart.setScaleEnabled(true)
        edaPhaseChart.setDrawGridBackground(false)
        edaPhaseChart.isAutoScaleMinMaxEnabled = true

        // if disabled, scaling can be done on x- and y-axis separately
        edaPhaseChart.setPinchZoom(true)

        // set an alternative background color
        //ecgChart.setBackgroundColor(Color.WHITE)

        val data = LineData()
        data.setValueTextColor(Color.WHITE)

        // add empty data
        edaPhaseChart.data = data

        // get the legend (only possible after setting data)
        val l: Legend = edaPhaseChart.legend

        // modify the legend ...
        l.form = Legend.LegendForm.LINE
        l.textColor = Color.WHITE
        l.isEnabled = false

        val xl: XAxis = edaPhaseChart.xAxis
        xl.textColor = Color.WHITE
        xl.setDrawGridLines(false)
        xl.setAvoidFirstLastClipping(true)
        xl.isEnabled = true

        val leftAxis: YAxis = edaPhaseChart.axisLeft
        leftAxis.textColor = Color.WHITE
        leftAxis.setDrawGridLines(false)

        val rightAxis: YAxis = edaPhaseChart.axisRight
        rightAxis.isEnabled = false

        edaPhaseChart.setDrawBorders(true)

        feedMultiple()
    }

    private fun createSet(): LineDataSet? {
        val set = LineDataSet(null, "EDA Phase Data Stream")
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.lineWidth = 3f
        set.color = Color.rgb(255, 51, 0)
        set.fillColor = Color.rgb(233, 179, 179)
        set.fillAlpha = 250
        set.setDrawFilled(true)
        set.isHighlightEnabled = false
        set.setDrawValues(false)
        set.setDrawCircles(false)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.cubicIntensity = 0.2f
        return set
    }

    private fun addEntry(EDAdata: EDADataPacket) {
        val data: LineData = edaPhaseChart.data

        if (data != null) {
            var set = data.getDataSetByIndex(0)
            if (set == null) {
                set = createSet()
                data.addDataSet(set)
            }

            for (i in EDAdata.payload.streamData) {
                if (i != null) {
                    if (i.realData != 0) {
                        val phase = atan((i.imaginaryData / i.realData).toDouble()).toFloat()
                        data.addEntry(Entry(prevX++.toFloat(), phase), 0)
                    }
                }
            }
            data.notifyDataChanged()

            // let the chart know it's data has changed
            edaPhaseChart.notifyDataSetChanged()

            // limit the number of visible entries
            edaPhaseChart.setVisibleXRangeMaximum(150F)

            // move to the latest entry
            edaPhaseChart.moveViewToX(data.entryCount.toFloat())
        }
    }

    private fun feedMultiple() {
        if (thread != null) {
            thread.interrupt()
        }

        eda.setCallback { EDAdata ->
            runOnUiThread {
                addEntry(EDAdata)
            }
            try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }

        eda.startSensor()
        eda.subscribeStream()
    }

    override fun onPause() {
        super.onPause()
        eda.stopAndUnsubscribeStream()
        if (thread != null) {
            thread.interrupt()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        eda.stopAndUnsubscribeStream()
        fragman!!
                .beginTransaction()
                .show(cf as ChartFragment)
                .commit()
    }
}