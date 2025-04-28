package com.mine.flowpay.data.repository

import com.mine.flowpay.data.Deposit
import com.mine.flowpay.data.dao.DepositDao

class DepositRepository(private val depositDao: DepositDao) {
    // Create
    suspend fun createDeposit(deposit: Deposit) = depositDao.createDeposit(deposit)

    // Read
    fun getUserDeposits(userId: Long) = depositDao.getUserDeposits(userId)
    fun getDepositById(depositId: Long) = depositDao.getDepositById(depositId)
    fun getDepositsByPaymentOption(paymentOption: String) = depositDao.getDepositsByPaymentOption(paymentOption)
    fun getAllDeposits() = depositDao.getAllDeposits()
    fun getLastDepositForUser(userId: Long) = depositDao.getLastDepositForUser(userId)

    // Update
    suspend fun updateDeposit(deposit: Deposit) = depositDao.updateDeposit(deposit)

    // Delete
    suspend fun deleteDeposit(deposit: Deposit) = depositDao.deleteDeposit(deposit)
    suspend fun deleteDepositById(depositId: Long) = depositDao.deleteDepositById(depositId)
    suspend fun deleteAllUserDeposits(userId: Long) = depositDao.deleteAllUserDeposits(userId)
}
