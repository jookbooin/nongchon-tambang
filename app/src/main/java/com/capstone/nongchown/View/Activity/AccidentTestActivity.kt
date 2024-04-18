package com.capstone.nongchown.View.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.capstone.nongchown.Model.ForegroundService
import com.capstone.nongchown.R

class AccidentTestActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private var instance: AccidentTestActivity? = null

        fun getInstance(): AccidentTestActivity {
            return instance!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)

        // 최초 한번만 포그라운드 서비스 실행
        sharedPreferences = getSharedPreferences("isFirst", Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (isFirstRun) {

            val serviceIntent = Intent(this@AccidentTestActivity, ForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
            with(sharedPreferences.edit()) {
                putBoolean("isFirstRun", false)
                apply()
            }
        }

        findViewById<Button>(R.id.button).setOnClickListener {

        }
    }

}