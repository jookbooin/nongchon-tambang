package com.capstone.nongchown.ViewModel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nongchown.Model.Enum.BluetoothState
import com.capstone.nongchown.Repository.BluetoothRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(private val bluetoothRepository: BluetoothRepository) : ViewModel() {

    // UIstate
    private val _bluetoothDiscoveryState = MutableStateFlow<DiscoveryState>(DiscoveryState.Loading) // 내부 상태를 변경할 수 있는 StateFlow
    val bluetoothDiscoveryState: StateFlow<DiscoveryState> = _bluetoothDiscoveryState.asStateFlow() // // 외부에 노출된 관찰 전용 -> 독하고 있는 컴포넌트에 변경 사항을 전파

    // 연결 상태 status
    private val _connectionStatus = MutableStateFlow(false) // 기본값으로 false
    val connectionStatus: StateFlow<Boolean> = _connectionStatus.asStateFlow()

    val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val pairedDevices: StateFlow<List<BluetoothDevice>> = _pairedDevices

    fun loadingDiscovery() {
        _bluetoothDiscoveryState.value = DiscoveryState.Loading
    }

    fun startBluetoothDiscovery() {
        viewModelScope.launch {//ViewModel 내에서 비동기 작업을 쉽게 수행
            bluetoothRepository.startDiscovery().collect() {
                _bluetoothDiscoveryState.value = DiscoveryState.Success(it)
            }
        }
    }

    fun getPairedDevices() {
        viewModelScope.launch {
            bluetoothRepository.getPairedDevices().collect { pairedDevices ->
                _pairedDevices.value = pairedDevices
            }
        }
    }

    fun stopBluetoothDiscovery() {
        bluetoothRepository.stopDiscovery()
    }

    fun cancelBluetoothDiscovery() {
        bluetoothRepository.cancelDiscovery()
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(bluetoothDevice: BluetoothDevice) {
        viewModelScope.launch {
            _connectionStatus.value = false
            try {
                bluetoothRepository.connectToDevice(bluetoothDevice)
                _connectionStatus.value = true // 연결 성공
            } catch (e: Exception) {
                Log.d("[로그]", "연결 실패: ${bluetoothDevice.name} : ${bluetoothDevice.address} 페어링 상태 : ${bluetoothDevice.bondState}\n ${e.message}")
                _connectionStatus.value = false
            }
        }
    }

    fun sendDataToDevice() {
        viewModelScope.launch {
            bluetoothRepository.sendDataToDevice()
        }
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothRepository.isBluetoothEnabled()
    }

    fun isBluetoothSupport(): Boolean {
        return bluetoothRepository.isBluetoothSupport()
    }

    fun checkBluetoothState(): BluetoothState {

        if (!isBluetoothSupport()) {
            return BluetoothState.NOT_SUPPORT // NOT_SUPPORT
        }

        if (!isBluetoothEnabled()) {  //  비활성화 상태
            Log.d("[로그]", "기기의 블루투스 비활성화 상태")
            return BluetoothState.DISABLED // DISABLED
        }

        Log.d("[로그]", "기기의 블루투스 활성화 상태")
        return BluetoothState.ENABLED      // ENABLED
    }




    sealed class DiscoveryState {
        object Loading : DiscoveryState()
        data class Success(val devices: List<BluetoothDevice>) : DiscoveryState()
        data class Error(val exception: Throwable) : DiscoveryState()
    }

//    sealed class UiState<out T> {
//        object Loading : UiState<Nothing>()
//        data class Success<out T>(val data: T) : UiState<T>()
//        data class Error(val exception: Throwable) : UiState<Nothing>()
//    }

}