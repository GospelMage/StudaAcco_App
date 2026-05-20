package com.example.gabsstudentstay.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gabsstudentstay.data.local.entity.PaymentEntity

@Dao
interface PaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPayment(payment: PaymentEntity): Long

    @Query("SELECT * FROM payments WHERE paymentId = :paymentId LIMIT 1")
    fun getPaymentById(paymentId: Int): PaymentEntity?

    @Query("SELECT * FROM payments WHERE studentId = :studentId ORDER BY paidAt DESC")
    fun getPaymentsForStudent(studentId: Int): LiveData<List<PaymentEntity>>
}
