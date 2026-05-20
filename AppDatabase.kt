package com.example.gabsstudentstay.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gabsstudentstay.data.local.dao.ListingDao
import com.example.gabsstudentstay.data.local.dao.PaymentDao
import com.example.gabsstudentstay.data.local.dao.ReservationDao
import com.example.gabsstudentstay.data.local.dao.SavedPreferenceDao
import com.example.gabsstudentstay.data.local.dao.StudentDao
import com.example.gabsstudentstay.data.local.entity.ListingEntity
import com.example.gabsstudentstay.data.local.entity.PaymentEntity
import com.example.gabsstudentstay.data.local.entity.ReservationEntity
import com.example.gabsstudentstay.data.local.entity.SavedPreferenceEntity
import com.example.gabsstudentstay.data.local.entity.StudentEntity

@Database(
    entities = [
        StudentEntity::class,
        ListingEntity::class,
        ReservationEntity::class,
        PaymentEntity::class,
        SavedPreferenceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun listingDao(): ListingDao
    abstract fun reservationDao(): ReservationDao
    abstract fun paymentDao(): PaymentDao
    abstract fun savedPreferenceDao(): SavedPreferenceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gabs_student_stay_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
