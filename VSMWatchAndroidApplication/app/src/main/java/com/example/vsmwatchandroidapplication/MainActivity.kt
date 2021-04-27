package com.example.vsmwatchandroidapplication


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.analog.study_watch_sdk.core.SDK
import com.example.vsmwatchandroidapplication.ui.chart.ChartFragment
import com.example.vsmwatchandroidapplication.ui.chart.PPGActivity
import com.example.vsmwatchandroidapplication.ui.dashboard.DashboardFragment
import com.example.vsmwatchandroidapplication.ui.dashboard.ScanFragment
import com.example.vsmwatchandroidapplication.ui.logging.LoggingFragment
import com.example.vsmwatchandroidapplication.ui.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


var watchSdk // sdk reference variable
: SDK? = null
var df : Fragment? = null
var cf : Fragment? = null
var lf : Fragment? = null
var sf : Fragment? = null
var ppgF : Fragment? = null
var fragman : FragmentManager? = null
class MainActivity : AppCompatActivity() {
    private val CHANNEL_ID = "channel_id_01"
    private val notificationID = 101
    var notified = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        //val navController = findNavController(R.id.nav_host_fragment)

        //val bar = setSupportActionBar(findViewById(R.id.my_toolbar))

        navView.setOnNavigationItemSelectedListener(navListener)
        df = DashboardFragment()
        cf = ChartFragment()
        lf = LoggingFragment()
        sf = SettingsFragment()
        ppgF = PPGActivity()
        fragman = supportFragmentManager
        fragman!!.beginTransaction()
                .add(R.id.nav_host_fragment, df as DashboardFragment)
                .add(R.id.nav_host_fragment, cf as ChartFragment)
                .add(R.id.nav_host_fragment, lf as LoggingFragment)
                .add(R.id.nav_host_fragment, sf as SettingsFragment)
                .add(R.id.nav_host_fragment, ppgF as PPGActivity)
                .hide(ppgF as PPGActivity)
                .hide(cf as ChartFragment)
                .hide(lf as LoggingFragment)
                .hide(sf as SettingsFragment)
                .commit()
        createNotificationChannel()
        //readHealthData()
        checkBattery()
    }

    private val navListener: BottomNavigationView.OnNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
             //By using switch we can easily get
            // the selected fragment
            // by using there id.
            var selectedFragment: Fragment? = null
            when (item.getItemId()) {
                R.id.navigation_dashboard -> selectedFragment = DashboardFragment()
                R.id.navigation_chart -> selectedFragment = ChartFragment()
                R.id.navigation_logging -> selectedFragment = LoggingFragment()
                R.id.navigation_settings -> selectedFragment = SettingsFragment()
            }
            // It will help to replace the
            // one fragment to other.
            if (selectedFragment != null) {
                if(item.itemId == R.id.navigation_chart){
                    fragman!!
                            .beginTransaction()
                            .hide(cf as ChartFragment)
                            .hide(lf as LoggingFragment)
                            .hide(sf as SettingsFragment)
                            .hide(df as DashboardFragment)
                            .show(cf as ChartFragment)
                            .commit()
                }
                if(item.itemId == R.id.navigation_dashboard){
                    fragman!!
                            .beginTransaction()
                            .hide(cf as ChartFragment)
                            .hide(lf as LoggingFragment)
                            .hide(sf as SettingsFragment)
                            .hide(df as DashboardFragment)
                            .show(df as DashboardFragment)
                            .commit()
                }
                if(item.itemId == R.id.navigation_logging){
                    fragman!!
                            .beginTransaction()
                            .hide(cf as ChartFragment)
                            .hide(lf as LoggingFragment)
                            .hide(sf as SettingsFragment)
                            .hide(df as DashboardFragment)
                            .show(lf as LoggingFragment)
                            .commit()
                }
                if(item.itemId == R.id.navigation_settings){
                    fragman!!
                            .beginTransaction()
                            .hide(cf as ChartFragment)
                            .hide(lf as LoggingFragment)
                            .hide(sf as SettingsFragment)
                            .hide(df as DashboardFragment)
                            .show(sf as SettingsFragment)
                            .commit()
                }
            }
            return true
        }

//        fun onNavigationItemSelected(item: MenuItem): Boolean {
//            // By using switch we can easily get
//            // the selected fragment
//            // by using there id.
//            var selectedFragment: Fragment? = null
//            when (item.getItemId()) {
//                R.id.algorithm -> selectedFragment = AlgorithmFragment()
//                R.id.course -> selectedFragment = CourseFragment()
//                R.id.profile -> selectedFragment = ProfileFragment()
//            }
//            // It will help to replace the
//            // one fragment to other.
//            supportFragmentManager
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, selectedFragment)
//                    .commit()
//            return true
//        }
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
//
//            ppg.startSensor()
//            ppg.subscribeStream()
//
//        }
//
//    }
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
