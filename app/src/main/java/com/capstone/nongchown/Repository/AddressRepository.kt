package com.capstone.nongchown.Repository

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.io.IOException
import java.util.Locale

class AddressRepository (private val context: Context){

    fun getAddress(latitude: Double, longitude: Double): List<Address>? {
        lateinit var address: List<Address>

        return try {
            val geocoder = Geocoder(context, Locale.KOREA)
            address = geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
            address
        } catch (e: IOException) {
            Log.e("[로그]", "Geocoder 에러 발생", e)
            null
        }
    }
}