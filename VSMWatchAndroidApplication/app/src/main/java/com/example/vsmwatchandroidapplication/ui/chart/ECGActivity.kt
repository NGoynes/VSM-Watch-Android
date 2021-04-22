package com.example.vsmwatchandroidapplication.ui.chart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.analog.study_watch_sdk.application.ECGApplication
import com.analog.study_watch_sdk.core.packets.stream.ECGDataPacket
import com.example.vsmwatchandroidapplication.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.sql.Time

class ECGActivity : AppCompatActivity() {

    private var thread: Thread = Thread()
    private lateinit var ecgChart: LineChart
    private var prevX = 0
    private val ecg: ECGApplication = com.example.vsmwatchandroidapplication.watchSdk!!.ecgApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ecg)

        ecgChart= findViewById((R.id.ecgChartInd))

        // enable description text
        ecgChart.description.isEnabled = true
        ecgChart.description.text = "ECG Sensor Stream"
        ecgChart.description.textColor = Color.WHITE

        // enable touch gestures
        ecgChart.setTouchEnabled(true)

        // enable scaling and dragging
        ecgChart.isDragEnabled = true
        ecgChart.setScaleEnabled(true)
        ecgChart.setDrawGridBackground(false)
        ecgChart.isAutoScaleMinMaxEnabled = true

        // if disabled, scaling can be done on x- and y-axis separately
        ecgChart.setPinchZoom(true)

        // set an alternative background color
        //ecgChart.setBackgroundColor(Color.WHITE)

        val data = LineData()
        data.setValueTextColor(Color.WHITE)

        // add empty data
        ecgChart.data = data

        // get the legend (only possible after setting data)
        val l: Legend = ecgChart.legend

        // modify the legend ...
        l.form = Legend.LegendForm.LINE
        l.textColor = Color.WHITE
        l.isEnabled = false

        val xl: XAxis = ecgChart.xAxis
        xl.textColor = Color.WHITE
        xl.setDrawGridLines(false)
        xl.setAvoidFirstLastClipping(true)
        xl.isEnabled = true

        val leftAxis: YAxis = ecgChart.axisLeft
        leftAxis.textColor = Color.WHITE
        leftAxis.setDrawGridLines(false)

        val rightAxis: YAxis = ecgChart.axisRight
        rightAxis.isEnabled = false

        ecgChart.setDrawBorders(true)

        feedMultiple()
    }

    private fun createSet(): LineDataSet? {
        val set = LineDataSet(null, "ECG Data Stream")
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.lineWidth = 3f
        set.color = Color.rgb(255, 51, 0)
        set.isHighlightEnabled = false
        set.setDrawValues(false)
        set.setDrawCircles(false)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.cubicIntensity = 0.2f
        return set
    }

    private fun addEntry(ECGdata: ECGDataPacket) {
        var data: LineData = ecgChart.data

        if (data != null) {
            var set = data.getDataSetByIndex(0)
            if (set == null) {
                set = createSet()
                data.addDataSet(set)
            }

            for (i in ECGdata.payload.streamData) {
                if (i != null) {
                    data.addEntry(Entry(prevX++.toFloat(), i.ecgData.toFloat()), 0)
                }
            }
            data.notifyDataChanged()

            // let the chart know it's data has changed
            ecgChart.notifyDataSetChanged()

            // limit the number of visible entries
            ecgChart.setVisibleXRangeMaximum(150F)

            // move to the latest entry
            ecgChart.moveViewToX(data.entryCount.toFloat())
        }
    }

    private fun feedMultiple() {
        if (thread != null) {
            thread.interrupt()
        }

        ecg.setCallback { ECGdata ->
            runOnUiThread {
                addEntry(ECGdata)
            }
            try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }

        ecg.startSensor()
        ecg.subscribeStream()
    }

    override fun onPause() {
        super.onPause()
        ecg.stopAndUnsubscribeStream()
        if (thread != null) {
            thread.interrupt()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        thread.interrupt()
        ecg.stopAndUnsubscribeStream()
        super.onDestroy()
    }
}