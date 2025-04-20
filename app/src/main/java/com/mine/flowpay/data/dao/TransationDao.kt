package com.mine.flowpay.data.dao

import androidx.room.*
import com.mine.flowpay.data.Transaction

@Dao
interface TransactionDao {
    // Create
    @Insert
    suspend fun createTransaction(transaction: Transaction): Long

    // Read
    @Query("SELECT * FROM transactions WHERE user_id = :userId")
    fun getUserTransactions(userId: Long): List<Transaction>

    @Query("SELECT * FROM transactions WHERE transaction_id = :transactionId")
    fun getTransactionById(transactionId: Long): Transaction

    @Query("SELECT * FROM transactions WHERE type = :type")
    fun getTransactionsByType(type: String): List<Transaction>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY timestamp DESC LIMIT 1")
    fun getLastTransactionForUser(userId: Long): Transaction

    // Update
    @Update
    suspend fun updateTransaction(transaction: Transaction)

    // Delete

    // Delete a transaction by its ID
    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    // Delete transaction by ID
    @Query("DELETE FROM transactions WHERE transaction_id = :transactionId")
    suspend fun deleteTransactionById(transactionId: Long)

    // Delete all transactions for a specific user
    @Query("DELETE FROM transactions WHERE user_id = :userId")
    suspend fun deleteAllUserTransactions(userId: Long)


}