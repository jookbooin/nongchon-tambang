package com.capstone.nongchown.View.Activity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.capstone.nongchown.R
import com.capstone.nongchown.ViewModel.BluetoothViewModel
import com.capstone.nongchown.ViewModel.BluetoothViewModel.DiscoveryState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeviceDiscoveryActivity : AppCompatActivity() {

    val bluetoothViewModel by viewModels<BluetoothViewModel>()
    // Hilt 추가하면 -> val viewModel: BluetoothViewModel = hiltViewModel() 다음과 같이 씀

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_device_discovery)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        discoveryBluetoothDevice()
        bluetoothViewModel.startBluetoothDiscovery()

        val btnCancelDiscovery: Button = findViewById(R.id.btncanceldiscovery)
        btnCancelDiscovery.setOnClickListener() {
            Log.d("[btnCancelDiscovery]", "SCAN CANCEL")
            bluetoothViewModel.cancelBluetoothDiscovery()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("[onDestroy]", "STOP BLUETOOTH DISCOVERY") // UNREGISTER RECEIVER
        bluetoothViewModel.stopBluetoothDiscovery()
//        unregisterReceiver(scanDeviceReceiver)
    }

    @SuppressLint("MissingPermission")
    private fun discoveryBluetoothDevice() {
        Log.d("[discoveryBluetoothDevice]", "DISCOVERY BLUETOOTH DEVICES ") // UNREGISTER RECEIVER
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bluetoothViewModel.bluetoothDiscoveryState.collect { state ->

                    when (state) {
                        is DiscoveryState.Success -> success(state.devices)
                        is DiscoveryState.Loading ->loading()
                        is DiscoveryState.Error -> Log.d("state error", "STATE ERROR")

                    }
                }

            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun success(devices: List<BluetoothDevice>) {
        Log.d("[showDevices]", "PROGRESS BAR OFF")
        // 디바이스 리스트를 화면에 표시하는 로직 구현
        devices?.forEach { device ->
            Log.d("[showDevices]", "Name: ${device.name}, Address: ${device.address}")
        }
    }
    private fun loading(){
        Log.d("[loading]", "PROGRESS BAR ON")
    }

}