package com.example.gabsstudentstay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val paymentId: Int = 0,
    val studentId: Int,
    val listingId: Int,
    val amountPaid: Double,
    val paymentMethod: String,
    val referenceNumber: String,
    val paidAt: Long,
    val status: String
)
