package com.example.vsmwatchandroidapplication.ui.chart

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.analog.study_watch_sdk.application.EDAApplication
import com.analog.study_watch_sdk.core.packets.stream.EDADataPacket
import com.example.vsmwatchandroidapplication.R
import com.example.vsmwatchandroidapplication.cf
import com.example.vsmwatchandroidapplication.fragman
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlin.math.pow
import kotlin.math.sqrt

class EDAMagActivity : AppCompatActivity() {

    private lateinit var edaMagChart: LineChart
    private var thread: Thread = Thread()
    private var prevX = 0
    private val eda: EDAApplication = com.example.vsmwatchandroidapplication.watchSdk!!.edaApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eda_mag)

        edaMagChart = findViewById((R.id.edaMagChartInd))

        // enable description text
        edaMagChart.description.isEnabled = true
        edaMagChart.description.text = "EDA Mag. Sensor Stream"
        edaMagChart.description.textColor = Color.WHITE

        // enable touch gestures
        edaMagChart.setTouchEnabled(true)

        // enable scaling and dragging
        edaMagChart.isDragEnabled = true
        edaMagChart.setScaleEnabled(true)
        edaMagChart.setDrawGridBackground(false)
        edaMagChart.isAutoScaleMinMaxEnabled = true

        // if disabled, scaling can be done on x- and y-axis separately
        edaMagChart.setPinchZoom(true)

        // set an alternative background color
        //ecgChart.setBackgroundColor(Color.WHITE)

        val data = LineData()
        data.setValueTextColor(Color.WHITE)

        // add empty data
        edaMagChart.data = data

        // get the legend (only possible after setting data)
        val l: Legend = edaMagChart.legend

        // modify the legend ...
        l.form = Legend.LegendForm.LINE
        l.textColor = Color.WHITE
        l.isEnabled = false

        val xl: XAxis = edaMagChart.xAxis
        xl.textColor = Color.WHITE
        xl.setDrawGridLines(false)
        xl.setAvoidFirstLastClipping(true)
        xl.isEnabled = true

        val leftAxis: YAxis = edaMagChart.axisLeft
        leftAxis.textColor = Color.WHITE
        leftAxis.setDrawGridLines(false)

        val rightAxis: YAxis = edaMagChart.axisRight
        rightAxis.isEnabled = false

        edaMagChart.setDrawBorders(true)

        feedMultiple()
    }

    private fun createSet(): LineDataSet? {
        val set = LineDataSet(null, "EDA Mag Data Stream")
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
        val data: LineData = edaMagChart.data

        if (data != null) {
            var set = data.getDataSetByIndex(0)
            if (set == null) {
                set = createSet()
                data.addDataSet(set)
            }

            for (i in EDAdata.payload.streamData) {
                if (i != null) {
                    val mag = sqrt(i.realData.toDouble().pow(2.0) + i.imaginaryData.toDouble().pow(2.0)).toFloat()
                    data.addEntry(Entry(prevX++.toFloat(), mag), 0)
                }
            }
            data.notifyDataChanged()

            // let the chart know it's data has changed
            edaMagChart.notifyDataSetChanged()

            // limit the number of visible entries
            edaMagChart.setVisibleXRangeMaximum(150F)

            // move to the latest entry
            edaMagChart.moveViewToX(data.entryCount.toFloat())
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