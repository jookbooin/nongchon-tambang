package com.capstone.nongchown.Utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import java.util.Locale

class AddressConverter(context: Context, private val geocoderListener: GeocoderListener) {
    
    private val geocoder: Geocoder? by lazy {
        if (Geocoder.isPresent()) {
            Geocoder(context, Locale.KOREA)
        } else {
            null
        }
    }

    interface GeocoderListener {
        fun sendAddress(address: Address)
    }

    fun getAddressFromLocation(location: Location) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d("[로그]", "TIRAMISU 이상")
            geocoder?.getFromLocation(
                location.latitude,
                location.longitude,
                1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: List<Address>) { // geoCoding 성공하면 AddressList를 전달한다
                        Log.d("[로그]","onGeocode 성공")
                        geocoderListener.sendAddress(addresses[0])
                    }
                    
                    override fun onError(errorMessage: String?) {
                        Log.e("[로그]", "Geocoding 실패: $errorMessage")
                    }
                }
            )
        } else {
            Log.d("[로그]", "TIRAMISU 미만")
            val address: Address? = geocoder?.getFromLocation(location.latitude, location.longitude, 1)?.get(0)
            if (address != null) {
                geocoderListener.sendAddress(address)
            }
        }
    }

    companion object {
        fun convertAddressToString(address: Address): String {
            val addressLine: String = address.getAddressLine(0) ?: ""
            val postalCode: String = address.postalCode ?: ""
            val latitude = address.latitude
            val longitude = address.longitude

            val result = mutableListOf<String>()
            result.add(addressLine)
            result.add(postalCode)
//            result.add("(위도: $latitude, 경도: $longitude)")
            return result.joinToString(" ")
        }
    }

}