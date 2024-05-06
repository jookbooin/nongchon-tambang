package com.capstone.nongchown.View.Activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.nongchown.R
import com.capstone.nongchown.ViewModel.UserProfileViewModel

class UserProfileActivity : AppCompatActivity() {

    private lateinit var userName: EditText
    private lateinit var userEmail: EditText
    private lateinit var userPhone: EditText
    private lateinit var emergencyContact: LinearLayout
    private lateinit var emergencyAddButton: Button
    private lateinit var saveButton: Button

    private val emergencyContactList = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        userName = findViewById<EditText>(R.id.user_name)
        userEmail = findViewById<EditText>(R.id.user_email)
//        userPhone = findViewById<EditText>(R.id.user_phone)
        emergencyContact = findViewById<LinearLayout>(R.id.emergency_contact_list)
        emergencyAddButton = findViewById<Button>(R.id.emergency_contact_addButton)
        saveButton = findViewById<Button>(R.id.user_profile_saveButton)

        saveButton.setOnClickListener {
            val name = userName.text.toString()
            val email = userEmail.text.toString()
            val phone = userPhone.text.toString()

//            UserProfileViewModel().userProfileSave(name, email, phone, eContact)
        }

        emergencyAddButton.setOnClickListener {
            addEmergencyContact()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_profile)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun addEmergencyContact() {
        val inflater = LayoutInflater.from(this)
        val editText = inflater.inflate(R.layout.emergency_contact_item, emergencyContact, false)
        emergencyContact.addView(editText, 0)
    }

}



