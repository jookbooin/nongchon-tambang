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
import android.location.Address
import android.location.Location
import android.media.RingtoneManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.capstone.nongchown.R
import com.capstone.nongchown.Repository.BluetoothRepository
import com.capstone.nongchown.Utils.AddressConverter
import com.capstone.nongchown.View.Activity.AccidentActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundService : Service() {

    @Inject
    lateinit var bluetoothRepository: BluetoothRepository
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    companion object {
        @Volatile
        private var runningServiceState = false     // 메모리로 접근

        fun isServiceRunning() = runningServiceState

        fun setServiceState(flag: Boolean) {
            synchronized(this) {
                runningServiceState = flag
            }
        }
    }

    private lateinit var notificationManager: NotificationManager
    public var count = MutableLiveData<Int>(30)
    private lateinit var nowContext: Context
    private val binder = LocalBinder()
    private var accidentFlag: Boolean = false
    private lateinit var addressConverter: AddressConverter
    val locationChannel = Channel<Location>(Channel.CONFLATED)
    val addressChannel = Channel<Address>(Channel.CONFLATED)

    inner class LocalBinder : Binder() {
        fun getService(): ForegroundService {
            return this@ForegroundService
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("[로그]", "서비스 onCreate()")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nowContext = this
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
            // return
        }

    }

    @SuppressLint("ForegroundServiceType", "ServiceCast")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setServiceState(true)
        Log.d("[로그]", "서비스 시작")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "noti"
            val channelName = "General Notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }

// 알림 사운드 URI 설정
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

// 일반 알림 빌더 설정
        val generalNotification = NotificationCompat.Builder(this, "noti")
            .setContentTitle("농촌 실행중")
            .setContentText("농촌 서비스가 안전하게 지키고 있습니다.")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setSound(alarmSound) // 알림에 사운드 추가
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        if (Build.VERSION.SDK_INT < 34) {
            startForeground(2, generalNotification.build())
        } else {
            startForeground(
                2, generalNotification.build(),
                FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        }

        serviceScope.launch {

            addressConverter = AddressConverter(nowContext, object : AddressConverter.GeocoderListener {
                override fun sendAddress(address: Address) {
                    Log.d("[로그]", "Address 최신화")
                    serviceScope.launch { // 데이터가 전달될 확률 (사고날 확률) 적으니
                        addressChannel.send(address)
                    }
                }
            }
            )

            bluetoothRepository.readDataFromDevice().collect { location ->
                accidentFlag = true
                //showScreen(count.value ?: 0)
                bluetoothRepository.sendDataToDevice()
                addressConverter.getAddressFromLocation(location) // 성공시 oncreate의 fun sendAddress(address: Address) 로 이동해서 receiveAddress 변경 (비동기..)
                locationChannel.send(location)
            }
        }

        serviceScope.launch {
            while (true) {

                if (accidentFlag && (count.value ?: 0) >= 1) {

                    count.postValue((count.value ?: 0) - 1)

                    val accidentIntent = Intent(nowContext, AccidentActivity::class.java)
                    accidentIntent.putExtra("timer", (count.value ?: 0))
                    val pendingMain = PendingIntent.getActivity(
                        nowContext,
                        0,
                        accidentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val generalNotification2 =
                        NotificationCompat.Builder(nowContext, "noti")
                            .setContentTitle("전복사고 발생")
                            .setContentText(
                                "현재 안전하다면 " + (count.value ?: 0).toString() + "초 안에 버튼을 눌러주세요"
                            )
                            .setContentIntent(pendingMain)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setSound(alarmSound) // 알림에 사운드 추가
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .build()

                    with(NotificationManagerCompat.from(nowContext)) {
                        if (ActivityCompat.checkSelfPermission(
                                nowContext,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            notify(2, generalNotification2) // 포그라운드 알림과 다른 ID 사용
                        }

                    }


                } else if ((count.value ?: 0) <= 0 && accidentFlag) {
                    val updatedNotification = NotificationCompat.Builder(nowContext, "noti")
                        .setContentTitle("전복사고 발생")
                        .setContentText("정상적으로 신고되었습니다.")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .build()
                    NotificationManagerCompat.from(nowContext).notify(1, updatedNotification)


                    val Notification = NotificationCompat.Builder(nowContext, "noti")
                        .setContentTitle("농촌 실행중")
                        .setContentText("농촌 서비스가 안전하게 지키고 있습니다.")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .build()

                    with(NotificationManagerCompat.from(nowContext)) {
                        if (ActivityCompat.checkSelfPermission(
                                nowContext,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            notify(2, Notification)
                        }

                    }

                    //문자전송
                    val firebase = FirebaseCommunication()
                    val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
                    val userID = sharedPreferences.getString("ID", "")
                    val email = userID.toString()
                    val accidentAddress = addressChannel.receive().let {AddressConverter.convertAddressToString(it) }?:"위치 정보를 확인할 수 없습니다."
                    val receiveLocation = locationChannel.receive()
                    val locationText = "위도: ${receiveLocation.latitude}, 경도: ${receiveLocation.longitude}"
                    Log.d("[로그]", "$accidentAddress")
                    Log.d("[로그]", "latitiude : ${receiveLocation.latitude}, longitude : ${receiveLocation.longitude}")

                    firebase.fetchUserByDocumentId(email) { userInfo ->
                        if (userInfo != null) {
                            Log.d(
                                "[로그]",
                                "사용자 이름: ${userInfo.name}, 나이: ${userInfo.age}, 이메일: ${userInfo.email}"
                            )
                            if (ContextCompat.checkSelfPermission(
                                    nowContext,
                                    Manifest.permission.SEND_SMS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {//권한이 없다면

                            } else { //권한이 있다면 SMS를 보낸다.

                                val smsText :String = "${userInfo.name}님께서 사고를 당하셨습니다."
                                val smsManager = SmsManager.getDefault()
                                try {
                                    Log.d("로그","accidentadd : $accidentAddress")
                                    userInfo.emergencyContactList.forEach{number->
                                        smsManager.sendTextMessage(
                                            "+82" + number,
                                            null,
                                            smsText,
                                            null,
                                            null
                                        )
                                        smsManager.sendTextMessage(
                                            "+82" + number,
                                            null,

                                            locationText,
                                            null,
                                            null
                                        )

                                        smsManager.sendTextMessage(
                                            "+82" + number,
                                            null,
                                            "$accidentAddress",
                                            null,
                                            null
                                        )
                                    }

                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                    Toast.makeText(baseContext, ex.message, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }

                        } else {
                            Log.d("[로그]", "사용자 정보를 찾을 수 없습니다.")
                        }

                        changeAccidentFlag(false)
                        count.value=30
                    }
                }
                delay(1000)
            }
        }

        return START_NOT_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("[로그]", "서비스 종료")
        serviceScope.cancel()
        bluetoothRepository.disconnect()
        locationChannel.close()
        addressChannel.close()
        setServiceState(false)

        val intent = Intent("SERVICE_STOPPED")
        sendBroadcast(intent)
    }

    public fun userSafe() {
        changeAccidentFlag(false)
        count.postValue(30)

        val updatedNotification = NotificationCompat.Builder(nowContext, "noti")
            .setContentTitle("농촌 실행중")
            .setContentText("농촌 서비스가 안전하게 지키고 있습니다.")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        with(NotificationManagerCompat.from(nowContext)) {
            if (ActivityCompat.checkSelfPermission(
                    nowContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(2, updatedNotification)
            }
        }

    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    public fun changeAccidentFlag(flag: Boolean) {
        accidentFlag = flag
    }

    fun showScreen(data: Int) {
        val intent = Intent(this, AccidentActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            Log.d("[로그]", "data 받을 시 화면 띄우기")
            putExtra("timer", (count.value ?: 0))
        }
        startActivity(intent)
    }
}