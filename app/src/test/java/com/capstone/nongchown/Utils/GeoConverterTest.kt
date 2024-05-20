package com.capstone.nongchown.Utils

import com.capstone.nongchown.Model.GeoCoordinate
import org.junit.Assert.assertEquals

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
        val result1 = geoConverter.convertFromString("###latitude:잘못된값")
        assertEquals(GeoCoordinate(37.300392, 127.039766), result1)

        val result2 = geoConverter.convertFromString("###latitude:,longitude:127.024797###")
        assertEquals(GeoCoordinate(37.300392, 127.039766), result2)

        val result3 = geoConverter.convertFromString("###latitude:잘못된값,longitude:127.024797###")
        assertEquals(GeoCoordinate(37.300392, 127.039766), result3)

        val collect = geoConverter.convertFromString("###latitude:37.493579,longitude:127.024797###")
        assertEquals(GeoCoordinate(37.493579, 127.024797), collect)
    }

}