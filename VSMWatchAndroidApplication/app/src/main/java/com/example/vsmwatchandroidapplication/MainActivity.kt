package com.example.vsmwatchandroidapplication


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.analog.study_watch_sdk.core.SDK
import com.analog.study_watch_sdk.core.packets.stream.PPGDataPacket
import com.analog.study_watch_sdk.interfaces.PPGCallback
import com.example.vsmwatchandroidapplication.ui.dashboard.ScanFragment
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.io.InputStream

var watchSdk // sdk reference variable
: SDK? = null
class MainActivity : AppCompatActivity() {
    var watchSdk // sdk reference variable
            : SDK? = null
    /*var ppgSeries1 = LineGraphSeries<DataPoint>()
    var latPPGSeries1 = String()
    var ppgSeries2 = LineGraphSeries<DataPoint>()
    var latPPGSeries2 = String()
    var ecgSeries = LineGraphSeries<DataPoint>()
    var latEcgSeries = String()
    var edaSeriesMag = LineGraphSeries<DataPoint>()
    var latEdaSeries = String()
    var edaSeriesPhase = LineGraphSeries<DataPoint>()
    var accSeriesX = LineGraphSeries<DataPoint>()
    var latAccSeriesX = String()
    var accSeriesY = LineGraphSeries<DataPoint>()
    var latAccSeriesY = String()
    var accSeriesZ = LineGraphSeries<DataPoint>()
    var latAccSeriesZ = String()
    var accSeriesMag = LineGraphSeries<DataPoint>()
    var tempSeries = LineGraphSeries<DataPoint>()
    var latTempSeries = String()*/
    private val CHANNEL_ID = "channel_id_01"
    private val notificationID = 101
    var notified = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        val bar = setSupportActionBar(findViewById(R.id.my_toolbar))

        navView.setupWithNavController(navController)
        createNotificationChannel()
        //readHealthData()
        checkBattery()
    }

    /*    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            true
        }
        R.id.action_favorite -> {
            true
        }
        else -> {

            super.onOptionsItemSelected(item)
        }

    }*/
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "VSM Warning"
            val descriptionText = "Watch Battery is Low"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = descriptionText

            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }

    }

    private fun sendNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setChannelId(CHANNEL_ID)
                .setContentTitle("VSM Warning")
                .setContentText("Watch Battery is Low")
                .setStyle(NotificationCompat.BigPictureStyle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationID, builder.build())
        }

    }

    fun checkBattery() {
        val batterytxt: TextView = findViewById(R.id.battery_data)
        val batteryImage: ImageView = findViewById(R.id.battery_image)
//        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
//            registerReceiver(null, ifilter)
//        }
//        val batteryPct: Float? = batteryStatus?.let { intent ->
//            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
//            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
//            level * 100 / scale.toFloat()
//        }
        var batteryPct = ScanFragment().readBatter()
        batterytxt.setText(batteryPct.toString() + "%")
        if (batteryPct != null) {
            if (batteryPct <= 30) {
                batteryImage.setImageResource(R.drawable.low_battery)
                if (notified == false) {
                    sendNotification()
                    notified = true
                }
            } else {
                batteryImage.setImageResource(R.drawable.low_battery)
                notified = false
            }
        }
    }
    /*private fun readHealthData() {
        //read ppg
        var file: InputStream = resources.openRawResource(R.raw.adpd)
        var rows: List<List<String>> = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            ppgSeries1.appendData(DataPoint(time, rows[i][2].toDouble()), true, rows.size)
            ppgSeries2.appendData(DataPoint(time, rows[i][4].toDouble()), true, rows.size)
        }
        latPPGSeries1 = rows[rows.size - 1][2]
        latPPGSeries2 = rows[rows.size - 1][4]

        //read ecg
        file = resources.openRawResource(R.raw.ecg)
        rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            ecgSeries.appendData(DataPoint(time, rows[i][2].toDouble()), true, rows.size)
        }
        latEcgSeries = rows[rows.size - 1][2]
        //read eda
        file = resources.openRawResource(R.raw.eda)
        rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            edaSeriesMag.appendData(DataPoint(time, rows[i][3].toDouble()), true, rows.size)
            edaSeriesPhase.appendData(DataPoint(time, rows[i][4].toDouble()), true, rows.size)
        }
        latEdaSeries = rows[rows.size - 1][3]

        //read acc
        file = resources.openRawResource(R.raw.adxl)
        rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            accSeriesX.appendData(DataPoint(time, rows[i][1].toDouble()), true, rows.size)
            accSeriesY.appendData(DataPoint(time, rows[i][2].toDouble()), true, rows.size)
            accSeriesZ.appendData(DataPoint(time, rows[i][3].toDouble()), true, rows.size)
            accSeriesMag.appendData(DataPoint(time, rows[i][4].toDouble()), true, rows.size)
        }
        latAccSeriesX = rows[rows.size - 1][1]
        latAccSeriesY = rows[rows.size - 1][2]
        latAccSeriesZ = rows[rows.size - 1][3]

        //read temp
        file = resources.openRawResource(R.raw.temp)
        rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            tempSeries.appendData(DataPoint(time, rows[i][1].toDouble()), true, rows.size)

        }
        latTempSeries = rows[rows.size - 1][1]
    }

    //    fun readECG() {
//        if(com.example.vsmwatchandroidapplication.watchSdk != null) {
//            val ecg = com.example.vsmwatchandroidapplication.watchSdk!!.ecgApplication
//            var test = ecg.startSensor()
////            var sub = ecg.subscribeStream()
//            var see = ecg.readDeviceConfigurationBlock().payload.data
//            Log.d("Connection","pizza")
//            Log.d("Connection", see.toString())
//            Log.d("Connection","pizza")
//            var x = ECGDataPacket()
//            x = ecg
////            (Log.d("Connection", sub.toString))
////            ecg.stopSensor()
    fun readECG() {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {
//        val ECGtxt: TextView = findViewById(R.id.dbecg_data)
            val eda = com.example.vsmwatchandroidapplication.watchSdk!!.ecgApplication
            eda.setCallback { ECGdata ->
                Log.d("Connection", "DATA :: ${ECGdata.payload.ecgInfo}")
            }
            eda.writeLibraryConfiguration(arrayOf(longArrayOf(0x0, 0x4)))

            eda.startSensor()
            eda.subscribeStream()

        }

    }
    fun readTemp() {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {
            val temps = com.example.vsmwatchandroidapplication.watchSdk!!.temperatureApplication
            temps.setCallback { TempuratureDataPacket ->
                Log.d("Connection", "DATA :: ${TempuratureDataPacket.payload.temperature1}")
            }
            temps.startSensor()
            temps.subscribeStream()

        }

    }
    fun readEDA() {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {
            val eda = com.example.vsmwatchandroidapplication.watchSdk!!.edaApplication
            eda.setCallback { EDADataPacket ->
                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(0).imaginaryData}")
                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(0).realData}")
                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(1).imaginaryData}")
                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(1).realData}")
                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(2).imaginaryData}")
                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(2).realData}")
                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(3).imaginaryData}")
                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(3).realData}")
                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(4).imaginaryData}")
                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(4).realData}")
                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(5).imaginaryData}")
                Log.d("Connection", "DATA :: ${EDADataPacket.payload.streamData.get(5).realData}")
            }
            eda.startSensor()
            eda.subscribeStream()

        }
        latTempSeries = rows[rows.size-1][1]
    }*/

            ppg.startSensor()
            ppg.subscribeStream()

        }

    }
    fun stopPPG()
    {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {
            val ppg = com.example.vsmwatchandroidapplication.watchSdk!!.ppgApplication
            ppg.stopSensor()
            ppg.stopAndUnsubscribeStream()
        }
    }
    fun stopECG()
    {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {
            val ecg = com.example.vsmwatchandroidapplication.watchSdk!!.ecgApplication
            ecg.stopSensor()
            ecg.stopAndUnsubscribeStream()
        }
    }
    fun stopEDA()
    {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {
            val eda = com.example.vsmwatchandroidapplication.watchSdk!!.edaApplication
            eda.stopSensor()
            eda.stopAndUnsubscribeStream()
        }
    }
    fun stopTemp()
    {
        if (com.example.vsmwatchandroidapplication.watchSdk != null) {
            val temp = com.example.vsmwatchandroidapplication.watchSdk!!.temperatureApplication
            temp.stopSensor()
            temp.stopAndUnsubscribeStream()
        }
    }
}
