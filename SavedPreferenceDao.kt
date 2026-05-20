package com.example.gabsstudentstay.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gabsstudentstay.data.local.entity.SavedPreferenceEntity

@Dao
interface SavedPreferenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPreference(preference: SavedPreferenceEntity): Long

    @Update
    fun updatePreference(preference: SavedPreferenceEntity)

    @Query("SELECT * FROM saved_preferences WHERE studentId = :studentId LIMIT 1")
    fun getPreferenceForStudent(studentId: Int): SavedPreferenceEntity?

    @Query("SELECT * FROM saved_preferences WHERE notificationsEnabled = 1 ORDER BY createdAt DESC")
    fun getAllEnabledPreferences(): List<SavedPreferenceEntity>
}
