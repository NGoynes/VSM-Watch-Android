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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.analog.study_watch_sdk.application.*
import com.analog.study_watch_sdk.core.SDK
import com.example.vsmwatchandroidapplication.ui.chart.*
import com.example.vsmwatchandroidapplication.ui.dashboard.DashboardFragment
import com.example.vsmwatchandroidapplication.ui.dashboard.ScanFragment
import com.example.vsmwatchandroidapplication.ui.logging.LoggingFragment
import com.example.vsmwatchandroidapplication.ui.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*


var watchSdk // sdk reference variable
: SDK? = null
var df : Fragment? = null
var cf : Fragment? = null
var lf : Fragment? = null
var sf : Fragment? = null
var ppgF : Fragment? = null
var adxlF : Fragment? = null
var ecgF : Fragment? = null
var edaMagF : Fragment? = null
var edaPhaseF : Fragment? = null
var tempF : Fragment? = null
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
        ppgF = PPGFragment()
        adxlF = ADXLFragment()
        ecgF = ECGFragment()
        edaMagF = EDAMagFragment()
        edaPhaseF = EDAPhaseFragment()
        tempF = TempFragment()

        fragman = supportFragmentManager
        fragman!!.beginTransaction()
                .add(R.id.nav_host_fragment, df as DashboardFragment)
                .add(R.id.nav_host_fragment, cf as ChartFragment)
                .add(R.id.nav_host_fragment, lf as LoggingFragment)
                .add(R.id.nav_host_fragment, sf as SettingsFragment)
                .add(R.id.nav_host_fragment, ppgF as PPGFragment)
                .add(R.id.nav_host_fragment, adxlF as ADXLFragment)
                .add(R.id.nav_host_fragment, edaMagF as EDAMagFragment)
                .add(R.id.nav_host_fragment, edaPhaseF as EDAPhaseFragment)
                .add(R.id.nav_host_fragment, tempF as TempFragment)
                .add(R.id.nav_host_fragment, ecgF as ECGFragment)
                .hide(ecgF as ECGFragment)
                .hide(adxlF as ADXLFragment)
                .hide(edaMagF as EDAMagFragment)
                .hide(edaPhaseF as EDAPhaseFragment)
                .hide(tempF as TempFragment)
                .hide(ppgF as PPGFragment)
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

    override fun onBackPressed(){
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        nav_view.isVisible = true
        my_toolbar.isVisible = true
        fragman!!
                .beginTransaction()
                .hide(ppgF as PPGFragment)
                .hide(ecgF as ECGFragment)
                .hide(adxlF as ADXLFragment)
                .hide(edaMagF as EDAMagFragment)
                .hide(edaPhaseF as EDAPhaseFragment)
                .hide(tempF as TempFragment)
                .show(cf as ChartFragment)
                .commit()
    }
}
