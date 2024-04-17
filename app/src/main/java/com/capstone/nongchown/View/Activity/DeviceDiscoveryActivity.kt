package com.capstone.nongchown.View.Activity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.nongchown.Adapter.BluetoothAdapter
import com.capstone.nongchown.R
import com.capstone.nongchown.ViewModel.BluetoothViewModel
import com.capstone.nongchown.ViewModel.BluetoothViewModel.DiscoveryState
import com.capstone.nongchown.databinding.ActivityDeviceDiscoveryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeviceDiscoveryActivity : AppCompatActivity() {

    val bluetoothViewModel by viewModels<BluetoothViewModel>()

    // Hilt 추가하면 -> val viewModel: BluetoothViewModel = hiltViewModel() 다음과 같이 씀
    lateinit var binding: ActivityDeviceDiscoveryBinding
    lateinit var bluetoothAdapter: BluetoothAdapter

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDeviceDiscoveryBinding .inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()

        discoveryBluetoothDevice()
        bluetoothViewModel.startBluetoothDiscovery()

        binding.btncanceldiscovery.setOnClickListener() {
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

    private fun setupRecyclerView() {
        bluetoothAdapter = BluetoothAdapter(emptyList())
        binding.devicerv.apply {
            adapter = bluetoothAdapter
            layoutManager = LinearLayoutManager(this@DeviceDiscoveryActivity)
        }
    }

    @SuppressLint("MissingPermission")
    private fun discoveryBluetoothDevice() {
        Log.d("[discoveryBluetoothDevice]", "DISCOVERY BLUETOOTH DEVICES ") // UNREGISTER RECEIVER
        loading()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bluetoothViewModel.bluetoothDiscoveryState.collect { state ->

                    when (state) {
                        is DiscoveryState.Success -> success(state.devices)
                        is DiscoveryState.Loading -> loading()
                        is DiscoveryState.Error -> Log.d("state error", "STATE ERROR")

                    }
                }

            }
        }
    }

    @SuppressLint("MissingPermission", "NotifyDataSetChanged")
    private fun success(devices: List<BluetoothDevice>) {
        Log.d("[showDevices]", "UPDATE LIST")
        // 디바이스 리스트를 화면에 표시하는 로직 구현
        devices?.forEach { device ->
            Log.d("[showDevices]", "Name: ${device.name}, Address: ${device.address}")
        }

        bluetoothAdapter.deviceList = devices
        bluetoothAdapter.notifyDataSetChanged() //
//        binding.textView5.text = "기기 선택"

    }

    private fun loading() {
        Log.d("[loading]", "LOADING DISCOVERY")
        binding.textView5.text = "기기를 찾고 있습니다."
        bluetoothViewModel.loadingDiscovery()

    }

}