package com.example.login_page

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Student(
    val uid: String = "",
    val name: String = "",
    val department: String = ""
)