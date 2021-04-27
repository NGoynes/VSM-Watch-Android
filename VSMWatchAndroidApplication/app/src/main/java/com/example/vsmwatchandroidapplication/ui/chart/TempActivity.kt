package com.example.vsmwatchandroidapplication.ui.chart

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.analog.study_watch_sdk.application.TemperatureApplication
import com.analog.study_watch_sdk.core.packets.stream.TemperatureDataPacket
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

class TempActivity : AppCompatActivity() {

    private var thread: Thread = Thread()
    private lateinit var tempChart: LineChart
    private var prevX = 0
    private val temp: TemperatureApplication = com.example.vsmwatchandroidapplication.watchSdk!!.temperatureApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp)

        tempChart = findViewById((R.id.tempChartInd))

        // enable description text
        tempChart.description.isEnabled = true
        tempChart.description.text = "Temperature Sensor Stream"
        tempChart.description.textColor = Color.WHITE

        // enable touch gestures
        tempChart.setTouchEnabled(true)

        // enable scaling and dragging
        tempChart.isDragEnabled = true
        tempChart.setScaleEnabled(true)
        tempChart.setDrawGridBackground(false)
        tempChart.isAutoScaleMinMaxEnabled = true

        // if disabled, scaling can be done on x- and y-axis separately
        tempChart.setPinchZoom(true)

        // set an alternative background color
        //ecgChart.setBackgroundColor(Color.WHITE)

        val tempData = LineData()
        tempData.setValueTextColor(Color.WHITE)

        // add empty data
        tempChart.data = tempData

        // get the legend (only possible after setting data)
        val tempL: Legend = tempChart.legend

        // modify the legend ...
        tempL.form = Legend.LegendForm.LINE
        tempL.textColor = Color.WHITE
        tempL.isEnabled = false

        val tempXl: XAxis = tempChart.xAxis
        tempXl.textColor = Color.WHITE
        tempXl.setDrawGridLines(false)
        tempXl.setAvoidFirstLastClipping(true)
        tempXl.isEnabled = true

        val tempLeftAxis: YAxis = tempChart.axisLeft
        tempLeftAxis.textColor = Color.WHITE
        tempLeftAxis.setDrawGridLines(false)

        val tempRightAxis: YAxis = tempChart.axisRight
        tempRightAxis.isEnabled = false

        tempChart.setDrawBorders(true)

        feedMultiple()
    }

    private fun createSet(): LineDataSet? {
        val set = LineDataSet(null, "Temperature Data Stream")
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

    private fun addEntry(TempData: TemperatureDataPacket) {
        var data: LineData = tempChart.data

        if (data != null) {
            var set = data.getDataSetByIndex(0)
            if (set == null) {
                set = createSet()
                data.addDataSet(set)
            }

            if (TempData.payload != null) {
                data.addEntry(Entry(prevX++.toFloat(), TempData.payload.temperature1.toFloat()/10), 0)
            }

            data.notifyDataChanged()

            // let the chart know it's data has changed
            tempChart.notifyDataSetChanged()

            // limit the number of visible entries
            tempChart.setVisibleXRangeMaximum(150F)

            // move to the latest entry
            tempChart.moveViewToX(data.entryCount.toFloat())
        }
    }

    private fun feedMultiple() {
        if (thread != null) {
            thread.interrupt()
        }

        temp.setCallback { TempData ->
            runOnUiThread {
                addEntry(TempData)
            }
            try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }

        temp.startSensor()
        temp.subscribeStream()
    }

    override fun onPause() {
        super.onPause()
        //ecg.stopAndUnsubscribeStream()
        if (thread != null) {
            thread.interrupt()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        //thread.interrupt()
        temp.stopAndUnsubscribeStream()

        //super.onDestroy()
        fragman!!
                .beginTransaction()
                .show(cf as ChartFragment)
                .commit()
    }
}