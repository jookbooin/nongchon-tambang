package com.capstone.nongchown.View.Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
    private val PERMISSIONS_REQUEST_SEND_SMS = 1
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

        val btnFirebase: Button = findViewById(R.id.btnFirebase)
        btnFirebase.setOnClickListener {
            Log.v("test", "firebaseStart")
            var firebaseIntent = Intent(this@MainActivity, FireBaseTestActivity::class.java)
            startActivity(firebaseIntent)

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

        val smsBtn : Button = findViewById(R.id.btnSMS)
        smsBtn.setOnClickListener{
            val phoneText: EditText = findViewById(R.id.phone_number)

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {//권한이 없다면
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SEND_SMS),
                    PERMISSIONS_REQUEST_SEND_SMS
                )
            } else { //권한이 있다면 SMS를 보낸다.

                val smsManager = SmsManager.getDefault()
                try {
                    smsManager.sendTextMessage("+82"+phoneText.text.toString(), null, "안녕~~~", null,null)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Toast.makeText(baseContext, ex.message, Toast.LENGTH_SHORT).show()
                }
            }


        }
    }


}

