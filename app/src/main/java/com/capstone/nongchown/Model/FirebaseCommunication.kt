package com.capstone.nongchown.Model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

class FirebaseCommunication {
    private val db = FirebaseFirestore.getInstance()
    private val collectionUser = db.collection("user_data")

    // 특정 documentId로 문서 검색 및 UserInfo로 변환
    fun fetchUserByDocumentId(email: String, callback: (UserInfo?) -> Unit) {
        Log.d("[로그]", "fetchUserByDocumentId email: $email")
        val user = collectionUser.document(email)
        user.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    Log.d("[로그]", "addOnSuccessListener")
                    val userInfo = doc.toObject(UserInfo::class.java)
                    Log.d("[로그]", "userInfo: $userInfo")
                    callback(userInfo)
                } else {
                    Log.d("[로그]", "No such document")
                    callback(null)
                }
            }.addOnFailureListener { e ->
                e.printStackTrace()
                Log.d("[에러]", "Firebase connection error")
                callback(null)
            }
    }

    // UserInfo 데이터를 업데이트하거나 새로 생성
    suspend fun updateOrCreateUser(email: String, userInfo: UserInfo) =
        withContext(Dispatchers.IO) {
            val documentReference = collectionUser.document(email)
            try {
                val document = documentReference.get().await()
                if (document.exists()) {
                    documentReference.update(
                        mapOf(
                            "name" to userInfo.name,
                            "email" to userInfo.email,
                            "age" to userInfo.age,
                            "gender" to userInfo.gender,
                            "emergencyContactList" to userInfo.emergencyContactList
                        )
                    ).addOnSuccessListener {
                        Log.d("Firestore", "Document successfully updated.")
                    }.addOnFailureListener { e ->
                        Log.e("Firestore", "Error updating document", e)
                    }
                } else {
                    documentReference.set(userInfo).addOnSuccessListener {
                        Log.d("Firestore", "Document successfully created.")
                    }.addOnFailureListener { e ->
                        Log.e("Firestore", "Error creating document", e)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
}
