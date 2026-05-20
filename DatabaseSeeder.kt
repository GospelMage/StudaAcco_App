package com.example.gabsstudentstay.data.seed

import com.example.gabsstudentstay.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {

    suspend fun seedIfNeeded(database: AppDatabase) {
        withContext(Dispatchers.IO) {
            if (database.studentDao().countStudents() == 0) {
                database.studentDao().insertStudents(SeedData.students())
            }

            if (database.listingDao().countListings() == 0) {
                database.listingDao().insertListings(SeedData.listings())
            }
        }
    }
}
