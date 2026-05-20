package com.example.gabsstudentstay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_preferences")
data class SavedPreferenceEntity(
    @PrimaryKey(autoGenerate = true)
    val preferenceId: Int = 0,
    val studentId: Int,
    val maxPrice: Double,
    val preferredLocation: String,
    val availabilityDate: String,
    val preferredAmenities: String,
    val notificationsEnabled: Boolean,
    val createdAt: Long
)
