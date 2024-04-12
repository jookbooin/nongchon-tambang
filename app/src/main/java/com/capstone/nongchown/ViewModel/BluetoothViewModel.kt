package com.capstone.nongchown.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.nongchown.Model.Event
import com.capstone.nongchown.Repository.BluetoothRepository

class BluetoothViewModel(private val bluetoothRepository: BluetoothRepository) : ViewModel(){

    private val _requestBleOn = MutableLiveData<Event<Boolean>>()
    private val _isBluetoothDenied = MutableLiveData<Boolean>()
    val requestBleOn: LiveData<Event<Boolean>>
        get() = _requestBleOn

    val isBluetoothDenied: LiveData<Boolean> = _isBluetoothDenied
    fun setBluetoothDenied(isDenied: Boolean) {
        _isBluetoothDenied.value = isDenied
    }


}