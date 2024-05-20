package com.capstone.nongchown

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi


// 컴파일 시점
object Constants {

        val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH ,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        @RequiresApi(Build.VERSION_CODES.S)
        val PERMISSIONS_S = arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        const val PERMISSION_REQUEST_CODE_S = 32 // 31 이상
        const val PERMISSION_REQUEST_CODE = 30
        const val SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB"

}