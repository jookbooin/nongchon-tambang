package com.capstone.nongchown.View.Activity


import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.nongchown.Adapter.DeviceAdapter
import com.capstone.nongchown.Model.BluetoothService
import com.capstone.nongchown.Model.Enum.BluetoothState
import com.capstone.nongchown.Model.ForegroundService
import com.capstone.nongchown.R
import com.capstone.nongchown.Utils.moveActivity
import com.capstone.nongchown.Utils.showToast
import com.capstone.nongchown.ViewModel.BluetoothViewModel
import com.capstone.nongchown.databinding.ActivityBluetoothBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BluetoothActivity : AppCompatActivity(){

    val bluetoothViewModel by viewModels<BluetoothViewModel>()
    lateinit var binding: ActivityBluetoothBinding
    lateinit var deviceAdapter: DeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1차 권한 처리 ( 위치 정보, 블루투스 활성화 )
//        requestBluetoothPermissions()

        binding = ActivityBluetoothBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        showConnectSuccessMessage()

        deviceAdapter.itemClick = object : DeviceAdapter.ItemClick {

            override fun onClick(view: View, position: Int) {
                // 1. 우선 실행중인 service 제거
                val serviceIntent = Intent(this@BluetoothActivity, BluetoothService::class.java)
                stopService(serviceIntent)

                // 2. 연결
                val device = deviceAdapter.getDeviceAtPosition(position)
//                bluetoothViewModel.connectToDevice(device)
            }
        }

        val btnSendData: Button = findViewById(R.id.btnsenddata)
        btnSendData.setOnClickListener {
            bluetoothViewModel.sendDataToDevice()
        }

        // 디바이스 연결 / 새 기기 추가
        /**
         * 1. 권한 check
         *  BLUETOOTH_SCAN 허용 시 -> true
         *  BLUETOOTH_SCAN 1번 거절 시 -> 재요청
         *  BLUETOOTH_SCAN 2번 거절 시 -> 설정 page 이동
         *
         * 2. 블루투스 활성화 check
         */

        val btnDeviceAdd: Button = findViewById(R.id.btndevicediscovery)
        btnDeviceAdd.setOnClickListener {

            when (checkBluetoothState()) {
                BluetoothState.ENABLED -> moveActivity(DeviceDiscoveryActivity::class.java)
                BluetoothState.DISABLED -> {
                    val bluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startForResult.launch(bluetoothIntent)
                }

                else -> showToast("블루투스를 지원하지 않는 장비입니다.")
            }
        }

        val btnStopService: Button = findViewById(R.id.btnstopservice)
        btnStopService.setOnClickListener {
            val serviceIntent = Intent(this, BluetoothService::class.java)
            stopService(serviceIntent)
        }

    }
    override fun onResume() {
        super.onResume()
        bluetoothViewModel.getPairedDevices()
    }

    fun checkBluetoothState(): BluetoothState {

        if (!bluetoothViewModel.isBluetoothSupport()) {
            finish()
            return BluetoothState.NOT_SUPPORT // NOT_SUPPORT
        }

        if (!bluetoothViewModel.isBluetoothEnabled()) {  //  비활성화 상태
            Log.d("[로그]", "기기의 블루투스 비활성화 상태")
            return BluetoothState.DISABLED // DISABLED
        }

        Log.d("[로그]", "기기의 블루투스 활성화 상태")
        return BluetoothState.ENABLED      // ENABLED
    }


    private fun setupRecyclerView() {

        deviceAdapter = DeviceAdapter(emptyList())
        binding.paireddevice.apply {
            adapter = deviceAdapter
            layoutManager = LinearLayoutManager(this@BluetoothActivity)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bluetoothViewModel.pairedDevices.collect { devices->
                    deviceAdapter.updateDevices(devices)
                }
            }
        }
    }

    private fun showConnectSuccessMessage() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                bluetoothViewModel.connectionStatus.collect { isConnected ->
//                    if (isConnected) {
//                        showToast("연결되었습니다.")
//                        delay(1000)
//                        startBluetoothService()
//                    }
//                }
            }
        }
    }

    private fun startBluetoothService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        }else{
            startService(serviceIntent)
        }
    }

    /** 권한 관련 코드들 모을 수 있을 듯? */
    /** Array<String> 권한들 존재하는지 확인 */
//    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
//        for (permission in permissions) {
//            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
//                return false
//            }
//        }
//        return true
//    }

    /** 런타임 권한 모두 check 되었는지 확인 */
//    fun isPermissionGranted(results: IntArray): Boolean {
//        for (result in results) {
//            if (result != PackageManager.PERMISSION_GRANTED) {
//                return false
//            }
//        }
//        return true
//    }

    /** 블루투스 권한 처리*/
//    private fun requestBluetoothPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            Log.d("[로그]", "Build.VERSION.SDK_INT >= Build.VERSION_CODES.S")
//            if (!hasPermissions(this, Constants.PERMISSIONS_S)) {
//                Log.d("[로그]", "PERMISSIONS_S x")
//                requestPermissions(Constants.PERMISSIONS_S, Constants.PERMISSION_REQUEST_CODE_S)
//            } else {
//                Log.d("[로그]", "모든 권한이 허용되어 있다.")
//            }
//        } else {
//            Log.d("[로그]", "Build.VERSION.SDK_INT < Build.VERSION_CODES.S")
//            if (!hasPermissions(this, Constants.PERMISSIONS)) {
//                Log.d("[로그]", "PERMISSIONS x -> requestPermissions")
//                requestPermissions(Constants.PERMISSIONS, Constants.PERMISSION_REQUEST_CODE)
//            } else {
//                Log.d("[로그]", "모든 권한이 허용되어 있다.")
//            }
//        }
//    }


    /** Runtime 권한 선택 후 결과 callback */
    //    @RequiresApi(Build.VERSION_CODES.N)  // API 24 ( Android 7.0 Nougat 이상 )
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//
//            Constants.PERMISSION_REQUEST_CODE_S -> {
//                Log.d("[로그]", "requestCode : $requestCode")
//
//                if (grantResults.isNotEmpty() && isPermissionGranted(grantResults)) {
//                    Log.d("[로그]", "모든 권한을 허용하였습니다.")
//                    showToast("모든 권한을 허용하였습니다.")
//                } else {
//                    // 위치 권한
//                    if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
//                        Log.d("[로그]", "위치 권한을 허용하였습니다.")
//                    } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {  // 위치 권한 2번 확인
//                        Log.d("[로그]", "위치 권한을 거절하였습니다.")
//                    } else {
//                        Log.d("[로그]", "위치 권한을 다시 묻지 않음으로 하였습니다.")
//                    }
//
//                    // 블루투스 권한
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        Log.d("[로그]", "블루투스 권한을 허용하였습니다.")
//                    } else if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_SCAN)) {  // 블루투스 권한 2번 확인
//                        Log.d("[로그]", "블루투스 권한을 거절하였습니다.")
//                    } else {
//                        Log.d("[로그]", "블루투스 권한을 다시 묻지 않음으로 하였습니다.")
//                    }
//
//                }
//            }
//
//            Constants.PERMISSION_REQUEST_CODE -> {}
//        }
//    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            Log.d("[로그]", "블루투스 활성화")
        } else if (result.resultCode == RESULT_CANCELED) {
            Log.d("[로그]", "사용자 블루투스 활성화 거부")
        }
    }

}