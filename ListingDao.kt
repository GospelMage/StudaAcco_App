package com.example.gabsstudentstay.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gabsstudentstay.data.local.entity.ListingEntity

@Dao
interface ListingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListing(listing: ListingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListings(listings: List<ListingEntity>)

    @Query("SELECT * FROM listings ORDER BY listingId ASC")
    fun getAllListings(): LiveData<List<ListingEntity>>

    @Query("SELECT * FROM listings ORDER BY listingId ASC")
    fun getAllListingsNow(): List<ListingEntity>

    @Query("SELECT * FROM listings WHERE listingId = :listingId LIMIT 1")
    fun getListingById(listingId: Int): ListingEntity?

    @Query("SELECT * FROM listings WHERE status = 'Available' ORDER BY listingId ASC")
    fun getAvailableListings(): LiveData<List<ListingEntity>>

    @Query(
        """
        SELECT * FROM listings
        WHERE price <= :maxPrice
        AND location LIKE '%' || :location || '%'
        AND availabilityDate <= :availabilityDate
        ORDER BY price ASC
        """
    )
    fun filterListings(
        maxPrice: Double,
        location: String,
        availabilityDate: String
    ): LiveData<List<ListingEntity>>

    @Query("UPDATE listings SET status = :status WHERE listingId = :listingId")
    fun updateListingStatus(listingId: Int, status: String)

    @Query("SELECT COUNT(*) FROM listings")
    fun countListings(): Int
}
