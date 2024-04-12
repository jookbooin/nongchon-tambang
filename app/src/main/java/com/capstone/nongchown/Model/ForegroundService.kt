package com.capstone.nongchown.Model

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.capstone.nongchown.R
import com.capstone.nongchown.View.Activity.AccidentActivity
import com.capstone.nongchown.View.Activity.AccidentTestActivity

class ForegroundService : Service() {

    private var count = 20
    private lateinit var runnable: Runnable
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var nowContext: Context
    private val binder = LocalBinder()
    private var accidentFlag: Boolean = false

    inner class LocalBinder : Binder() {
        fun getService(): ForegroundService = this@ForegroundService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    @SuppressLint("ForegroundServiceType", "ServiceCast")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        nowContext = this
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "count"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1", channelName, importance).apply {
                description = "Description of my channel"
            }

            notificationManager.createNotificationChannel(channel)

        }

        val notiBuilder = NotificationCompat.Builder(this, "1")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("농촌 실행중")
            .setContentText("농촌 서비스가 안전하게 지키고 있습니다.")

        val notiComplete = NotificationCompat.Builder(this, "2")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Complete")
            .setContentText("complete")
        if (Build.VERSION.SDK_INT < 34) {
            startForeground(1, notiBuilder.build())
        } else {
            startForeground(
                1, notiBuilder.build(),
                FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        }

        //여기서 포그라운드에서 할 동작을 구현

        runnable = object : Runnable {
            override fun run() {
                if (accidentFlag && count >= 1) {

                    count--
                    Log.d("test", count.toString())

                    val accident = AccidentActivity.getInstance()
                    val accidentIntent = Intent(nowContext, AccidentActivity::class.java)
                    accidentIntent.putExtra("timer", count)
                    val pendingMain = PendingIntent.getActivity(
                        nowContext,
                        0,
                        accidentIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )

                    val updatedNotification = NotificationCompat.Builder(nowContext, "1")
                        .setContentTitle("전복사고 발생")
                        .setContentText("현재 안전하다면 " + count.toString() + "초 안에 버튼을 눌러주세요")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentIntent(pendingMain)
                        .build()

                    if (ActivityCompat.checkSelfPermission(
                            nowContext,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    NotificationManagerCompat.from(nowContext).notify(1, updatedNotification)


                    // 메인 액티비티가 존재하고, 참조가 유효한지 확인
                    if (accident != null) {
                        // 데이터 전달
                        accident.updateTimerText(count)
                        Log.d("test", "!!")
                    } else {
                        Log.d("test", "null")
                    }
                } else if (count <= 0) {
                    val updatedNotification = NotificationCompat.Builder(nowContext, "1")
                        .setContentTitle("전복사고 발생")
                        .setContentText("정상적으로 신고되었습니다.")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .build()
                    NotificationManagerCompat.from(nowContext).notify(1, updatedNotification)
                } else {
                    val updatedNotification = NotificationCompat.Builder(nowContext, "1")
                        .setContentTitle("농촌 실행중")
                        .setContentText("농촌 서비스가 안전하게 지키고 있습니다.")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .build()
                    NotificationManagerCompat.from(nowContext).notify(1, updatedNotification)

                }


                handler.postDelayed(this, 1000) // 1초 후에 다시 실행
            }
        }
        handler.post(runnable)

        return START_NOT_STICKY
    }

    public fun userSafe() {
        changeAccidentFlag(false)
        var mainIntent = Intent(AccidentActivity.getInstance(), AccidentTestActivity::class.java)
        AccidentActivity.getInstance().startActivity(mainIntent)
    }

    public fun userAccident() {
        changeAccidentFlag(true)
        count = 20
    }

    public fun changeAccidentFlag(flag: Boolean) {
        accidentFlag = flag
    }

    public fun changeTimer(timer: Int) {
        count = timer
    }

    fun getTimerCount() :Int{
        return count
    }
}