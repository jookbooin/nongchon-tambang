package com.capstone.nongchown.Repository

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.StateFlow

interface BluetoothRepository {

    fun startDiscovery(): StateFlow<List<BluetoothDevice>>
    fun getPairedDevice(): StateFlow<List<BluetoothDevice>>
    suspend fun connectToDevice(bluetoothDevice: BluetoothDevice)
    fun stopDiscovery()
    fun cancelDiscovery()
    fun isBluetoothEnabled() : Boolean
    fun isBluetoothSupport() : Boolean
    suspend fun sendDataToDevice()

}