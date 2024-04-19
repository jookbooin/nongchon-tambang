package com.capstone.nongchown.ViewModel

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.nongchown.Repository.BluetoothRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(private val bluetoothRepository: BluetoothRepository) : ViewModel(){

    // UIstate
    private val _bluetoothDiscoveryState = MutableStateFlow<DiscoveryState>(DiscoveryState.Loading) // 내부 상태를 변경할 수 있는 StateFlow
    val bluetoothDiscoveryState: StateFlow<DiscoveryState> = _bluetoothDiscoveryState.asStateFlow() // // 외부에 노출된 관찰 전용 -> 독하고 있는 컴포넌트에 변경 사항을 전파

    fun loadingDiscovery(){
        _bluetoothDiscoveryState.value = DiscoveryState.Loading
    }
    fun startBluetoothDiscovery() {
        viewModelScope.launch {//ViewModel 내에서 비동기 작업을 쉽게 수행
            bluetoothRepository.startDiscovery().collect(){
                _bluetoothDiscoveryState.value = DiscoveryState.Success(it)
            }
        }
    }



    fun stopBluetoothDiscovery(){
            bluetoothRepository.stopDiscovery()
    }

    fun cancelBluetoothDiscovery(){
        bluetoothRepository.cancelDiscovery()
    }

    fun connectToDevice(bluetoothDevice : BluetoothDevice) {
        bluetoothRepository.connectToDevice(bluetoothDevice)

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