package com.mine.flowpay.data.dao

import androidx.room.*
import com.mine.flowpay.data.Deposit

@Dao
interface DepositDao {
    // Create
    @Insert
    suspend fun createDeposit(deposit: Deposit): Long

    // Read
    @Query("SELECT * FROM deposits WHERE user_id = :userId")
    fun getUserDeposits(userId: Long): List<Deposit>

    @Query("SELECT * FROM deposits WHERE deposit_id = :depositId")
    fun getDepositById(depositId: Long): Deposit

    @Query("SELECT * FROM deposits WHERE payment_option = :paymentOption")
    fun getDepositsByPaymentOption(paymentOption: String): List<Deposit>

    @Query("SELECT * FROM deposits ORDER BY datetime DESC")
    fun getAllDeposits(): List<Deposit>

    @Query("SELECT * FROM deposits WHERE user_id = :userId ORDER BY datetime DESC LIMIT 1")
    fun getLastDepositForUser(userId: Long): Deposit

    // Update
    @Update
    suspend fun updateDeposit(deposit: Deposit)

    // Delete
    @Delete
    suspend fun deleteDeposit(deposit: Deposit)

    // Delete deposit by ID
    @Query("DELETE FROM deposits WHERE deposit_id = :depositId")
    suspend fun deleteDepositById(depositId: Long)

    // Delete all deposits for a specific user
    @Query("DELETE FROM deposits WHERE user_id = :userId")
    suspend fun deleteAllUserDeposits(userId: Long)
}
