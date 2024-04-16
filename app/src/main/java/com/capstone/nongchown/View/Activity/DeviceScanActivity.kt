package com.capstone.nongchown.View.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.nongchown.R
import com.capstone.nongchown.ViewModel.BluetoothViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceScanActivity : AppCompatActivity() {

    val bluetoothViewModel by viewModels<BluetoothViewModel>()
    // Hilt 추가하면 -> val viewModel: BluetoothViewModel = hiltViewModel() 다음과 같이 씀

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_device_scan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        scanBluetoothDevice()

        val btnCancelScan: Button = findViewById(R.id.btncancelscan)
        btnCancelScan.setOnClickListener() {
            Log.d("scanCancel", "SCAN CANCEL")
            bluetoothViewModel.cancelBluetoothScan()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestroy", "STOP BLUETOOTH SCAN") // UNREGISTER RECEIVER
        bluetoothViewModel.stopBluetoothScan()
//        unregisterReceiver(scanDeviceReceiver)
    }

    private fun scanBluetoothDevice() {
        Log.d("onCreate", "SCAN BLUETOOTH DEVICES ") // UNREGISTER RECEIVER
        bluetoothViewModel.startBluetoothScan()
    }

//    @SuppressLint("MissingPermission")
//    private fun scanBluetoothDevice() {
//        val filter = IntentFilter()
//        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED) //블루투스 상태변화 액션
//        filter.addAction(BluetoothDevice.ACTION_FOUND) //기기 검색됨
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED) //기기 검색 시작
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) //기기 검색 종료
//
//        registerReceiver(scanDeviceReceiver, filter)
//        bluetoothAdapter?.startDiscovery()          //블루투스 기기 검색 시작 -> 비동기식 -> 12초의 조회 스캔
//    }
//
//    @Suppress("DEPRECATION", "MissingPermission")
//    private val scanDeviceReceiver = object : BroadcastReceiver() {
//
//        override fun onReceive(context: Context?, intent: Intent?) {
//            var action = ""
//            if (intent != null) {
//                action = intent.action.toString() //입력된 action
//            }
//
//            when (action) {
//                BluetoothAdapter.ACTION_STATE_CHANGED -> {
//                    Log.d("scanDevice1", "STATE CHANGED")
//                }
//
//                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
//                    Log.d("scanDevice2", "DISCOVERY STARTED")
//                }
//
//                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
//                    Log.d("scanDevice3", "DISCOVERY FINISHED")
//                }
//
//                BluetoothDevice.ACTION_FOUND -> {
//                    val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
//                    var name: String? = null
//                    if (device != null && device.name != null) {
//                        Log.d("scanDevice4", "${device.name} ${device.address}")
//                    }
//                }
//            }
//
//        }
//    }


}