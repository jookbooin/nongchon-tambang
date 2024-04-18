package com.capstone.nongchown.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//private val foreground: ForegroundService
class AccidentViewModel() :ViewModel() {
    private var _accidentCount = MutableLiveData<Int>()

    fun getTimerCount():LiveData<Int>{
        return _accidentCount
    }

    fun setTimerCount(){
        //val count = foreground.getTimerCount()
        //_accidentCount.value=count
    }


}