package com.example.gabsstudentstay.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gabsstudentstay.data.local.entity.ReservationEntity

@Dao
interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReservation(reservation: ReservationEntity): Long

    @Query("SELECT * FROM reservations WHERE studentId = :studentId ORDER BY reservedAt DESC")
    fun getReservationsForStudent(studentId: Int): LiveData<List<ReservationEntity>>

    @Query("SELECT * FROM reservations WHERE listingId = :listingId LIMIT 1")
    fun getReservationForListing(listingId: Int): ReservationEntity?

    @Query("SELECT COUNT(*) FROM reservations WHERE listingId = :listingId")
    fun countReservationsForListing(listingId: Int): Int
}
