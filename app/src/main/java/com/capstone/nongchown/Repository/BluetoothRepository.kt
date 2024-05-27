package com.capstone.nongchown.Repository

import android.bluetooth.BluetoothDevice
import android.location.Location
import com.capstone.nongchown.Model.PairedBluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothRepository {

    fun startDiscovery(): StateFlow<List<BluetoothDevice>>
    fun getPairedDevices(): StateFlow<List<BluetoothDevice>>
    fun getPairedDevices2(): StateFlow<List<PairedBluetoothDevice>>
    suspend fun connectToDevice(bluetoothDevice: BluetoothDevice)
    fun stopDiscovery()
//    fun cancelDiscovery()
    suspend fun sendDataToDevice()
    fun readDataFromDevice() : Flow<Location>
    fun isBluetoothEnabled(): Boolean
    fun isBluetoothSupport(): Boolean
    fun disconnect()

}