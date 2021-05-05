package com.example.vsmwatchandroidapplication.ui.chart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.analog.study_watch_sdk.core.enums.PPGLcfgID
import com.analog.study_watch_sdk.core.packets.stream.SYNCPPGDataPacket
import com.example.vsmwatchandroidapplication.R
import com.example.vsmwatchandroidapplication.df
import com.example.vsmwatchandroidapplication.ui.dashboard.DashboardFragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.jetbrains.anko.support.v4.runOnUiThread

class PPGFragment : Fragment() {

    private lateinit var chartViewModel: ChartViewModel
    private var thread: Thread = Thread()
    private lateinit var ppgChart: LineChart
    private var maxEntry = 300
    private var removalCounter: Long = 0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        chartViewModel =
                ViewModelProvider(this).get(ChartViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_ppg, container, false)



        ppgChart = root.findViewById((R.id.ppgChartInd))

        // enable description text
        ppgChart.description.isEnabled = true
        ppgChart.description.text = "PPG Sensor Stream"
        ppgChart.description.textColor = Color.WHITE

        // enable touch gestures
        ppgChart.setTouchEnabled(true)

        // enable scaling and dragging
        ppgChart.isDragEnabled = true
        ppgChart.setScaleEnabled(true)
        ppgChart.setDrawGridBackground(false)
        ppgChart.isAutoScaleMinMaxEnabled = true

        // if disabled, scaling can be done on x- and y-axis separately
        ppgChart.setPinchZoom(true)

        // set an alternative background color
        //ecgChart.setBackgroundColor(Color.WHITE)

        val ppgData = LineData()
        ppgData.setValueTextColor(Color.WHITE)

        // add empty data
        ppgChart.data = ppgData

        // get the legend (only possible after setting data)
        val ppgL: Legend = ppgChart.legend

        // modify the legend ...
        ppgL.form = Legend.LegendForm.LINE
        ppgL.textColor = Color.WHITE
        ppgL.isEnabled = false

        val ppgXl: XAxis = ppgChart.xAxis
        ppgXl.textColor = Color.WHITE
        ppgXl.setDrawGridLines(false)
        ppgXl.setAvoidFirstLastClipping(true)
        ppgXl.isEnabled = true

        val ppgLeftAxis: YAxis = ppgChart.axisLeft
        ppgLeftAxis.textColor = Color.WHITE
        ppgLeftAxis.setDrawGridLines(false)
        ppgLeftAxis.setLabelCount(3, true)

        val ppgRightAxis: YAxis = ppgChart.axisRight
        ppgRightAxis.isEnabled = false

        ppgChart.setDrawBorders(true)

        return root
    }

    private fun createSet(): LineDataSet? {
        val set = LineDataSet(null, "PPG Data Stream")
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

    fun addEntry(PPGdata: SYNCPPGDataPacket) {
        var data: LineData = ppgChart.data

        if (data != null) {
            var set = data.getDataSetByIndex(0)
            if (set == null) {
                set = createSet()
                data.addDataSet(set)
            }

            for (i in PPGdata.payload.streamData) {
                if (i != null) {
                    data.addEntry(Entry((set.entryCount + removalCounter).toFloat(), i.ppgData.toFloat()), 0)
                    val xval = set.entryCount + removalCounter
                    println("X:$xval")
                    println("Y:" + i.ppgData)
                    println("\n")
                }
            }

            if (set.entryCount >= maxEntry) {
                data.removeEntry(removalCounter.toFloat(), 0)
                set.removeFirst()
                removalCounter++
            }

            data.notifyDataChanged()
            ppgChart.notifyDataSetChanged()
            ppgChart.setVisibleXRangeMaximum(maxEntry.toFloat() / 2)
            ppgChart.moveViewToX((set.entryCount + removalCounter).toFloat())
            ppgChart.invalidate()
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