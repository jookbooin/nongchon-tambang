package com.capstone.nongchown.View.Activity.BaseActivity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BluetoothBaseActivity : AppCompatActivity() {

    protected val bluetoothManager: BluetoothManager by lazy {
        getSystemService(BluetoothManager::class.java)
    }

    protected val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}