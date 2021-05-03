package com.example.vsmwatchandroidapplication.ui.chart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.google.common.base.Stopwatch
import java.util.concurrent.TimeUnit
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt

class EDAPhaseFragment : Fragment() {

    private lateinit var chartViewModel: ChartViewModel
    private lateinit var edaPhaseChart: LineChart
    private var thread: Thread = Thread()
    private var prevX = 0
    private var range = 60

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        chartViewModel = ViewModelProvider(this).get(ChartViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_eda_phase, container, false)

        edaPhaseChart = root.findViewById((R.id.edaPhaseChartInd))

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
        leftAxis.setLabelCount(3, true)

        val rightAxis: YAxis = edaPhaseChart.axisRight
        rightAxis.isEnabled = false

        edaPhaseChart.setDrawBorders(true)

        return root
    }

    private fun createSet(): LineDataSet? {
        val set = LineDataSet(null, "EDA Phase Data Stream")
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.lineWidth = 3f
        set.color = Color.rgb(255, 51, 0)
        set.fillColor = Color.rgb(255, 51, 0)
        set.fillAlpha = 80
        set.setDrawFilled(true)
        set.isHighlightEnabled = false
        set.setDrawValues(false)
        set.setDrawCircles(false)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.cubicIntensity = 0.2f
        return set
    }

    fun addEntryPhase(EDAdata: EDADataPacket, EDATimer: Stopwatch) {
        val data: LineData = edaPhaseChart.data

        if (data != null) {
            var set = data.getDataSetByIndex(0)
            if (set == null) {
                set = createSet()
                data.addDataSet(set)
            }
            if (EDATimer.elapsed(TimeUnit.MILLISECONDS) > 500) {
                for (i in EDAdata.payload.streamData) {
                    if (i != null) {
                        val mag = sqrt(i.realData.toDouble().pow(2.0) + i.imaginaryData.toDouble().pow(2.0)).toFloat()
                        data.addEntry(Entry(prevX++.toFloat(), mag), 0)
                    }
                }
                data.notifyDataChanged()

                // let the chart know it's data has changed
                edaPhaseChart.notifyDataSetChanged()

                var sampleRate: Long = 1
                if (EDATimer.elapsed(TimeUnit.SECONDS).toInt() != 0) {
                    sampleRate = prevX / EDATimer.elapsed(TimeUnit.SECONDS)
                }

                // limit the number of visible entries
                edaPhaseChart.setVisibleXRangeMaximum((sampleRate * range).toFloat())

                // move to the latest entry
                edaPhaseChart.moveViewToX(data.xMax)
            }
        }
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
}