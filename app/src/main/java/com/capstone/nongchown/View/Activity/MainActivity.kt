package com.capstone.nongchown.View.Activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.nongchown.R

import com.capstone.nongchown.Utils.moveActivity

class MainActivity : AppCompatActivity() {
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
    }
}