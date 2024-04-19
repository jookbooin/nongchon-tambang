package com.capstone.nongchown.View.Activity


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.nongchown.Constants
import com.capstone.nongchown.Model.Enum.BluetoothState
import com.capstone.nongchown.R
import com.capstone.nongchown.Utils.moveActivity
import com.capstone.nongchown.Utils.showToast
import com.capstone.nongchown.View.Activity.BaseActivity.BluetoothBaseActivity
import com.capstone.nongchown.ViewModel.BluetoothViewModel
import dagger.hilt.android.AndroidEntryPoint
import moveActivity
import showToast



@AndroidEntryPoint
class BluetoothActivity : BluetoothBaseActivity() {

    val bluetoothViewModel by viewModels<BluetoothViewModel>()   // Fragment KTX 적용

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_bluethooth)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1차 권한 처리 ( 위치 정보, 블루투스 활성화 )
        requestBluetoothPermissions()

        // 디바이스 연결 / 새 기기 추가
        val btnDeviceAdd: Button = findViewById(R.id.btndeviceadd)
        btnDeviceAdd.setOnClickListener {
            /**
             * 1. 권한 check
             *  BLUETOOTH_SCAN 허용 시 -> true
             *  BLUETOOTH_SCAN 1번 거절 시 -> 재요청
             *  BLUETOOTH_SCAN 2번 거절 시 -> 설정 page 이동
             */

            /**
             * 2. 블루투스 활성화 check
             * */
            when (checkBluetoothState()) {
                BluetoothState.ENABLED -> moveActivity(DeviceDiscoveryActivity::class.java)
                BluetoothState.DISABLED -> {
                    val bluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startForResult.launch(bluetoothIntent)
                }

                else -> showToast("블루투스를 지원하지 않는 장비입니다.")
            }
        }
    }

    /** 권한 관련 코드들 모을 수 있을 듯? */
    /** Array<String> 권한들 존재하는지 확인 */
    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /** 런타임 권한 모두 check 되었는지 확인 */
    fun isPermissionGranted(results: IntArray): Boolean {
        for (result in results) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /** 블루투스 권한 처리*/
    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d("[로그]", "Build.VERSION.SDK_INT >= Build.VERSION_CODES.S")
            if (!hasPermissions(this, Constants.PERMISSIONS_S)) {
                Log.d("[로그]", "PERMISSIONS_S x")
                requestPermissions(Constants.PERMISSIONS_S, Constants.PERMISSION_REQUEST_CODE_S)
            } else {
                Log.d("[로그]", "모든 권한이 허용되어 있다.")
            }
        } else {
            Log.d("[로그]", "Build.VERSION.SDK_INT < Build.VERSION_CODES.S")
            if (!hasPermissions(this, Constants.PERMISSIONS)) {
                Log.d("[로그]", "PERMISSIONS x -> requestPermissions")
                requestPermissions(Constants.PERMISSIONS, Constants.PERMISSION_REQUEST_CODE)
            } else {
                Log.d("[로그]", "모든 권한이 허용되어 있다.")
            }
        }
    }


    /** Runtime 권한 선택 후 결과 callback */
    //    @RequiresApi(Build.VERSION_CODES.N)  // API 24 ( Android 7.0 Nougat 이상 )
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            Constants.PERMISSION_REQUEST_CODE_S -> {
                Log.d("[로그]", "requestCode : $requestCode")

                if (grantResults.isNotEmpty() && isPermissionGranted(grantResults)) {
                    Log.d("[로그]", "모든 권한을 허용하였습니다.")
                    showToast("모든 권한을 허용하였습니다.")
                } else {
                    // 위치 권한
                    if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("[로그]", "위치 권한을 허용하였습니다.")
                    } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {  // 위치 권한 2번 확인
                        Log.d("[로그]", "위치 권한을 거절하였습니다.")
                    } else {
                        Log.d("[로그]", "위치 권한을 다시 묻지 않음으로 하였습니다.")
                    }

                    // 블루투스 권한
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("[로그]", "블루투스 권한을 허용하였습니다.")
                    } else if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_SCAN)) {  // 블루투스 권한 2번 확인
                        Log.d("[로그]", "블루투스 권한을 거절하였습니다.")
                    } else {
                        Log.d("[로그]", "블루투스 권한을 다시 묻지 않음으로 하였습니다.")
                    }

                }
            }

            Constants.PERMISSION_REQUEST_CODE -> {}
        }

    }


    fun checkBluetoothState(): BluetoothState {

        if (!isBluetoothSupport()) {
            finish()
            return BluetoothState.NOT_SUPPORT // NOT_SUPPORT
        }

        if (!isBluetoothEnabled()) {  //  비활성화 상태
            Log.d("[로그]", "기기의 블루투스 비활성화 상태")
            return BluetoothState.DISABLED // DISABLED
        }

        Log.d("[로그]", "기기의 블루투스 활성화 상태")
        return BluetoothState.ENABLED      // ENABLED
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            Log.d("[로그]", "블루투스 활성화")
        } else if (result.resultCode == RESULT_CANCELED) {
            Log.d("[로그]", "사용자 블루투스 활성화 거부")
        }
    }

    fun isBluetoothSupport(): Boolean {
        return if (bluetoothAdapter == null) {
            Log.d("[로그]", "기기가 블루투스 지원하지 않습니다.")
            false
        } else {
            Log.d("[로그]", "기기가 블루투스를 지원합니다.")
            true
        }
    }

    fun isBluetoothEnabled(): Boolean {
        return if (bluetoothAdapter?.isEnabled == false) {   // 기기의 블루투스 비활성화 상태
            false
        } else {
            true
        }
    }


}