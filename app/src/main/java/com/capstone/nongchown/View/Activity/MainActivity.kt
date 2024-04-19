package com.capstone.nongchown.View.Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.nongchown.Model.ForegroundService
import com.capstone.nongchown.R

import com.capstone.nongchown.Utils.moveActivity

class MainActivity() : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnUserProfile: Button = findViewById(R.id.btnuser_profile)
        btnUserProfile.setOnClickListener {
            Log.v("setOnClick-MainActivity", "btn")
            moveActivity(UserProfileActivity::class.java)
        }
        val btnBluetooth: Button = findViewById(R.id.btnbluetooth)
        btnBluetooth.setOnClickListener {
            Log.v("setOnClick-MainActivity", "btn")
            moveActivity(BluetoothActivity::class.java)

        }



       sharedPreferences = getSharedPreferences("isFirst", Context.MODE_PRIVATE)
       val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        val btnAccident: Button = findViewById(R.id.btnAccident)
        btnAccident.setOnClickListener{

            if (isFirstRun) {

                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // 권한이 부족한 경우 권한 요청
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        100
                    )
                }
                val serviceIntent = Intent(this@MainActivity, ForegroundService::class.java)
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


            var accidentIntent = Intent(this@MainActivity, AccidentActivity::class.java)
            startActivity(accidentIntent)

        }
    }
}