package com.capstone.nongchown.Utils

import android.location.Location
import org.junit.Before
import org.junit.Test

class GeoConverterTest{
    lateinit var geoConverter: GeoConverter

    @Before
    fun setUp() {
        geoConverter = GeoConverter()
    }

    @Test
    fun formatTest(){
        val TestLocation = Location("").apply {
            latitude = 37.300392
            longitude = 127.039766
        }

//        val result1 = geoConverter.convertFromString("###latitude:잘못된값")
//        assertEquals(GeoCoordinate(37.300392, 127.039766), result1.latitude)
//
//        val result2 = geoConverter.convertFromString("###latitude:,longitude:127.024797###")
//        assertEquals(GeoCoordinate(37.300392, 127.039766), result2)
//
//        val result3 = geoConverter.convertFromString("###latitude:잘못된값,longitude:127.024797###")
//        assertEquals(GeoCoordinate(37.300392, 127.039766), result3)
//
//        val collect = geoConverter.convertFromString("###latitude:37.493579,longitude:127.024797###")
//        assertEquals(GeoCoordinate(37.493579, 127.024797), collect)
    }

}