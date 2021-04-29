package com.example.vsmwatchandroidapplication.ui.chart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.analog.study_watch_sdk.core.packets.stream.ADXLDataPacket
import com.analog.study_watch_sdk.core.packets.stream.SYNCPPGDataPacket
import com.example.vsmwatchandroidapplication.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class ADXLFragment : Fragment() {

    private lateinit var chartViewModel: ChartViewModel
    private var thread: Thread = Thread()
    private lateinit var accChart: LineChart
    private var prevX = 0
    private var maxEntry = 300
    private var removalCounter: Long = 0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        chartViewModel = ViewModelProvider(this).get(ChartViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_adxl, container, false)

        accChart = root.findViewById((R.id.accChartInd))

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

        return root
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

    fun addEntry(ACCdata: SYNCPPGDataPacket) {
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
                    if (i.adxlX.toFloat() > 65000) {
                        data.getDataSetByIndex(0).addEntry(Entry((setX.entryCount + removalCounter).toFloat(), 65000 - i.adxlX.toFloat()))
                    }
                    else {
                        data.getDataSetByIndex(0).addEntry(Entry((setX.entryCount + removalCounter).toFloat(), i.adxlX.toFloat()))
                    }
                    if (i.adxlY.toFloat() > 65000) {
                        data.getDataSetByIndex(1).addEntry(Entry((setX.entryCount + removalCounter).toFloat(), 65000 - i.adxlY.toFloat()))
                    }
                    else {
                        data.getDataSetByIndex(1).addEntry(Entry((setX.entryCount + removalCounter).toFloat(), i.adxlY.toFloat()))
                    }
                    if (i.adxlZ.toFloat() > 65000) {
                        data.getDataSetByIndex(2).addEntry(Entry((setX.entryCount + removalCounter).toFloat(), 65000 - i.adxlZ.toFloat()))
                    }
                    else {
                        data.getDataSetByIndex(2).addEntry(Entry((setX.entryCount + removalCounter).toFloat(), i.adxlZ.toFloat()))
                    }
                }
            }

            if (setX.entryCount > maxEntry) {
                data.removeEntry(removalCounter.toFloat(), 0)
                data.removeEntry(removalCounter.toFloat(), 1)
                data.removeEntry(removalCounter.toFloat(), 2)
                removalCounter++
            }

            data.notifyDataChanged()
            accChart.notifyDataSetChanged()
            accChart.setVisibleXRangeMaximum(maxEntry.toFloat() / 2)
            accChart.invalidate()
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