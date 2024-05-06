package com.capstone.nongchown.ViewModel

import android.provider.ContactsContract.CommonDataKinds.Email
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.capstone.nongchown.R

class UserProfileViewModel : ViewModel() {
    fun loadStoredData(){
//        앱을 시작하면 데이터베이스에서 사용자 정보를 긁어오는 메소드
    }

    fun validateEmail(email: String): String {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (emailRegex.matches(email)) {
            return email
        } else {
            throw IllegalArgumentException("Invalid email format")
        }
    }

    fun validateAge(age:String):String{
        val ageRegex = "^(?:150|[1-9]?[0-9])$".toRegex()
        if (ageRegex.matches(age)) {
            return age
        } else {
            throw IllegalArgumentException("Invalid age range")
        }
    }

    fun validatePhone(number: String): String {
        // 숫자와 하이픈만 허용
        val cleanNumber = number.filter { it.isDigit() || it == '-' }
        val digitsOnly = cleanNumber.filter { it.isDigit() }

        // 정규식을 사용하여 유효한 번호 형식인지 검사 (하이픈 포함)
        val phoneRegexWithHyphen = "^\\d{2,3}-\\d{3,4}-\\d{4}$".toRegex()
        val phoneRegexWithoutHyphen = "^\\d{10,11}$".toRegex()

        if (phoneRegexWithHyphen.matches(cleanNumber)) {
            return cleanNumber  // 이미 유효한 형식
        } else if (phoneRegexWithoutHyphen.matches(digitsOnly)) {
            // 하이픈이 없는 경우에는 적절한 위치에 하이픈 추가
            return when (digitsOnly.length) {
                10 -> "${digitsOnly.substring(0, 3)}-${
                    digitsOnly.substring(
                        3,
                        6
                    )
                }-${digitsOnly.substring(6)}"

                11 -> "${digitsOnly.substring(0, 3)}-${
                    digitsOnly.substring(
                        3,
                        7
                    )
                }-${digitsOnly.substring(7)}"

                else -> throw IllegalArgumentException("Invalid phone number length.")
            }
        } else {
            throw IllegalArgumentException("Invalid phone number format.")
        }
    }

    fun userProfileSave(
        name: String,
        email: String,
        age: String,
        gender: String,
        emergencyContactList: MutableList<String>
    ) {
        try {
            val validEmail = validateEmail(email)
            val validAge = validateAge(age)
            val validEmergencyContacts = emergencyContactList.map { contact ->
                validatePhone(contact)
            }
//            값에 별 문제가 없으면 데이터베이스에 저장
        } catch (e: IllegalArgumentException) {
            println("Error: ${e.message}")
        }

    }


}