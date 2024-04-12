package com.capstone.nongchown.View.Activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.capstone.nongchown.Model.Service.ForegroundService
import com.capstone.nongchown.R

class AccidentActivity : ComponentActivity() {

    companion object {
        private var instance: AccidentActivity? = null

        fun getInstance(): AccidentActivity {
            return instance!!
        }
    }

    private var foregroundService: ForegroundService? = null
    var countData = 0

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as ForegroundService.LocalBinder
            foregroundService = binder.getService()
            if (countData == 0) {
                foregroundService?.userAccident()
            } else {
                foregroundService?.changeTimer(countData)
            }

        }

        override fun onServiceDisconnected(className: ComponentName) {

        }
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        Intent(this, ForegroundService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        Log.d("test", "accident")
        setContentView(R.layout.accident_notification)
        val intent = intent
        countData = intent?.getIntExtra("timer", 0)!!
        if (countData != 0) {
            if (countData != null) {
                updateTimerText(countData)


            }
        }


        findViewById<Button>(R.id.ok_btn).setOnClickListener {
            Log.d("test", "btnON")
            foregroundService?.userSafe()

        }
    }

    public fun updateTimerText(count: Int) {
        findViewById<TextView>(R.id.timer).text = count.toString()
        if (count >= 10) {


        }
    }
}