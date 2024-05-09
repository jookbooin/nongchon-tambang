package com.capstone.nongchown.View.Activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.capstone.nongchown.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class FireBaseTestActivity() : AppCompatActivity() {
    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.firebase_test_layout)

        val btnAccident: Button = findViewById(R.id.find_user)
        btnAccident.setOnClickListener {
            val editText: EditText = findViewById(R.id.user_doc)
            val nameText: TextView = findViewById(R.id.user_name)
            val numberText: TextView = findViewById(R.id.user_number)
            val docRefName: String = editText.text.toString()

            val doc = db.collection("testData").document(docRefName)
            doc.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name")
                        nameText.text = name

                        val number = document.getString("number")
                        numberText.text = number
                    } else {
                        nameText.text = "유효하지 않은 문서입니다"
                        numberText.text = "유효하지 않은 문서입니다"
                    }
                }

        }


    }


}