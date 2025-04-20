package com.mine.flowpay.data.repository

import com.mine.flowpay.data.Mail
import com.mine.flowpay.data.dao.MailDao

class MailRepository(private val mailDao: MailDao) {
    // Insert operations
    suspend fun insertMail(mail: Mail) = mailDao.insertMail(mail)
    
    // Query operations
    fun getUserMails(userId: Long) = mailDao.getUserMails(userId)
    fun getMailById(mailId: Long) = mailDao.getMailById(mailId)
    fun getMailsByReadStatus(userId: Long, isRead: Boolean) = mailDao.getMailsByReadStatus(userId, isRead)
    fun getMailsByTransaction(transactionId: Long) = mailDao.getMailsByTransaction(transactionId)
    
    // Delete operations
    suspend fun deleteMail(mail: Mail) = mailDao.deleteMail(mail)
    suspend fun deleteMailById(mailId: Long) = mailDao.deleteMailById(mailId)
    suspend fun deleteAllMails() = mailDao.deleteAllMails()
    suspend fun deleteAllMailsByUserId(userId: Long) = mailDao.deleteAllMailsByUserId(userId)
    
    // Update operations
    suspend fun updateMail(mail: Mail) = mailDao.updateMail(mail)
    
    // Relation queries
    fun getUserWithMails(userId: Long) = mailDao.getUserWithMails(userId)
    fun getTransactionWithMails(transactionId: Long) = mailDao.getTransactionWithMails(transactionId)
    fun getMailWithTransaction(mailId: Long) = mailDao.getMailWithTransaction(mailId)
}