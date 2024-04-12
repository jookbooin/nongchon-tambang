package com.capstone.nongchown.Utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.moveActivity(destination: Class<out Activity>) {
    val intent = Intent(this, destination)
    startActivity(intent)
}