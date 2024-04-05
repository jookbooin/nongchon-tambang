package com.capstone.nongchown

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

class LogoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.title)
        Log.d("test", "title")

        val prefs = getSharedPreferences("isFirst", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        android.os.Handler().postDelayed({
            Log.d("test", "title2")
            // 다른 액티비티를 실행하는 Intent 생성
            val intent = Intent(this, AccidentTestActivity::class.java)
            startActivity(intent)

            // 현재 액티비티를 종료
            finish()
        }, 2000) // 5초(5000밀리초) 지연

    }
}