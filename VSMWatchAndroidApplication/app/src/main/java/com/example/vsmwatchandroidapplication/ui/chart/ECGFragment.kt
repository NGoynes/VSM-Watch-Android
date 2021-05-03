package com.example.vsmwatchandroidapplication.ui.chart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.analog.study_watch_sdk.application.ECGApplication
import com.analog.study_watch_sdk.core.packets.stream.ECGDataPacket
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
import com.google.common.base.Stopwatch
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.math.sqrt

class ECGFragment : Fragment() {

    private lateinit var chartViewModel: ChartViewModel
    private var thread: Thread = Thread()
    private lateinit var ecgChart: LineChart
    private var prevX = 0
    private var range = 30

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        chartViewModel = ViewModelProvider(this).get(ChartViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_ecg, container, false)

        ecgChart= root.findViewById((R.id.ecgChartInd))

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
        leftAxis.setLabelCount(3, true)

        val rightAxis: YAxis = ecgChart.axisRight
        rightAxis.isEnabled = false

        ecgChart.setDrawBorders(true)

        return root
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

    fun addEntry(ECGdata: ECGDataPacket, ECGTimer: Stopwatch) {
        var data: LineData = ecgChart.data

        if (data != null) {
            var set = data.getDataSetByIndex(0)
            if (set == null) {
                set = createSet()
                data.addDataSet(set)
            }
            if (ECGTimer.elapsed(TimeUnit.MILLISECONDS) > 500) {
                for (i in ECGdata.payload.streamData) {
                    if (i != null) {
                        data.addEntry(Entry(prevX++.toFloat(), i.ecgData.toFloat()), 0)
                    }
                }
                data.notifyDataChanged()

                // let the chart know it's data has changed
                ecgChart.notifyDataSetChanged()

                var sampleRate: Long = 1
                if (ECGTimer.elapsed(TimeUnit.SECONDS).toInt() != 0) {
                    sampleRate = prevX / ECGTimer.elapsed(TimeUnit.SECONDS)
                }

                // limit the number of visible entries
                ecgChart.setVisibleXRangeMaximum((sampleRate * range).toFloat())

                // move to the latest entry
                ecgChart.moveViewToX(data.xMax)
            }
        }
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
}