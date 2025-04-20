package com.mine.flowpay.data.repository

import com.mine.flowpay.data.Transaction
import com.mine.flowpay.data.dao.TransactionDao

class TransactionRepository(private val transactionDao: TransactionDao) {
    // Create
    suspend fun createTransaction(transaction: Transaction) = transactionDao.createTransaction(transaction)
    
    // Read
    fun getUserTransactions(userId: Long) = transactionDao.getUserTransactions(userId)
    fun getTransactionById(transactionId: Long) = transactionDao.getTransactionById(transactionId)
    fun getTransactionsByType(type: String) = transactionDao.getTransactionsByType(type)
    fun getAllTransactions() = transactionDao.getAllTransactions()
    
    // Update
    suspend fun updateTransaction(transaction: Transaction) = transactionDao.updateTransaction(transaction)
    
    // Delete
    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.deleteTransaction(transaction)
    suspend fun deleteTransactionById(transactionId: Long) = transactionDao.deleteTransactionById(transactionId)
    suspend fun deleteAllUserTransactions(userId: Long) = transactionDao.deleteAllUserTransactions(userId)
}