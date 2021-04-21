package com.example.vsmwatchandroidapplication


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.analog.study_watch_sdk.core.SDK
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
    fun createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val name = "VSM Warning"
            val descriptionText = "Watch Battery is Low"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description= descriptionText

            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }

    }
    private fun sendNotification()
    {
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
    fun checkBattery(){
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
            if(batteryPct <= 30) {
                batteryImage.setImageResource(R.drawable.low_battery)
                if(notified == false)
                {
                    sendNotification()
                    notified = true
                }
            }
            else
            {
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
            ppgSeries1.appendData(DataPoint(time, rows[i][2].toDouble()),true, rows.size)
            ppgSeries2.appendData(DataPoint(time, rows[i][4].toDouble()),true, rows.size)
        }
        latPPGSeries1 = rows[rows.size-1][2]
        latPPGSeries2 = rows[rows.size-1][4]

        //read ecg
        file = resources.openRawResource(R.raw.ecg)
        rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            ecgSeries.appendData(DataPoint(time, rows[i][2].toDouble()),true, rows.size)
        }
        latEcgSeries = rows[rows.size-1][2]
        //read eda
        file = resources.openRawResource(R.raw.eda)
        rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            edaSeriesMag.appendData(DataPoint(time, rows[i][3].toDouble()),true, rows.size)
            edaSeriesPhase.appendData(DataPoint(time, rows[i][4].toDouble()),true, rows.size)
        }
        latEdaSeries = rows[rows.size-1][3]

        //read acc
        file = resources.openRawResource(R.raw.adxl)
        rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            accSeriesX.appendData(DataPoint(time, rows[i][1].toDouble()),true, rows.size)
            accSeriesY.appendData(DataPoint(time, rows[i][2].toDouble()),true, rows.size)
            accSeriesZ.appendData(DataPoint(time, rows[i][3].toDouble()),true, rows.size)
            accSeriesMag.appendData(DataPoint(time, rows[i][4].toDouble()),true, rows.size)
        }
        latAccSeriesX = rows[rows.size-1][1]
        latAccSeriesY = rows[rows.size-1][2]
        latAccSeriesZ = rows[rows.size-1][3]

        //read temp
        file = resources.openRawResource(R.raw.temp)
        rows = csvReader().readAll(file)
        for (i in rows.indices) {
            val time = rows[i][0].toDouble() / 1000
            tempSeries.appendData(DataPoint(time, rows[i][1].toDouble()),true, rows.size)

        }
        latTempSeries = rows[rows.size-1][1]
    }*/


}