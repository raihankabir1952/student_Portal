package com.example.login_page

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Routine(
    val courseCode: String = "",
    val courseName: String = "",
    val day: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val facultyName: String = "",
    val roomNo: String = ""
)