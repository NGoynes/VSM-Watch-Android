        package com.example.vsmwatchandroidapplication.ui.chart

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.analog.study_watch_sdk.core.packets.stream.ECGDataPacket
import com.example.vsmwatchandroidapplication.MainActivity
import com.example.vsmwatchandroidapplication.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import org.jetbrains.anko.support.v4.runOnUiThread
import java.util.*


class ChartFragment : Fragment() {

    private lateinit var chartViewModel: ChartViewModel
    private var thread: Thread = Thread()
    private var edaThread: Thread = Thread()
    private var prevX = 0

    //private lateinit var ecgChart: LineChart
    //private var plotData = true
    private var ecgSeries = LineGraphSeries<DataPoint>()
    private lateinit var ecgChart: LineChart

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

        //var ppgSeries1 = LineGraphSeries<DataPoint>()
        //var ppgSeries2 = LineGraphSeries<DataPoint>()
        //var edaSeriesMag = LineGraphSeries<DataPoint>()
        //var edaSeriesPhase = LineGraphSeries<DataPoint>()
        //var accSeriesX = LineGraphSeries<DataPoint>()
        //var accSeriesY = LineGraphSeries<DataPoint>()
        //var accSeriesZ = LineGraphSeries<DataPoint>()
        //var accSeriesMag = LineGraphSeries<DataPoint>()
        //var tempSeries = LineGraphSeries<DataPoint>()

        //val ppgChart: GraphView = root.findViewById((R.id.ppgChart))
        ecgChart = root.findViewById((R.id.ecgChart))
        //val edaPhaseChart: GraphView = root.findViewById((R.id.edaPhaseChart))
        //val edaMagChart: GraphView = root.findViewById((R.id.edaMagChart))
        //val accChart: GraphView = root.findViewById((R.id.accChart))
        //val tempChart: GraphView = root.findViewById((R.id.tempChart))

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

        val rightAxis: YAxis = ecgChart.axisRight
        rightAxis.isEnabled = false

        ecgChart.setDrawBorders(true)

        feedMultiple()

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
        }

        edaMagChart.setOnClickListener{
            val intent: Intent = Intent(context?.applicationContext, EDAMagActivity::class.java)
            startActivity(intent)
        }

        edaPhaseChart.setOnClickListener{
            val intent: Intent = Intent(context?.applicationContext, EDAPhaseActivity::class.java)
            startActivity(intent)
        }*/

        ecgChart.setOnClickListener {
            val intent: Intent =
                Intent(context?.applicationContext, ECGActivity::class.java)
            startActivity(intent)
        }

        /*tempChart.setOnClickListener{
            val intent: Intent = Intent(context?.applicationContext, TempActivity::class.java)
            startActivity(intent)
        }

        accChart.setOnClickListener{
            val intent: Intent = Intent(context?.applicationContext, AccActivity::class.java)
            startActivity(intent)
        }
        */
        return root
    }

    private fun createSet(): LineDataSet? {
        val set = LineDataSet(null, "ECG Data Stream")
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

    private fun addEntry(ECGdata: ECGDataPacket) {
        var data: LineData = ecgChart.data

        if (data != null) {
            var set = data.getDataSetByIndex(0)
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