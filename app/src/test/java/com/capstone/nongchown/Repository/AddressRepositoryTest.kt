package com.capstone.nongchown.Repository

import android.content.Context
import android.location.Geocoder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddressRepositoryTest {

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

        assertThrows(NumberFormatException::class.java) {
            val result = resultString.trim('#').split(',')
            result[0].split(":")[1].toDouble() // tn
        }

    }

    @Test
    fun testSplit() {
        val string1 = "latitude:"
        val string2 = "latitude:123"
        val string3 = "latitude:xxx"

        val result1 = string1.split(":")
        val result2 = string2.split(":")
        val result3 = string3.split(":")

        assertEquals(2, result1.size)

        assertEquals(2, result2.size)

        assertEquals(2, result3.size)
    }


    @Test
    fun testEmptyException() {
        val resultString = "###latitude:,longitude:127.024797###"
        val result = resultString.trim('#').split(',')
        val latitudeResult = result[0].split(":")

        assertThrows(IllegalArgumentException::class.java) {

            // 비어있는지 확인
            if (latitudeResult.size < 2 || latitudeResult[1].isEmpty()) {
                throw IllegalArgumentException("비어 있음")
            }

            // 여기서 추가적인 처리를 계속할 수 있습니다.
            val latitude = latitudeResult[1].toDouble() // 이 부분에서 실제 값을 사용하기 전에 예외가 발생하지 않았는지 확인합니다.
        }
    }

}

