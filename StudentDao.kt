package com.example.gabsstudentstay.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gabsstudentstay.data.local.entity.StudentEntity

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudent(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudents(students: List<StudentEntity>)

    @Query("SELECT * FROM students WHERE email = :email LIMIT 1")
    fun getStudentByEmail(email: String): StudentEntity?

    @Query("SELECT * FROM students WHERE email = :email AND password = :password LIMIT 1")
    fun login(email: String, password: String): StudentEntity?

    @Query("SELECT * FROM students ORDER BY studentId ASC")
    fun getAllStudents(): List<StudentEntity>

    @Query("SELECT COUNT(*) FROM students")
    fun countStudents(): Int
}
