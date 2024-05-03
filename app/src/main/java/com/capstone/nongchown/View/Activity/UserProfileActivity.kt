package com.capstone.nongchown.View.Activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.nongchown.R
import com.capstone.nongchown.ViewModel.UserProfileViewModel

class UserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        val userName = findViewById<EditText>(R.id.user_name)
        val userEmail = findViewById<EditText>(R.id.user_email)
        val userPhone = findViewById<EditText>(R.id.user_phone)
        val emergencyContact = findViewById<EditText>(R.id.emergency_contact)
        val saveButton = findViewById<Button>(R.id.user_profile_saveButton)

        saveButton.setOnClickListener {
            val name = userName.text.toString()
            val email = userEmail.text.toString()
            val phone = userPhone.text.toString()
            val eContact = emergencyContact.text.toString()

            UserProfileViewModel().userProfileSave(name, email, phone, eContact)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_profile)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}



