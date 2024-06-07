package com.capstone.nongchown.View.Activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.nongchown.Adapter.DiscoveredDeviceAdapter
import com.capstone.nongchown.Model.Enum.ConnectResult
import com.capstone.nongchown.Model.ForegroundService
import com.capstone.nongchown.R
import com.capstone.nongchown.Utils.showToast
import com.capstone.nongchown.ViewModel.BluetoothViewModel
import com.capstone.nongchown.databinding.ActivityDeviceDiscoveryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class DeviceDiscoveryActivity : AppCompatActivity() {

    val bluetoothViewModel by viewModels<BluetoothViewModel>()

    // Hilt 추가하면 -> val viewModel: BluetoothViewModel = hiltViewModel() 다음과 같이 씀
    lateinit var binding: ActivityDeviceDiscoveryBinding
    lateinit var discovredDeviceAdapter: DiscoveredDeviceAdapter

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDeviceDiscoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        startDiscovery()

        connectDevice()
        cancelDiscovery()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("[로그]", "STOP BLUETOOTH DISCOVERY") // UNREGISTER RECEIVER
        bluetoothViewModel.stopBluetoothDiscovery()
    }

    private fun setupRecyclerView() {
        discovredDeviceAdapter = DiscoveredDeviceAdapter(emptyList())
        binding.devicerv.apply {
            adapter = discovredDeviceAdapter
            layoutManager = LinearLayoutManager(this@DeviceDiscoveryActivity)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun startDiscovery() {
        bluetoothViewModel.startBluetoothDiscovery()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bluetoothViewModel.discoverydeviceList.collect { devices ->
                    discovredDeviceAdapter.deviceList = devices
                    discovredDeviceAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    fun connectDevice() {
        discovredDeviceAdapter.itemClick = object : DiscoveredDeviceAdapter.ItemClick {

            override fun onClick(view: View, position: Int) {
                lifecycleScope.launch {
                    if (ForegroundService.isServiceRunning()) {
                        Log.d("[로그]", "연결 시킬 기기 눌렀을 때 - 서비스 상태 : ${ForegroundService.isServiceRunning()}")
                        suspendCoroutine<Unit> { continuation ->
                            val filter = IntentFilter("SERVICE_STOPPED")
                            val serviceStoppedReceiver = object : BroadcastReceiver() {
                                override fun onReceive(context: Context?, intent: Intent?) {
                                    if (intent?.action == "SERVICE_STOPPED") {
                                        Log.d("[로그]", "SERVICE_STOPPED 수신")
                                        Log.d("[로그]", "종료 후 서비스 상태 : ${ForegroundService.isServiceRunning()}")
                                        context?.unregisterReceiver(this)
                                        continuation.resume(Unit)
                                    }
                                }
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                registerReceiver(serviceStoppedReceiver, filter, RECEIVER_EXPORTED)
                            } else {
                                registerReceiver(serviceStoppedReceiver, filter)
                            }

                            stopForegroundService()
                        }

                        if (!ForegroundService.isServiceRunning()) {
                            attemptConnectToDevice(position)
                        }
                    } else {
                        Log.d("[로그]", "연결 시킬 눌렀을 때 - 서비스 상태 : ${ForegroundService.isServiceRunning()}")
                        attemptConnectToDevice(position)
                    }
                }
            }
        }
    }

    fun cancelDiscovery() {
        binding.btncanceldiscovery.setOnClickListener() {
            finish()
        }
    }

    private fun startForegroundService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        ForegroundService.setServiceState(true)
    }

    private fun stopForegroundService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        stopService(serviceIntent)
        ForegroundService.setServiceState(false)
    }

    suspend fun attemptConnectToDevice(position: Int) {
        val device = discovredDeviceAdapter.getDeviceAtPosition(position)
        val flag = bluetoothViewModel.connectToDevice(device)
        delay(700)
        handleConnectionResult(flag)
    }

    fun handleConnectionResult(flag: ConnectResult) {
        if (flag == ConnectResult.CONNECT) {
            showToast("연결되었습니다.")
            finish()
            startForegroundService()
        } else if(flag == ConnectResult.DISCONNECT) {
            Log.d("[로그]", "연결 실패")
            showToast("연결 실패했습니다.")
        }
    }

}