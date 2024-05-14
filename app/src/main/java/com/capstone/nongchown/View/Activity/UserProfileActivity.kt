package com.capstone.nongchown.View.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.capstone.nongchown.R
import com.capstone.nongchown.ViewModel.UserProfileViewModel
import kotlinx.coroutines.launch

class UserProfileActivity : AppCompatActivity() {
    private val userprofileViewModel = UserProfileViewModel()
    private lateinit var pageScroll: ScrollView

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var age: String
    private lateinit var gender: String
    private val emergencyContactList = mutableListOf<String>()

    private val emergencyAddButton: Button by lazy {
        findViewById(R.id.emergency_contact_addButton)
    }
    private val saveButton: Button by lazy {
        findViewById(R.id.user_profile_saveButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        pageScroll = findViewById(R.id.user_profile_scroll)

        val userName = findViewById<EditText>(R.id.user_name)
        userName.addTextChangedListener {
            Log.d("[로그]", "name changed")
            saveButton.isEnabled = true
        }

        val userEmail = findViewById<EditText>(R.id.user_email)
        userEmail.addTextChangedListener {
            Log.d("[로그]", "email changed")
            saveButton.isEnabled = true
        }

        val userAge = findViewById<EditText>(R.id.user_age)
        userAge.addTextChangedListener {
            Log.d("[로그]", "age changed")
            saveButton.isEnabled = true
        }

        val userGender = findViewById<Spinner>(R.id.gender)
//        userGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                if (userGender.selectedItem.toString() != gender) {
//                    saveButton.isEnabled = true
//                }
//                Log.d("[로그]", "gender changed")
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // Do nothing
//            }
//        }

        val emergencyContacts = findViewById<LinearLayout>(R.id.emergency_contact_list)

        emergencyAddButton.setOnClickListener {
            addEmergencyContact(emergencyContacts)
        }

        saveButton.setOnClickListener {
            try {
                Log.d("[로그]", "저장 버튼 클릭")
                emergencyContacts.children.filterIsInstance<EditText>()
                    .forEach { emergencyContact ->
                        Log.d("[로그]", "emergencyContact: ${emergencyContact.text}")
                        emergencyContactList.add(emergencyContact.text.toString())
                    }
                val userInfo = UserProfileViewModel().userProfileSave(
                    userName.text.toString(),
                    userEmail.text.toString(),
                    userAge.text.toString(),
                    userGender.selectedItem.toString(),
                    emergencyContactList
                )
                this.name = userInfo.name
                this.email = userInfo.email
                this.age = userInfo.age
                this.gender = userInfo.gender
                this.emergencyContactList.clear()
                this.emergencyContactList.addAll(userInfo.emergencyContactList)

                saveButton.isEnabled = false
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, "입력 오류: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        //        앱 시작 시 데이터베이스로부터 사용자 데이터를 받아온다.(있다고 가정)
        initUserInfo(
            userName,
            userEmail,
            userAge,
            userGender,
            emergencyContacts
        )

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_profile)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initUserInfo(
        userName: EditText,
        userEmail: EditText,
        userAge: EditText,
        userGender: Spinner,
        emergencyContacts: LinearLayout
    ) {

        Log.d("[로그]", "initializing")

        email = "sanghoo1023@gmail.com"

        lifecycleScope.launch {
            val userInfo = userprofileViewModel.loadStoredData(email)

            userName.setText(userInfo.name)
            userEmail.setText(userInfo.email)
            userAge.setText(userInfo.age)
            userGender.setSelection((if (userInfo.gender == "남") 0 else 1))
            for (i: Int in 0..<userInfo.emergencyContactList.size) {
                addEmergencyContact(emergencyContacts)
                Log.d("[로그]", "addEmergencyContact(emergencyContacts)")
                val eContact = emergencyContacts.getChildAt(i)
                if (eContact is EditText) {
                    eContact.setText(userInfo.emergencyContactList[i])
                } else {
                    Log.d("[에러]", "비상 연락망 위젯 개수 오류")
                }
            }
        }


        Log.d("[로그]", "initializing complete")
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addEmergencyContact(emergencyContacts: LinearLayout) {
        val inflater = LayoutInflater.from(this)
        val eContact =
            inflater.inflate(R.layout.emergency_contact_item, emergencyContacts, false) as EditText
        eContact.addTextChangedListener {
            Log.d("[로그]", "emergencyContact changed")
            saveButton.isEnabled = true

            eContact.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val clearDrawable = eContact.compoundDrawablesRelative[2]
                    if (clearDrawable != null && event.rawX >= (eContact.right - clearDrawable.bounds.width())) {
                        eContact.setText("")
                        return@setOnTouchListener true
                    }
                }
                false
            }
        }
        emergencyContacts.addView(eContact, emergencyContacts.childCount - 1)
    }

    //    form 입력 시 해당 form 으로 스크롤 이동
//    private fun setupFocusListener(editText: EditText) {
//        editText.setOnFocusChangeListener { v, hasFocus ->
//            if (hasFocus) {
//                pageScroll.post {
//                    pageScroll.scrollTo(0, v.top)
//                }
//            }
//        }
//
//        // Optional: Adjusting scroll when text changes
//        editText.doOnTextChanged { _, _, _, _ ->
//            if (editText.hasFocus()) {
//                pageScroll.post {
//                    pageScroll.scrollTo(0, editText.top)
//                }
//            }
//        }
//    }
}



