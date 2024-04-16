package com.capstone.nongchown.ViewModel

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nongchown.Repository.BluetoothRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(private val bluetoothRepository: BluetoothRepository) : ViewModel(){

    private val _discoveredDeviceList = MutableStateFlow<List<BluetoothDevice>>(emptyList())  // 내부 상태를 변경할 수 있는 StateFlow
    val discoveredDeviceList: StateFlow<List<BluetoothDevice>> = _discoveredDeviceList  // 외부에 노출된 관찰 전용 -> 독하고 있는 컴포넌트에 변경 사항을 전파

    val _pairedDeviceList= MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val pairedDeviceList: StateFlow<List<BluetoothDevice>> = _pairedDeviceList

    fun startBluetoothScan() {
        viewModelScope.launch {//ViewModel 내에서 비동기 작업을 쉽게 수행
            val discoveredDevices = bluetoothRepository.startDiscovery().value
            _discoveredDeviceList.value = discoveredDevices
        }
    }

    fun stopBluetoothScan(){
            bluetoothRepository.stopDiscovery()
    }

    fun cancelBluetoothScan(){
        bluetoothRepository.cancelDiscovery()
    }

}