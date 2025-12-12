package com.example.login_page

import com.google.firebase.Timestamp

data class Notice(
    val id: String = "",
    val title: String = "",
    val details: String = "",
    val timestamp: Timestamp? = null
)