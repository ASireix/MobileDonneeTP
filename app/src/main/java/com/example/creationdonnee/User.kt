package com.example.creationdonnee

import android.icu.text.DateFormat
import java.util.Date

data class User(
    val mail: String,
    val name: String,
    val username: String,
    val phoneNumber: String,
    val dateOfBirth: Date,
    val hobbies: List<String>,
    val password: String
)
