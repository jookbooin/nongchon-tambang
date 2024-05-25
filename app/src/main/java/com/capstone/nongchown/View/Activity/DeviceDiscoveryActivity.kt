package com.capstone.nongchown.View.Activity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Intent
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
import com.capstone.nongchown.Model.BluetoothService
import com.capstone.nongchown.Model.ForegroundService
import com.capstone.nongchown.R
import com.capstone.nongchown.Utils.showToast
import com.capstone.nongchown.ViewModel.BluetoothViewModel
import com.capstone.nongchown.ViewModel.BluetoothViewModel.DiscoveryState
import com.capstone.nongchown.databinding.ActivityDeviceDiscoveryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        showDiscoveredBluetoothDevice()

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

    private fun loading() {
        Log.d("[로그]", "LOADING")
        binding.textView5.text = "기기를 찾고 있습니다."
        bluetoothViewModel.loadingDiscovery()
    }

    private fun startDiscovery() {
        loading()
        bluetoothViewModel.startBluetoothDiscovery()
    }

    @SuppressLint("MissingPermission")
    private fun showDiscoveredBluetoothDevice() {
        // UNREGISTER RECEIVER
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bluetoothViewModel.bluetoothDiscoveryState.collect { state ->

                    when (state) {
                        is DiscoveryState.Loading -> loading()
                        is DiscoveryState.Success -> success(state.devices)
                        is DiscoveryState.Error -> Log.d("state error", "STATE ERROR")
                    }
                }
            }
        }
    }


    @SuppressLint("MissingPermission", "NotifyDataSetChanged")
    private fun success(devices: List<BluetoothDevice>) {
        Log.d("[로그]", "UPDATE LIST")
        // 디바이스 리스트를 화면에 표시하는 로직 구현
        devices?.forEach { device ->
            Log.d("[로그]", "SUCCESS ( Name: ${device.name}, Address: ${device.address} )")
        }

        discovredDeviceAdapter.deviceList = devices
        discovredDeviceAdapter.notifyDataSetChanged()
    }

    fun connectDevice() {
        discovredDeviceAdapter.itemClick = object : DiscoveredDeviceAdapter.ItemClick {

            override fun onClick(view: View, position: Int) {

                // service가 시작되어 있다면 종료
                val serviceIntent = Intent(this@DeviceDiscoveryActivity, BluetoothService::class.java)
                stopService(serviceIntent)

                val device = discovredDeviceAdapter.getDeviceAtPosition(position)
                //                bluetoothViewModel.connectToDevice(device)
            }
        }
    }

    fun cancelDiscovery() {
        binding.btncanceldiscovery.setOnClickListener() {
            bluetoothViewModel.cancelBluetoothDiscovery()
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

    fun handleConnectionResult(flag: Boolean) {
        if (flag) {
            showToast("연결되었습니다.")
            startForegroundService()
        } else {
            showToast("연결 실패했습니다.")
        }
    }

}