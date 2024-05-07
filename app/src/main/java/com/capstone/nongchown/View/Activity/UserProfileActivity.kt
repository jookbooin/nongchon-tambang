package com.capstone.nongchown.View.Activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.capstone.nongchown.R
import com.capstone.nongchown.ViewModel.UserProfileViewModel

class UserProfileActivity : AppCompatActivity() {

    private lateinit var pageScroll: ScrollView

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var age: String
    private lateinit var gender: String
    private val emergencyContactList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        pageScroll = findViewById(R.id.user_profile_scroll)
        val emergencyAddButton = findViewById<Button>(R.id.emergency_contact_addButton)
        val saveButton = findViewById<Button>(R.id.user_profile_saveButton)

        val userName = findViewById<EditText>(R.id.user_name)
        userName.addTextChangedListener {
            saveButton.isEnabled = true
        }

        val userEmail = findViewById<EditText>(R.id.user_email)
        userEmail.addTextChangedListener {
            saveButton.isEnabled = true
        }

        val age = findViewById<EditText>(R.id.age)
        age.addTextChangedListener {
            saveButton.isEnabled = true
        }

        val gender = findViewById<Spinner>(R.id.gender)
        gender.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                saveButton.isEnabled = true
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                // Do nothing
            }
        }

        val emergencyContacts = findViewById<LinearLayout>(R.id.emergency_contact_list)
        emergencyContacts.children.filterIsInstance<EditText>().forEach { emergencyContact ->
            emergencyContact.addTextChangedListener {
                saveButton.isEnabled = true
            }
        }

        emergencyAddButton.setOnClickListener {
            addEmergencyContact(emergencyContacts)
        }

        saveButton.setOnClickListener {
            this.name = userName.text.toString()
            this.email = userEmail.text.toString()
            this.age = age.text.toString()
            this.gender = gender.selectedItem.toString()

            emergencyContacts.children.filterIsInstance<EditText>().forEach { emergencyContact ->
                emergencyContactList.add(emergencyContact.text.toString())
            }
            Log.d("[로그]", "저장 버튼 클릭")
            UserProfileViewModel().userProfileSave(
                this.name,
                this.email,
                this.age,
                this.gender,
                emergencyContactList
            )
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_profile)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun addEmergencyContact(emergencyContacts: LinearLayout) {
        val inflater = LayoutInflater.from(this)
        val eContact = inflater.inflate(R.layout.emergency_contact_item, emergencyContacts, false)
        setupFocusListener(eContact as EditText)
        emergencyContacts.addView(eContact, emergencyContacts.childCount - 1)
    }

    private fun setupFocusListener(editText: EditText) {
        editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                pageScroll.post {
                    pageScroll.scrollTo(0, v.top)
                }
            }
        }

        // Optional: Adjusting scroll when text changes
        editText.doOnTextChanged { _, _, _, _ ->
            if (editText.hasFocus()) {
                pageScroll.post {
                    pageScroll.scrollTo(0, editText.top)
                }
            }
        }
    }
}



