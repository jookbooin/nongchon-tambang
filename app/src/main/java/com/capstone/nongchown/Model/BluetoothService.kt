package com.capstone.nongchown.Model

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.capstone.nongchown.R
import com.capstone.nongchown.Repository.BluetoothRepository
import com.capstone.nongchown.View.Activity.AccidentActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BluetoothService : Service() {

    @Inject
    lateinit var bluetoothRepository: BluetoothRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    //    private var startMode: Int = 0             // 서비스가 kill 될 때, 어떻게 동작할지를 나타냄
//    private var binder: IBinder? = null        // bind 된 클라이언트와 소통하기 위한 인터페이스
    private var allowRebind: Boolean = false   // onRebind() 메소드가 사용될지 말지를 결정함

    private lateinit var notificationManager: NotificationManager
    private lateinit var nowContext: Context

    val MAIN_NOTIFICATION = "1"
    val MAIN_ID = 1

    override fun onCreate() {
        super.onCreate()
        Log.d("[로그]", "서비스 onCreate()")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nowContext = this
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // startService()에 의해 서비스가 시작될 때
        Log.d("[로그]", "서비스 onStartCommand()")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "count"
            val importance = NotificationManager.IMPORTANCE_NONE
            val channel = NotificationChannel("1", channelName, importance).apply {
                description = "Description of my channel"
            }

            notificationManager.createNotificationChannel(channel)
        }

        val notiServiceStart = NotificationCompat.Builder(this, MAIN_NOTIFICATION)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("농촌 실행중")
            .setContentText("농촌 서비스가 안전하게 지키고 있습니다.")
            .build()

        if (Build.VERSION.SDK_INT < 34) {
            startForeground(MAIN_ID, notiServiceStart)
        } else {
            startForeground(
                MAIN_ID, notiServiceStart,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        }

        serviceScope.launch {
            bluetoothRepository.readDataFromDevice().collect { data ->
//                if (data.isNotEmpty()) {
//                    showScreen(data)
                    val notiAccidentOccur = NotificationCompat.Builder(nowContext, MAIN_NOTIFICATION)
                        .setContentTitle("전복사고 발생")
                        .setContentText("현재 안전하다면 버튼을 눌러주세요")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .build()
                    NotificationManagerCompat.from(nowContext).notify(MAIN_ID, notiAccidentOccur)
//                }
            }
        }

        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent): IBinder? {
        // bindService()에 의해 서비스가 시작될 때
        return null
    }

    override fun onUnbind(intent: Intent): Boolean {
        // 모든 클라이언트가 unbindService()를 호출했을 때
        // 즉, 더이상 바인딩된 클라이언트가 없을때
        return allowRebind
    }

    override fun onRebind(intent: Intent) {
        // onUnbind()가 호출된적이 있는 상태에서, 다시 bindService()를 통해 바인딩 할 때
    }

    override fun onDestroy() {
        // 서비스가 파괴될 때
        super.onDestroy()
        Log.d("[로그]", "서비스 중단")
        serviceScope.cancel()
        notificationManager.cancelAll()
        bluetoothRepository.disconnect()
    }

    fun showScreen(data: String) {
        val intent = Intent(this, AccidentActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            Log.d("[로그]", "data 받을 시 화면 띄우기")
            putExtra("data", data)
        }
        startActivity(intent)
    }
}