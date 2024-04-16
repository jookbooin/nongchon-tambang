package com.capstone.nongchown

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NongchownApplication : Application(){
    override fun onCreate() {
        super.onCreate()
    }
}