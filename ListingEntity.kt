package com.example.gabsstudentstay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listings")
data class ListingEntity(
    @PrimaryKey(autoGenerate = true)
    val listingId: Int = 0,
    val title: String,
    val price: Double,
    val location: String,
    val roomType: String,
    val amenities: String,
    val availabilityDate: String,
    val depositAmount: Double,
    val imageName: String,
    val status: String,
    val providerName: String,
    val providerPhone: String,
    val campusName: String,
    val distanceFromCampusKm: Double,
    val latitude: Double,
    val longitude: Double,
    val description: String
)
