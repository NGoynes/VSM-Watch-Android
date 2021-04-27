package com.example.vsmwatchandroidapplication.ui.chart

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.analog.study_watch_sdk.application.ADXLApplication
import com.analog.study_watch_sdk.core.packets.stream.ADXLDataPacket
import com.analog.study_watch_sdk.core.packets.stream.ECGDataPacket
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
import java.io.InputStream
import java.lang.Double

class AccActivity : AppCompatActivity() {

    private var thread: Thread = Thread()
    private lateinit var accChart: LineChart
    private var prevX = 0
    private val acc: ADXLApplication = com.example.vsmwatchandroidapplication.watchSdk!!.adxlApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acc)

        accChart = findViewById((R.id.accChartInd))

        // enable description text
        accChart.description.isEnabled = true
        accChart.description.text = "ADXL Sensor Stream"
        accChart.description.textColor =  Color.WHITE

        // enable touch gestures
        accChart.setTouchEnabled(true)

        // enable scaling and dragging
        accChart.isDragEnabled = true
        accChart.setScaleEnabled(true)
        accChart.setDrawGridBackground(false)
        accChart.isAutoScaleMinMaxEnabled = true

        // if disabled, scaling can be done on x- and y-axis separately
        accChart.setPinchZoom(true)

        // set an alternative background color
        //ecgChart.setBackgroundColor(Color.WHITE)

        val data = LineData()
        data.setValueTextColor(Color.WHITE)

        // add empty data
        accChart.data = data

        // get the legend (only possible after setting data)
        val l: Legend = accChart.legend

        // modify the legend ...
        l.form = Legend.LegendForm.LINE
        l.textColor = Color.WHITE
        l.isEnabled = true

        val xl: XAxis = accChart.xAxis
        xl.textColor = Color.WHITE
        xl.setDrawGridLines(false)
        xl.setAvoidFirstLastClipping(true)
        xl.isEnabled = true

        val leftAxis: YAxis = accChart.axisLeft
        leftAxis.textColor = Color.WHITE
        leftAxis.setDrawGridLines(false)

        val rightAxis: YAxis = accChart.axisRight
        rightAxis.isEnabled = false

        accChart.setDrawBorders(true)
    }

    private fun createSetX(): LineDataSet? {
        val set = LineDataSet(null, "X")
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

    private fun createSetY(): LineDataSet? {
        val set = LineDataSet(null, "Y")
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.lineWidth = 3f
        set.color = Color.rgb(51, 153, 255)
        set.isHighlightEnabled = false
        set.setDrawValues(false)
        set.setDrawCircles(false)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.cubicIntensity = 0.2f
        return set
    }

    private fun createSetZ(): LineDataSet? {
        val set = LineDataSet(null, "Z")
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.lineWidth = 3f
        set.color = Color.rgb(0, 204, 0)
        set.isHighlightEnabled = false
        set.setDrawValues(false)
        set.setDrawCircles(false)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.cubicIntensity = 0.2f
        return set
    }

    private fun addEntry(ACCdata: ADXLDataPacket) {
        var data: LineData = accChart.data

        if (data != null) {
            var setX = data.getDataSetByIndex(0)
            var setY = data.getDataSetByIndex(1)
            var setZ = data.getDataSetByIndex(2)

            if (setX == null || setY == null || setZ == null) {
                setX = createSetX()
                setY = createSetY()
                setZ = createSetZ()
                data.addDataSet(setX)
                data.addDataSet(setY)
                data.addDataSet(setZ)
            }

            for (i in ACCdata.payload.streamData) {
                if (i != null) {
                    if (i.x.toFloat() > 65000) {
                        data.getDataSetByIndex(0).addEntry(Entry(prevX++.toFloat(), 65000 - i.x.toFloat()))
                    }
                    else {
                        data.getDataSetByIndex(0).addEntry(Entry(prevX++.toFloat(), i.x.toFloat()))
                    }
                    if (i.y.toFloat() > 65000) {
                        data.getDataSetByIndex(1).addEntry(Entry(prevX++.toFloat(), 65000 - i.y.toFloat()))
                    }
                    else {
                        data.getDataSetByIndex(1).addEntry(Entry(prevX++.toFloat(), i.y.toFloat()))
                    }
                    if (i.z.toFloat() > 65000) {
                        data.getDataSetByIndex(2).addEntry(Entry(prevX++.toFloat(), 65000 - i.z.toFloat()))
                    }
                    else {
                        data.getDataSetByIndex(2).addEntry(Entry(prevX++.toFloat(), i.z.toFloat()))
                    }
                }
            }

            data.notifyDataChanged()

            // let the chart know it's data has changed
            accChart.notifyDataSetChanged()

            // limit the number of visible entries
            accChart.setVisibleXRangeMaximum(150F)

            // move to the latest entry
            accChart.moveViewToX(data.entryCount.toFloat())
            accChart.moveViewTo(data.entryCount.toFloat(), data.yMax, YAxis.AxisDependency.LEFT)
        }
    }

    private fun feedMultiple() {
        if (thread != null) {
            thread.interrupt()
        }

        acc.setCallback { ACCdata ->

            //Log.d("Connection", "DATA :: ${ECGdata.payload.ecgInfo}")
            runOnUiThread {
                addEntry(ACCdata)
            }
            try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }

        acc.startSensor()
        acc.subscribeStream()
    }

    override fun onPause() {
        super.onPause()
        if (thread != null) {
            thread.interrupt()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        acc.stopAndUnsubscribeStream()
        fragman!!
                .beginTransaction()
                .show(cf as ChartFragment)
                .commit()
    }
}