package com.capstone.nongchown

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BluetoothMain : AppCompatActivity() {

    private var PERMISSION_REQUEST_CODE_S = 32 // 31 이상
    private var PERMISSION_REQUEST_CODE = 30

    private val bluetoothManager: BluetoothManager by lazy {
        getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }

    /** 권한 관련 코드들 모을 수 있을 듯? */
    /** Array<String> 권한들 존재하는지 확인 */
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /** 런타임 권한 모두 check 되었는지 확인 */
    private fun isPermissionGranted(results : IntArray):Boolean{
        for(result in results){
            if(result != PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
    }

    /** 블루투스 권한 처리*/
    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d("BluetoothPermissions", "Build.VERSION.SDK_INT >= Build.VERSION_CODES.S")
            if (!hasPermissions(this, PERMISSIONS_S)) {
                Log.d("hasPermissions", "PERMISSIONS_S x")
                requestPermissions(PERMISSIONS_S, PERMISSION_REQUEST_CODE_S)
            }else{
                Log.d("hasPermissions", "모든 권한이 허용되어 있다.")
            }
        } else {
            Log.d("BluetoothPermissions", "else")
            if (!hasPermissions(this, PERMISSIONS)) {
                Log.d("hasPermissions", "PERMISSIONS x")
                requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE)
            }else{
                Log.d("hasPermissions", "모든 권한이 허용되어 있다.")
            }
        }
    }

//    private val startForResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//            if (result.resultCode == RESULT_OK) {
//                Log.d("Bluetooth", "블루투스 활성화")
//            } else if (result.resultCode == RESULT_CANCELED) {
//                Log.d("Bluetooth", "사용자 블루투스 활성화 거부")
//            }
//        }


    /** Runtime 권한 선택 후 결과 callback */
    //    @RequiresApi(Build.VERSION_CODES.N)  // API 24 ( Android 7.0 Nougat 이상 )
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            PERMISSION_REQUEST_CODE_S ->{
                Log.d("PermissionResult","requestCode : $requestCode")
                if(grantResults.isNotEmpty() && isPermissionGranted(grantResults)){
                    Toast.makeText(this, "모든 권한을 허용하였습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    if(shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_SCAN)){
                        Toast.makeText(this, "블루투스 권한을 거절하였습니다.", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "블루투스 권한을 다시 묻지 않음을 하였습니다.", Toast.LENGTH_SHORT).show()
                    }
//                    for(index in grantResults.indices) {
//                        Log.d("PermissionResult", "grantResults[$index] : $ ${grantResults[index]}")
//                    }
                }
            }

            PERMISSION_REQUEST_CODE -> {}
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bluethooth_first)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 권한 처리
        requestBluetoothPermissions()

//        if (isBluetoothSupport()) { // 지원한다면
//            Log.d("bluetoothAdapter", "연결")
//
//            if (!isBluetoothEnabled()) {  //  비활성화 상태
//                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                startForResult.launch(enableBtIntent)
//            } else {
//
//            }
//        } else {
//            Toast.makeText(this, "블루투스를 지원하지 않는 장비입니다.", Toast.LENGTH_SHORT).show()
//            finish()
//        }

    }

    // 외부로 옮길 것들

    val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val PERMISSIONS_S = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    fun isBluetoothSupport(): Boolean {
        return if (bluetoothAdapter == null) {
            Log.d("BluetoothSupport", "블루투스 지원하지 않습니다.")
            false
        } else {
            Log.d("BluetoothSupport", "블루투스 지원합니다.")
            true
        }
    }

    // repository
    fun isBluetoothEnabled(): Boolean {
        return if (bluetoothAdapter?.isEnabled == false) {   // 비활성화 상태
            Log.d("BluetoothEnabled", "비활성화 상태")
            // 활성화 상태로 변경하도록 요청
            false
        } else {
            Log.d("BluetoothEnabled", "활성화 상태")
            true
        }
    }


}