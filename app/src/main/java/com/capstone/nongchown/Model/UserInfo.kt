package com.capstone.nongchown.Model

data class UserInfo(
    val name: String = "",
    val email: String = "",
    val age: String = "",
    val gender: String = "",
    val emergencyContactList: MutableList<String> = mutableListOf()
){

}
