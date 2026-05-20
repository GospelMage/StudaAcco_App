package com.example.gabsstudentstay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val studentId: Int = 0,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    // For this academic demo only. Real apps must hash passwords before storing them.
    val password: String,
    val role: String,
    val createdAt: Long
)
