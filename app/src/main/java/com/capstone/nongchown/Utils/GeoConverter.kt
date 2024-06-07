package com.capstone.nongchown.Utils

import android.location.Location
import android.util.Log

class GeoConverter {
    fun convertFromString(input: String): Location {
        val (latitude: Double, longitude: Double) = try {
            formatValidate(input)
        } catch (e: IllegalArgumentException) {
            Log.e("[로그]","format 오류.. 처리 보류..",e)
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

    fun formatValidate(input: String): Pair<Double, Double> {
        Log.d("[로그]","formatValidate : $input" )
        val regex = """###latitude:(\d+(?:\.\d+)?),longitude:(\d+(?:\.\d+)?)###""".toRegex()
        val matchResult = regex.find(input)

        if (matchResult == null) {
            throw IllegalArgumentException("입력 형식이 올바르지 않습니다: $input")
        }

        try {
            val latitude = matchResult.groupValues[1].toDouble()
            val longitude = matchResult.groupValues[2].toDouble()
            return Pair(latitude, longitude)
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("위도 또는 경도 값을 변환할 수 없습니다.: ${matchResult.groupValues[1]} $${matchResult.groupValues[2]} ", e)
        }
    }

//    private fun formatValidate(input: String): Pair<Double, Double> {
//        val splitComma = input.trim('#').split(',')
//        // 1) ,분리
//        if (splitComma.size != 2) throw IllegalArgumentException(",로 분리할 수 없습니다.: $splitComma")
//
//        //  2) :분리 -> 빈 문자열 오면 안됨
//        val lat = splitComma[0].split(":")[1]
//        val lng = splitComma[1].split(":")[1]
//
//        if (lat.isEmpty() || lng.isEmpty()) throw IllegalArgumentException("위도 또는 경도 값이 비어 있습니다: $splitComma")
//
//        val latitude: Double
//        val longitude: Double
//        try {
//            latitude = lat.toDouble()
//            longitude = lng.toDouble()
//        } catch (e: NumberFormatException) {
//            throw IllegalArgumentException("위도 또는 경도 값을 변환할 수 없습니다.: $splitComma")
//        }
//
//        return Pair(latitude, longitude)
//    }
}