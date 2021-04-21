package com.example.vsmwatchandroidapplication.ui.chart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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

class ECGActivity : AppCompatActivity() {

    private var thread: Thread = Thread()
    private var ecgSeries = LineGraphSeries<DataPoint>()
    private lateinit var ecgChart: LineChart
    private var prevX = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ecg)

        ecgChart= findViewById((R.id.ecgChartInd))

        // enable description text
        ecgChart.description.isEnabled = true

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
        ecgChart.setBackgroundColor(Color.WHITE)

        val data = LineData()
        data.setValueTextColor(Color.WHITE)

        // add empty data
        ecgChart.data = data

        // get the legend (only possible after setting data)
        val l: Legend = ecgChart.legend

        // modify the legend ...
        l.form = Legend.LegendForm.LINE
        l.textColor = Color.WHITE

        val xl: XAxis = ecgChart.xAxis
        xl.textColor = Color.WHITE
        xl.setDrawGridLines(true)
        xl.setAvoidFirstLastClipping(true)
        xl.isEnabled = true

        val leftAxis: YAxis = ecgChart.axisLeft
        leftAxis.textColor = Color.WHITE
        leftAxis.setDrawGridLines(false)
        //leftAxis.axisMaximum = 10000f
        //leftAxis.axisMinimum = 0f
        leftAxis.setLabelCount(5, true)
        leftAxis.setDrawGridLines(true)

        val rightAxis: YAxis = ecgChart.axisRight
        rightAxis.isEnabled = false

        ecgChart.axisLeft.setDrawGridLines(true)
        ecgChart.xAxis.setDrawGridLines(true)
        ecgChart.setDrawBorders(true)

        feedMultiple()
    }

    private fun createSet(): LineDataSet? {
        val set = LineDataSet(null, "Dynamic Data")
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.lineWidth = 3f
        set.color = Color.MAGENTA
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
            // set.addEntry(...); // can be called as well

            // set.addEntry(...); // can be called as well
            if (set == null) {
                set = createSet()
                data.addDataSet(set)
            }

            for (i in ECGdata.payload.streamData) {
                if (i != null) {
                    //if (i.timestamp.toFloat() > data.xMax) {
                        //data.addEntry(Entry(i.timestamp.toFloat(), i.ecgData.toFloat()), 0)
                    data.addEntry(Entry(prevX++.toFloat(), i.ecgData.toFloat()), 0)
                    //}
                }
            }
            //data.addEntry(Entry(set.entryCount.toFloat(), (Math.random() * 80).toFloat() + 10f), 0)
            //Collections.sort(data, EntryXComparator())
            data.notifyDataChanged()

            // let the chart know it's data has changed
            ecgChart.notifyDataSetChanged()

            // limit the number of visible entries
            ecgChart.setVisibleXRangeMaximum(150F)
            // mChart.setVisibleYRange(30, AxisDependency.LEFT)

            // move to the latest entry
            ecgChart.moveViewToX(data.getEntryCount().toFloat())

            //println(data.dataSets.toString())
        }
    }

    private fun feedMultiple() {
        if (thread != null) {
            thread.interrupt()
        }
        val ecg = com.example.vsmwatchandroidapplication.watchSdk!!.ecgApplication
        ecg.setCallback { ECGdata ->

            //Log.d("Connection", "DATA :: ${ECGdata.payload.ecgInfo}")
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
        if (thread != null) {
            thread.interrupt()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        thread.interrupt()
        super.onDestroy()
    }
}