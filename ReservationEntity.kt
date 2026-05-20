package com.example.gabsstudentstay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservations")
data class ReservationEntity(
    @PrimaryKey(autoGenerate = true)
    val reservationId: Int = 0,
    val studentId: Int,
    val listingId: Int,
    val paymentId: Int?,
    val reservedAt: Long,
    val status: String
)
