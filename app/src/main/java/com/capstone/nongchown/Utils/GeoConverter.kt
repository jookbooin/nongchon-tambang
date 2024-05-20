package com.capstone.nongchown.Utils

import android.location.Location

class GeoConverter {
    fun convertFromString(input: String): Location {
        val (latitude: Double, longitude: Double) = try {
            formatValidate(input)
        } catch (e: IllegalArgumentException) {

            return Location("").apply {
                this.latitude = 37.300392
                this.longitude = 127.039766
            }

        }
        return Location("").apply {
            this.latitude = latitude
            this.longitude = longitude
        }
    }

    private fun formatValidate(input: String): Pair<Double, Double> {
        val splitComma = input.trim('#').split(',')
        // 1) ,분리
        if (splitComma.size != 2) throw IllegalArgumentException(",로 분리할 수 없습니다.: $splitComma")

        //  2) :분리 -> 빈 문자열 오면 안됨
        val lat = splitComma[0].split(":")[1]
        val lng = splitComma[1].split(":")[1]

        if (lat.isEmpty() || lng.isEmpty()) throw IllegalArgumentException("위도 또는 경도 값이 비어 있습니다: $splitComma")

        val latitude: Double
        val longitude: Double
        try {
            latitude = lat.toDouble()
            longitude = lng.toDouble()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("위도 또는 경도 값을 변환할 수 없습니다.: $splitComma")
        }

        return Pair(latitude, longitude)
    }
}