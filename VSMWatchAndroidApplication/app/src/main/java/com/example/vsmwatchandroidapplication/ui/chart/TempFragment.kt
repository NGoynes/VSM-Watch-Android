package com.example.vsmwatchandroidapplication.ui.chart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.analog.study_watch_sdk.application.TemperatureApplication
import com.analog.study_watch_sdk.core.packets.stream.TemperatureDataPacket
import com.example.vsmwatchandroidapplication.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.common.base.Stopwatch
import kotlinx.android.synthetic.main.fragment_chart.*
import java.util.concurrent.TimeUnit

class TempFragment : Fragment() {

    private lateinit var chartViewModel: ChartViewModel
    private var thread: Thread = Thread()
    lateinit var tempChart: LineChart
    private var Temptitle: TextView? = null
    var prevX = 0
    //private var range = 60

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        chartViewModel = ViewModelProvider(this).get(ChartViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_temp, container, false)

        tempChart = root.findViewById((R.id.tempChartInd))

        Temptitle = root.findViewById((R.id.tempCurrInd))
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
        tempXl.setLabelCount(5, true)
        tempXl.isEnabled = true

        val tempLeftAxis: YAxis = tempChart.axisLeft
        tempLeftAxis.textColor = Color.WHITE
        tempLeftAxis.setDrawGridLines(false)
        tempLeftAxis.setLabelCount(3, true)

        val tempRightAxis: YAxis = tempChart.axisRight
        tempRightAxis.isEnabled = false

        tempChart.setDrawBorders(true)

        return root
    }

    private fun createSet(): LineDataSet? {
        val set = LineDataSet(null, "Temperature Data Stream")
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

    fun addEntry(TempData: TemperatureDataPacket, TempTimer: Stopwatch) {
        var data: LineData = tempChart.data
        if(tempCel){
            Temptitle!!.text = "Temp. (C)"
        }
        else{


            Temptitle!!.text = "Temp. (F)"
        }
        if (data != null) {
            var set = data.getDataSetByIndex(0)
            if (set == null) {
                set = createSet()
                data.addDataSet(set)
            }
            if (TempTimer.elapsed(TimeUnit.MILLISECONDS) > 500) {
                if (TempData.payload != null) {
                    data.addEntry(Entry(prevX++.toFloat(), TempData.payload.temperature1.toFloat() / 10), 0)
                }


                data.notifyDataChanged()

                // let the chart know it's data has changed
                tempChart.notifyDataSetChanged()

                // limit the number of visible entries
                tempChart.setVisibleXRangeMaximum((1 * tempRange).toFloat())

                // move to the latest entry
                tempChart.moveViewToX(data.xMax)
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