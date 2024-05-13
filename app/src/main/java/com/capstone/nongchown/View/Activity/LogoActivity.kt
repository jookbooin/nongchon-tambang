package com.capstone.nongchown.View.Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.capstone.nongchown.Constants
import com.capstone.nongchown.R
import com.capstone.nongchown.Utils.showToast

class LogoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.title)
        Log.d("test", "title")

        val prefs = getSharedPreferences("isFirst", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        // 1차 권한 처리 ( 위치 정보, 블루투스 활성화 )
        requestBluetoothPermissions()

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

    /** 블루투스 권한 처리*/
    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d("[로그]", "Build.VERSION.SDK_INT >= Build.VERSION_CODES.S")
            if (!hasPermissions(this, Constants.PERMISSIONS_S)) {
                Log.d("[로그]", "PERMISSIONS_S 권한 요청")
                requestPermissions(Constants.PERMISSIONS_S, Constants.PERMISSION_REQUEST_CODE_S)
            } else {
                Log.d("[로그]", "모든 권한이 허용되어 있다.")
            }
        } else {
            Log.d("[로그]", "Build.VERSION.SDK_INT < Build.VERSION_CODES.S")
            if (!hasPermissions(this, Constants.PERMISSIONS)) {
                Log.d("[로그]", "PERMISSIONS 권한 요청")
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

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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

}

//val snackBar = Snackbar.make(findViewById(android.R.id.content), "앱을 실행하려면 블루투스 권한을 허용해야 합니다.", Snackbar.LENGTH_LONG)
//    .setAction("확인") {
//        val intent = Intent()
//        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//        val uri = Uri.fromParts("package", packageName, null)
//        intent.data = uri
//        startActivity(intent)
//    }
//snackBar.show()