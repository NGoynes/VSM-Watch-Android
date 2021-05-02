package com.example.vsmwatchandroidapplication.ui.chart

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.analog.study_watch_sdk.application.*
import com.analog.study_watch_sdk.core.packets.stream.*
import com.example.vsmwatchandroidapplication.*
import com.example.vsmwatchandroidapplication.ui.dashboard.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.common.base.Stopwatch
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt


class ChartFragment : Fragment() {

    private lateinit var chartViewModel: ChartViewModel
    private var thread: Thread = Thread()
    private var maxPPGEntry = 1000
    private var maxEntry = 300
    private var prevX = 0
    private var prevEDAPhaseX = 0
    private var prevEDAMagX = 0
    private var prevADXLX = 0

    private lateinit var ecgChart: LineChart
    private lateinit var accChart: LineChart
    private lateinit var ppgChart: LineChart
    private lateinit var edaPhaseChart: LineChart
    private lateinit var edaMagChart: LineChart
    private lateinit var tempChart: LineChart

    private var adxlRemovalCounter: Long = 0
    private var ecgRemovalCounter: Long = 0
    private var edaMagRemovalCounter: Long = 0
    private var edaPhaseRemovalCounter: Long = 0
    private var ppgRemovalCounter: Long = 0
    private var tempRemovalCounter: Long = 0

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        chartViewModel =
            ViewModelProvider(this).get(ChartViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_chart, container, false)

        (activity as MainActivity)?.supportActionBar?.title = "Chart"
        (activity as MainActivity).checkBattery()
        ppgChart = root.findViewById((R.id.ppgChart))
        ecgChart = root.findViewById((R.id.ecgChart))
        edaPhaseChart = root.findViewById((R.id.edaPhaseChart))
        edaMagChart = root.findViewById((R.id.edaMagChart))
        accChart = root.findViewById((R.id.accChart))
        tempChart = root.findViewById((R.id.tempChart))

        //ECG GRAPHING
        // enable description text
        ecgChart.description.isEnabled = false
        ecgChart.description.text = "ECG Sensor Stream"

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

        val ecgChartData = LineData()
        ecgChartData.setValueTextColor(Color.WHITE)

        // add empty data
        ecgChart.data = ecgChartData

        // get the legend (only possible after setting data)
        val ecgL: Legend = ecgChart.legend

        // modify the legend ...
        ecgL.form = Legend.LegendForm.LINE
        ecgL.textColor = Color.WHITE
        ecgL.isEnabled = false

        val ecgXl: XAxis = ecgChart.xAxis
        ecgXl.textColor = Color.WHITE
        ecgXl.setDrawGridLines(false)
        ecgXl.setAvoidFirstLastClipping(true)
        ecgXl.isEnabled = true

        val ecgLeftAxis: YAxis = ecgChart.axisLeft
        ecgLeftAxis.textColor = Color.WHITE
        ecgLeftAxis.setDrawGridLines(false)
        ecgLeftAxis.setLabelCount(3, true)

        val ecgRightAxis: YAxis = ecgChart.axisRight
        ecgRightAxis.isEnabled = false

        ecgChart.setDrawBorders(true)


        //ADXL GRAPHING
        // enable description text
        accChart.description.isEnabled = false
        accChart.description.text = "ADXL Sensor Stream"

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

        val accData = LineData()
        accData.setValueTextColor(Color.WHITE)

        // add empty data
        accChart.data = accData

        // get the legend (only possible after setting data)
        val accL: Legend = accChart.legend

        // modify the legend ...
        accL.form = Legend.LegendForm.LINE
        accL.textColor = Color.WHITE
        accL.isEnabled = false

        val accXl: XAxis = accChart.xAxis
        accXl.textColor = Color.WHITE
        accXl.setDrawGridLines(false)
        accXl.setAvoidFirstLastClipping(true)
        accXl.isEnabled = true

        val accLeftAxis: YAxis = accChart.axisLeft
        accLeftAxis.textColor = Color.WHITE
        accLeftAxis.setDrawGridLines(false)
        accLeftAxis.setLabelCount(3, true)

        val accRightAxis: YAxis = accChart.axisRight
        accRightAxis.isEnabled = false

        accChart.setDrawBorders(true)

        //EDA MAG GRAPHING

        // enable description text
        edaMagChart.description.isEnabled = false
        edaMagChart.description.text = "EDA Mag Sensor Stream"
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

        val edaMagData = LineData()
        edaMagData.setValueTextColor(Color.WHITE)

        // add empty data
        edaMagChart.data = edaMagData

        // get the legend (only possible after setting data)
        val edaMagL: Legend = edaMagChart.legend

        // modify the legend ...
        edaMagL.form = Legend.LegendForm.LINE
        edaMagL.textColor = Color.WHITE
        edaMagL.isEnabled = false

        val edaMagXl: XAxis = edaMagChart.xAxis
        edaMagXl.textColor = Color.WHITE
        edaMagXl.setDrawGridLines(false)
        edaMagXl.setAvoidFirstLastClipping(true)
        edaMagXl.isEnabled = true

        val edaMagLeftAxis: YAxis = edaMagChart.axisLeft
        edaMagLeftAxis.textColor = Color.WHITE
        edaMagLeftAxis.setDrawGridLines(false)
        edaMagLeftAxis.setLabelCount(3, true)

        val edaMagRightAxis: YAxis = edaMagChart.axisRight
        edaMagRightAxis.isEnabled = false

        edaMagChart.setDrawBorders(true)


        //EDA PHASE GRAPHING

        // enable description text
        edaPhaseChart.description.isEnabled = false
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

        val edaPhaseData = LineData()
        edaPhaseData.setValueTextColor(Color.WHITE)

        // add empty data
        edaPhaseChart.data = edaPhaseData

        // get the legend (only possible after setting data)
        val edaPhaseL: Legend = edaPhaseChart.legend

        // modify the legend ...
        edaPhaseL.form = Legend.LegendForm.LINE
        edaPhaseL.textColor = Color.WHITE
        edaPhaseL.isEnabled = false

        val edaPhaseXl: XAxis = edaPhaseChart.xAxis
        edaPhaseXl.textColor = Color.WHITE
        edaPhaseXl.setDrawGridLines(false)
        edaPhaseXl.setAvoidFirstLastClipping(true)
        edaPhaseXl.isEnabled = true

        val edaPhaseLeftAxis: YAxis = edaPhaseChart.axisLeft
        edaPhaseLeftAxis.textColor = Color.WHITE
        edaPhaseLeftAxis.setDrawGridLines(false)
        edaPhaseLeftAxis.setLabelCount(3, true)

        val edaPhaseRightAxis: YAxis = edaPhaseChart.axisRight
        edaPhaseRightAxis.isEnabled = false

        edaPhaseChart.setDrawBorders(true)


        //PPG GRAPHING

        // enable description text
        ppgChart.description.isEnabled = false
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


        //TEMP GRAPHING

        // enable description text
        tempChart.description.isEnabled = false
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
        tempLeftAxis.setLabelCount(3, true)

        val tempRightAxis: YAxis = tempChart.axisRight
        tempRightAxis.isEnabled = false

        tempChart.setDrawBorders(true)


        //create click listeners
        ppgChart.setOnClickListener{
            if (ppgOn) {
                (activity as MainActivity).nav_view.isVisible = false
                (activity as MainActivity).my_toolbar.isVisible = false
                (activity as MainActivity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                fragman!!
                        .beginTransaction()
                        .show(ppgF as PPGFragment)
                        .hide(cf as ChartFragment)
                        .commit()
            }
        }

        edaMagChart.setOnClickListener{
            if(edaOn) {
                (activity as MainActivity).nav_view.isVisible = false
                (activity as MainActivity).my_toolbar.isVisible = false
                (activity as MainActivity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                fragman!!
                        .beginTransaction()
                        .show(edaMagF as EDAMagFragment)
                        .hide(cf as ChartFragment)
                        .commit()
            }
        }

        edaPhaseChart.setOnClickListener{
            if(edaOn) {
                (activity as MainActivity).nav_view.isVisible = false
                (activity as MainActivity).my_toolbar.isVisible = false
                (activity as MainActivity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                fragman!!
                        .beginTransaction()
                        .show(edaPhaseF as EDAPhaseFragment)
                        .hide(cf as ChartFragment)
                        .commit()
            }
        }

        ecgChart.setOnClickListener {
            if(ecgOn) {
                (activity as MainActivity).nav_view.isVisible = false
                (activity as MainActivity).my_toolbar.isVisible = false
                (activity as MainActivity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                fragman!!
                        .beginTransaction()
                        .show(ecgF as ECGFragment)
                        .hide(cf as ChartFragment)
                        .commit()
            }
        }

        tempChart.setOnClickListener{
            if (tempOn) {
                (activity as MainActivity).nav_view.isVisible = false
                (activity as MainActivity).my_toolbar.isVisible = false
                (activity as MainActivity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                fragman!!
                        .beginTransaction()
                        .show(tempF as TempFragment)
                        .hide(cf as ChartFragment)
                        .commit()
            }
        }

        accChart.setOnClickListener{
            if(ppgOn) {
                (activity as MainActivity).nav_view.isVisible = false
                (activity as MainActivity).my_toolbar.isVisible = false
                (activity as MainActivity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                fragman!!
                        .beginTransaction()
                        .show(adxlF as ADXLFragment)
                        .hide(cf as ChartFragment)
                        .commit()
            }
        }
        return root
    }

    private fun createSet(): LineDataSet? {
        val set = LineDataSet(null, null)
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

    fun addEntry(ECGdata: ECGDataPacket, ECGTimer: Stopwatch) {
        var data: LineData = ecgChart.data

        if (data != null) {
            var set = data.getDataSetByIndex(0)
            if (set == null) {
                set = createSet()
                set.setDrawFilled(false)
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

                // limit the number of visible entries
                ecgChart.setVisibleXRangeMaximum(2700f)

                // move to the latest entry
                ecgChart.moveViewToX(data.xMax)
            }
        }
    }

    fun addEntryADXL(ACCdata: SYNCPPGDataPacket, ADXLTimer: Stopwatch) {
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

            if (ADXLTimer.elapsed(TimeUnit.MILLISECONDS) > 500) {
                for (i in ACCdata.payload.streamData) {
                    if (i != null) {
                        if (i.adxlX.toFloat() > 65000) {
                            data.getDataSetByIndex(0).addEntry(Entry(prevADXLX++.toFloat(), 65000 - i.adxlX.toFloat()))
                        }
                        else {
                            data.getDataSetByIndex(0).addEntry(Entry(prevADXLX++.toFloat(), i.adxlX.toFloat()))
                        }
                        if (i.adxlY.toFloat() > 65000) {
                            data.getDataSetByIndex(1).addEntry(Entry(prevADXLX++.toFloat(), 65000 - i.adxlY.toFloat()))
                        }
                        else {
                            data.getDataSetByIndex(1).addEntry(Entry(prevADXLX++.toFloat(), i.adxlY.toFloat()))
                        }
                        if (i.adxlZ.toFloat() > 65000) {
                            data.getDataSetByIndex(2).addEntry(Entry(prevADXLX++.toFloat(), 65000 - i.adxlZ.toFloat()))
                        }
                        else {
                            data.getDataSetByIndex(2).addEntry(Entry(prevADXLX++.toFloat(), i.adxlZ.toFloat()))
                        }
                    }
                }
                data.notifyDataChanged()

                // let the chart know it's data has changed
                accChart.notifyDataSetChanged()

                // limit the number of visible entries
                accChart.setVisibleXRangeMaximum(4500f)

                // move to the latest entry
                accChart.moveViewToX(data.xMax)
            }
        }
    }

    fun addEntryMag(EDAdata: EDADataPacket, EDATimer: Stopwatch) {
        val data: LineData = edaMagChart.data

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
                        data.addEntry(Entry(prevEDAMagX++.toFloat(), mag), 0)
                    }
                }
                data.notifyDataChanged()

                // let the chart know it's data has changed
                edaMagChart.notifyDataSetChanged()

                // limit the number of visible entries
                edaMagChart.setVisibleXRangeMaximum(540f)

                // move to the latest entry
                edaMagChart.moveViewToX(data.xMax)
            }
        }
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
                        data.addEntry(Entry(prevEDAPhaseX++.toFloat(), mag), 0)
                    }
                }
                data.notifyDataChanged()

                // let the chart know it's data has changed
                edaPhaseChart.notifyDataSetChanged()

                // limit the number of visible entries
                edaPhaseChart.setVisibleXRangeMaximum(540f)

                // move to the latest entry
                edaPhaseChart.moveViewToX(data.xMax)
            }
        }
    }

    fun addEntry(PPGdata: SYNCPPGDataPacket, PPGTimer: Stopwatch) {
        var data: LineData = ppgChart.data

        if (data != null) {
            var set = data.getDataSetByIndex(0)
            if (set == null) {
                set = createSet()
                data.addDataSet(set)
            }

            if (PPGTimer.elapsed(TimeUnit.MILLISECONDS) > 500) {
                for (i in PPGdata.payload.streamData) {
                    if (i != null) {
                        data.addEntry(Entry((prevX++).toFloat(), i.ppgData.toFloat()), 0)
                    }
                }
                data.notifyDataChanged()

                // let the chart know it's data has changed
                ppgChart.notifyDataSetChanged()

                // limit the number of visible entries
                ppgChart.setVisibleXRangeMaximum(3000f)

                // move to the latest entry
                ppgChart.moveViewToX(data.xMax)
            }
        }
    }

    fun addEntry(TempData: TemperatureDataPacket, TempTimer: Stopwatch) {
        var data: LineData = tempChart.data

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
                tempChart.setVisibleXRangeMaximum(60f)

                // move to the latest entry
                tempChart.moveViewToX(data.xMax)
            }
        }
    }

    override fun onDestroy() {
        thread.interrupt()
        super.onDestroy()
    }
}