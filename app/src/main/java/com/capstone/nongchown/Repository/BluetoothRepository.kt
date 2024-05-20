package com.capstone.nongchown.Repository

import android.bluetooth.BluetoothDevice
import com.capstone.nongchown.Model.GeoCoordinate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothRepository {

    fun startDiscovery(): StateFlow<List<BluetoothDevice>>
    fun getPairedDevices(): StateFlow<List<BluetoothDevice>>
    suspend fun connectToDevice(bluetoothDevice: BluetoothDevice)
    fun stopDiscovery()
    fun cancelDiscovery()
    suspend fun sendDataToDevice()
    fun readDataFromDevice() : Flow<GeoCoordinate>
    fun isBluetoothEnabled(): Boolean
    fun isBluetoothSupport(): Boolean
    fun disconnect()

}