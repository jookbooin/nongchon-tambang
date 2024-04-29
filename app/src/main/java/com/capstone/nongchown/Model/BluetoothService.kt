package com.capstone.nongchown.Model

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.capstone.nongchown.Repository.BluetoothRepository
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

    private var startMode: Int = 0             // 서비스가 kill 될 때, 어떻게 동작할지를 나타냄
    private var binder: IBinder? = null        // bind 된 클라이언트와 소통하기 위한 인터페이스
    private var allowRebind: Boolean = false   // onRebind() 메소드가 사용될지 말지를 결정함

    override fun onCreate() {
        super.onCreate()
        Log.d("[로그]", "BluetoothService 생성")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // startService()에 의해 서비스가 시작될 때
        Log.d("[로그]", "BluetoothService 실행 시작")
        serviceScope.launch {
            bluetoothRepository.readDataFromDevice()
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
        serviceScope.cancel()
        Log.d("[로그]", "BluetoothService 종료")
        super.onDestroy()
    }
}