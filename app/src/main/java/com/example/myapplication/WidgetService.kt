package com.example.myapplication
            // JUST FOR TESTING PURPOSE, DO NOT USED IN PROJECT!!!!!!!!!!!!!!!!!!!
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import java.util.*

class WidgetService: Service() {

    val TAG = "MyService"

    private lateinit var player: MediaPlayer
    private val binder = LocalBinder()
    private val mGenerator = Random()

    val randomNumber: Int
        get() = mGenerator.nextInt(100)


    override fun onBind(intent: Intent): IBinder {
        Log.e("Service_Debug", "Service onBind")
        return binder
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): WidgetService = this@WidgetService
    }

    override fun onStart(intent: Intent?, startId: Int) {
        Log.e("Service_Debug", "Service onStart")
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        Log.e("Service_Debug", "Service onDestroy")
        //player.stop()
        super.onDestroy()
    }

    override fun onCreate() {
        Log.e("Service_Debug", "Service onCreate")
        super.onCreate()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.e("Service_Debug", "Service onUnbind")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        Log.e("Service_Debug", "Service onRebind")
        super.onRebind(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.e("Service_Debug", "Service onTaskRemoved")
        super.onTaskRemoved(rootIntent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("Service_Debug", "Service onStartCommand")
        player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI)
        player.isLooping = true
        //player.start()
        return START_STICKY
    }

}