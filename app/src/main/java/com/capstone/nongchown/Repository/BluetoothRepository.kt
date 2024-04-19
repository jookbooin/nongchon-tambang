package com.capstone.nongchown.Repository

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.StateFlow

interface BluetoothRepository {

    fun startDiscovery(): StateFlow<List<BluetoothDevice>>
    fun getPairedDevice(): StateFlow<List<BluetoothDevice>>
    fun connectToDevice(bluetoothDevice: BluetoothDevice)
    fun stopDiscovery()
    fun cancelDiscovery()

}