package com.capstone.nongchown.ViewModel.Service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.nongchown.Model.ForegroundService

class AccidentViewModel(private val foreground: ForegroundService) :ViewModel() {
    private var _accidentCount = MutableLiveData<Int>()

    fun getTimerCount():LiveData<Int>{
        return _accidentCount
    }

    fun setTimerCount(){
        val count = foreground.getTimerCount()
        _accidentCount.value=count
    }

    fun userSafe(){
        // 유저가 안전할때 전달할 데이터
        foreground.userSafe()
    }


}