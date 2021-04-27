        package com.example.vsmwatchandroidapplication.ui.chart

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.analog.study_watch_sdk.application.ADXLApplication
import com.analog.study_watch_sdk.application.ECGApplication
import com.analog.study_watch_sdk.application.EDAApplication
import com.analog.study_watch_sdk.core.packets.stream.ADXLDataPacket
import com.analog.study_watch_sdk.core.packets.stream.ECGDataPacket
import com.analog.study_watch_sdk.core.packets.stream.EDADataPacket
import com.example.vsmwatchandroidapplication.MainActivity
import com.example.vsmwatchandroidapplication.R
import com.example.vsmwatchandroidapplication.cf
import com.example.vsmwatchandroidapplication.ui.dashboard.accOn
import com.example.vsmwatchandroidapplication.ui.dashboard.ecgOn
import com.example.vsmwatchandroidapplication.ui.dashboard.edaOn
import com.example.vsmwatchandroidapplication.ui.dashboard.ppgOn
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_chart.*
import org.jetbrains.anko.support.v4.runOnUiThread
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt


class ChartFragment : Fragment() {

    private lateinit var chartViewModel: ChartViewModel
    private var thread: Thread = Thread()
    private var prevX = 0

    private lateinit var ecgChart: LineChart
    private lateinit var accChart: LineChart
    private lateinit var ppgChart: LineChart
    private lateinit var edaPhaseChart: LineChart
    private lateinit var edaMagChart: LineChart
    private lateinit var tempChart: LineChart

    private val eda: EDAApplication = com.example.vsmwatchandroidapplication.watchSdk!!.edaApplication
    private val acc: ADXLApplication = com.example.vsmwatchandroidapplication.watchSdk!!.adxlApplication
    private val ecg: ECGApplication = com.example.vsmwatchandroidapplication.watchSdk!!.ecgApplication
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

        val edaPhaseRightAxis: YAxis = edaPhaseChart.axisRight
        edaPhaseRightAxis.isEnabled = false

        edaPhaseChart.setDrawBorders(true)

        //feedMultiple()
        /*//PPG PLOT
        ppgSeries1.color = Color.rgb(255, 51, 0)
        ppgSeries1.isDrawBackground = true
        ppgSeries1.backgroundColor = Color.argb(150, 255, 133, 102)
        ppgSeries1.thickness = 10
        ppgSeries1.title = "S1"
        ppgSeries2.color = Color.rgb(51, 153, 255)
        ppgSeries2.isDrawBackground = true
        ppgSeries2.backgroundColor = Color.argb(150, 128, 191, 255)
        ppgSeries2.thickness = 10
        ppgSeries2.title = "S2"
        ppgChart.title = "PPG"
        ppgChart.gridLabelRenderer.horizontalAxisTitle = "Time (s)"
        ppgChart.gridLabelRenderer.verticalAxisTitle = "PPG"
        ppgChart.gridLabelRenderer.numVerticalLabels = 3
        ppgChart.legendRenderer.isVisible = true
        ppgChart.viewport.isYAxisBoundsManual = true
        ppgChart.viewport.isXAxisBoundsManual = true
        ppgChart.viewport.setMinY(min(ppgSeries1.lowestValueY, ppgSeries2.lowestValueY))
        ppgChart.viewport.setMaxY(max(ppgSeries1.highestValueY, ppgSeries2.highestValueY))
        ppgChart.viewport.setMinX(min(ppgSeries1.lowestValueX, ppgSeries2.lowestValueX))
        ppgChart.viewport.setMaxX(max(ppgSeries1.highestValueX, ppgSeries2.highestValueX))
        ppgChart.viewport.isScrollable = true
        ppgChart.addSeries(ppgSeries2)
        ppgChart.addSeries(ppgSeries1)*/

        /*//ECG PLOT
        ecgSeries.color = Color.rgb(255, 51, 0)
        ecgSeries.isDrawBackground = true
        ecgSeries.backgroundColor = Color.argb(200, 233, 179, 179)
        ecgSeries.thickness = 10
        ecgChart.title = "ECG"
        ecgChart.gridLabelRenderer.horizontalAxisTitle = "Time (s)"
        ecgChart.gridLabelRenderer.verticalAxisTitle = "ECG"
        ecgChart.gridLabelRenderer.numVerticalLabels = 3
        ecgChart.addSeries(ecgSeries)
        ecgChart.viewport.isYAxisBoundsManual = true
        ecgChart.viewport.isXAxisBoundsManual = true
        ecgChart.viewport.setMinY(0.0)
        ecgChart.viewport.setMaxY(100000.0)
        ecgChart.viewport.setMinX(0.0)
        ecgChart.viewport.setMaxX(60.0)
        ecgChart.viewport.isScrollable = true*/

        /*//EDA MAG PLOT
        edaSeriesMag.color = Color.rgb(255, 51, 0)
        edaSeriesMag.isDrawBackground = true
        edaSeriesMag.backgroundColor = Color.argb(200, 233, 179, 179)
        edaSeriesMag.thickness = 10
        edaMagChart.title = "EDA MAGNITUDE"
        edaMagChart.gridLabelRenderer.horizontalAxisTitle = "Time (s)"
        edaMagChart.gridLabelRenderer.verticalAxisTitle = "Imp. Mag. (Ohms)"
        edaMagChart.gridLabelRenderer.numVerticalLabels = 3
        edaMagChart.viewport.isYAxisBoundsManual = true
        edaMagChart.viewport.isXAxisBoundsManual = true
        edaMagChart.viewport.setMinY(edaSeriesMag.lowestValueY)
        edaMagChart.viewport.setMaxY(edaSeriesMag.highestValueY)
        edaMagChart.viewport.setMinX(edaSeriesMag.lowestValueX)
        edaMagChart.viewport.setMaxX(edaSeriesMag.highestValueX)
        edaMagChart.viewport.isScrollable = true
        edaMagChart.addSeries(edaSeriesMag)

        //EDA PHASE PLOT
        edaSeriesPhase.color = Color.rgb(51, 153, 255)
        edaSeriesPhase.isDrawBackground = true
        edaSeriesPhase.backgroundColor = Color.argb(150, 128, 191, 255)
        edaSeriesPhase.thickness = 10
        edaPhaseChart.title = "EDA PHASE"
        edaPhaseChart.gridLabelRenderer.horizontalAxisTitle = "Time (s)"
        edaPhaseChart.gridLabelRenderer.verticalAxisTitle = "Imp. Phase (Rad)"
        edaPhaseChart.gridLabelRenderer.numVerticalLabels = 3
        edaPhaseChart.viewport.isYAxisBoundsManual = true
        edaPhaseChart.viewport.isXAxisBoundsManual = true
        edaPhaseChart.viewport.setMinY(edaSeriesPhase.lowestValueY)
        edaPhaseChart.viewport.setMaxY(edaSeriesPhase.highestValueY)
        edaPhaseChart.viewport.setMinX(edaSeriesPhase.lowestValueX)
        edaPhaseChart.viewport.setMaxX(edaSeriesPhase.highestValueX)
        edaPhaseChart.viewport.isScrollable = true
        edaPhaseChart.addSeries(edaSeriesPhase)

        //ACC PLOT
        accSeriesX.color = Color.rgb(255, 51, 0)
        accSeriesX.thickness = 5
        accSeriesX.title = "X"
        accSeriesY.color = Color.rgb(51, 153, 255)
        accSeriesY.thickness = 5
        accSeriesY.title = "Y"
        accSeriesZ.color = Color.rgb(0, 204, 0)
        accSeriesZ.thickness = 5
        accSeriesZ.title = "Z"
        accChart.title = "ACCELEROMETER"
        accChart.gridLabelRenderer.horizontalAxisTitle = "Time (s)"
        accChart.gridLabelRenderer.verticalAxisTitle = "Accelerometer"
        accChart.gridLabelRenderer.numVerticalLabels = 3
        accChart.legendRenderer.isVisible = true
        accChart.viewport.isYAxisBoundsManual = true
        accChart.viewport.isXAxisBoundsManual = true
        accChart.viewport.setMinY(min(min(accSeriesX.lowestValueY, accSeriesY.lowestValueY), accSeriesZ.lowestValueY))
        accChart.viewport.setMaxY(max(max(accSeriesX.highestValueY, accSeriesY.highestValueY), accSeriesZ.highestValueY))
        accChart.viewport.setMinX(min(min(accSeriesX.lowestValueX, accSeriesY.lowestValueX), accSeriesZ.lowestValueX))
        accChart.viewport.setMaxX(max(max(accSeriesX.highestValueX, accSeriesY.highestValueX), accSeriesY.highestValueX))
        accChart.viewport.isScrollable = true
        accChart.addSeries(accSeriesX)
        accChart.addSeries(accSeriesY)
        accChart.addSeries(accSeriesZ)

        //TEMP PLOT
        tempSeries.color = Color.rgb(51, 153, 255)
        tempSeries.isDrawBackground = true
        tempSeries.backgroundColor = Color.argb(150, 128, 191, 255)
        tempSeries.thickness = 10
        tempChart.title = "TEMPERATURE"
        tempChart.gridLabelRenderer.horizontalAxisTitle = "Time (s)"
        tempChart.gridLabelRenderer.verticalAxisTitle = "Temperature (C)"
        tempChart.gridLabelRenderer.numVerticalLabels = 3
        tempChart.viewport.isYAxisBoundsManual = true
        tempChart.viewport.isXAxisBoundsManual = true
        tempChart.viewport.setMinY(tempSeries.lowestValueY)
        tempChart.viewport.setMaxY(tempSeries.highestValueY)
        tempChart.viewport.setMinX(tempSeries.lowestValueX)
        tempChart.viewport.setMaxX(tempSeries.highestValueX)
        tempChart.viewport.isScrollable = true
        tempChart.addSeries(tempSeries)


        //create click listeners
        ppgChart.setOnClickListener{
            val intent: Intent = Intent(context?.applicationContext, PPGActivity::class.java)
            startActivity(intent)
        }*/

        edaMagChart.setOnClickListener{
            if(edaOn == true) {
                val intent: Intent = Intent(context?.applicationContext, EDAMagActivity::class.java)
                startActivity(intent)
            }
        }

        edaPhaseChart.setOnClickListener{
            if(edaOn == true) {
                val intent: Intent = Intent(context?.applicationContext, EDAPhaseActivity::class.java)
                startActivity(intent)
            }
        }

        ecgChart.setOnClickListener {
            if(ecgOn == true) {
                val intent: Intent =
                        Intent(context?.applicationContext, ECGActivity::class.java)
                startActivity(intent)
            }
        }

        /*tempChart.setOnClickListener{
            val intent: Intent = Intent(context?.applicationContext, TempActivity::class.java)
            startActivity(intent)
        }*/

        accChart.setOnClickListener{
            if(accOn == true) {
                val intent: Intent = Intent(context?.applicationContext, AccActivity::class.java)
                startActivity(intent)
            }
        }
        return root
    }

    private fun createSet(): LineDataSet? {
        val set = LineDataSet(null, null)
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.lineWidth = 3f
        set.color = Color.rgb(255, 51, 0)
        set.fillColor = Color.rgb(233, 179, 179)
        set.fillAlpha = 250
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

     fun addEntry(ECGdata: ECGDataPacket) {
        var data: LineData = ecgChart.data

        if (data != null) {
            var set = data.getDataSetByIndex(0)
            if (set == null) {
                set = createSet()
                set.setDrawFilled(false)
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
        }
    }

    fun addEntryMag(EDAdata: EDADataPacket) {
        val data: LineData = edaMagChart.data

        if (data != null) {
            var set = data.getDataSetByIndex(0)
            if (set == null) {
                set = createSet()
                data.addDataSet(set)
            }

            for (i in EDAdata.payload.streamData) {
                if (i != null) {
                    val mag = sqrt(i.realData.toDouble().pow(2.0) + i.imaginaryData.toDouble().pow(2.0)).toFloat()
                    data.addEntry(Entry(prevX++.toFloat(), mag), 0)
                }
            }
            data.notifyDataChanged()

            // let the chart know it's data has changed
            edaMagChart.notifyDataSetChanged()

            // limit the number of visible entries
            edaMagChart.setVisibleXRangeMaximum(150F)

            // move to the latest entry
            edaMagChart.moveViewToX(data.entryCount.toFloat())
        }
    }

    fun addEntryPhase(EDAdata: EDADataPacket) {
        val data: LineData = edaPhaseChart.data

        if (data != null) {
            var set = data.getDataSetByIndex(0)
            if (set == null) {
                set = createSet()
                data.addDataSet(set)
            }

            for (i in EDAdata.payload.streamData) {
                if (i != null) {
                    if (i.realData != 0) {
                        val phase = atan((i.imaginaryData / i.realData).toDouble()).toFloat()
                        data.addEntry(Entry(prevX++.toFloat(), phase), 0)
                    }
                }
            }
            data.notifyDataChanged()

            // let the chart know it's data has changed
            edaPhaseChart.notifyDataSetChanged()

            // limit the number of visible entries
            edaPhaseChart.setVisibleXRangeMaximum(150F)

            // move to the latest entry
            edaPhaseChart.moveViewToX(data.entryCount.toFloat())
        }
    }

    private fun feedMultiple() {
        if (thread != null) {
            thread.interrupt()
        }
        if(ecgOn == true) {
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

            acc.setCallback { ACCdata ->
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

            /*
        eda.setCallback { EDAdata ->
            runOnUiThread {
                addEntryMag(EDAdata)
                addEntryPhase(EDAdata)
            }
            try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }*/

            //eda.startSensor()
            //eda.subscribeStream()

            acc.startSensor()
            acc.subscribeStream()

        }
    }

    override fun onPause() {
        super.onPause()
        if (thread != null) {
            thread.interrupt()
        }
        //eda.stopAndUnsubscribeStream()
        acc.stopAndUnsubscribeStream()
        ecg.stopAndUnsubscribeStream()
    }

    override fun onResume() {
        //feedMultiple()
        super.onResume()
    }

    override fun onDestroy() {
        thread.interrupt()
        //eda.stopAndUnsubscribeStream()
        acc.stopAndUnsubscribeStream()
        ecg.stopAndUnsubscribeStream()
        super.onDestroy()
    }
}