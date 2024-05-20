package com.capstone.nongchown.Repository

import android.content.Context
import android.location.Geocoder
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddressRepositoryTest{

    lateinit var addressRepository: AddressRepository

    @Mock
    lateinit var geocoder: Geocoder

    @Mock
    lateinit var context: Context

    @Before
    fun setUp() {
        addressRepository = AddressRepository(context)
        geocoder = mock(Geocoder::class.java)
    }



    @Test
    fun addressNameTest1() {
        val latitude: Double = 37.493579
        val longitude: Double = 127.024797
        val resultString = "###latitude:$latitude,longitude:$longitude###"

//        geocoder.getFromLocation(latitude,longitude,1)?.firstOrNull()?.let { address ->
//            println( address.countryName )
//        }
        println("--------------[로그 출력]--------------\n")

        val result = resultString.trim('#').split(',')
        val lati = result[0].split(":")[1].toDouble()
        val longi = result[1].split(":")[1].toDouble()

        println("lati : $lati")
        println("longi : $longi")
    }

    @Test
    fun testNumberFormatException() {
        val resultString = "###latitude:잘못된값,longitude:127.024797###"

        assertThrows(NumberFormatException::class.java){
            val result = resultString.trim('#').split(',')
            result[0].split(":")[1].toDouble() // tn
        }

    }



}

