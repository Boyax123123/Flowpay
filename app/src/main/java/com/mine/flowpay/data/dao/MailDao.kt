package com.mine.flowpay.data.dao

import androidx.room.*
import com.mine.flowpay.data.Mail
import com.mine.flowpay.data.relations.UserWithMails
import com.mine.flowpay.data.relations.TransactionWithMails
import com.mine.flowpay.data.relations.MailWithTransaction

@Dao
interface MailDao {
    // Insert operations
    @Insert
    suspend fun insertMail(mail: Mail)

    // Query operations
    @Query("SELECT * FROM mails WHERE user_id = :userId ORDER BY timestamp DESC")
    fun getUserMails(userId: Long): List<Mail>

    @Query("SELECT * FROM mails WHERE mail_id = :mailId")
    fun getMailById(mailId: Long): Mail

//----- Delete operations

    // Delete by object
    @Delete
    suspend fun deleteMail(mail: Mail)

    // Delete by ID
    @Query("DELETE FROM mails WHERE mail_id = :mailId")
    suspend fun deleteMailById(mailId: Long)

    // Delete all mails
    @Query("DELETE FROM mails")
    suspend fun deleteAllMails()

    // Delete all mails for a specific user
    @Query("DELETE FROM mails WHERE user_id = :userId")
    suspend fun deleteAllMailsByUserId(userId: Long)

    // Update operations
    @Update
    suspend fun updateMail(mail: Mail)

    // Additional useful queries
    @Query("SELECT * FROM mails WHERE user_id = :userId AND is_read = :isRead ORDER BY timestamp DESC")
    fun getMailsByReadStatus(userId: Long, isRead: Boolean): List<Mail>

    @Query("SELECT * FROM mails WHERE transaction_id = :transactionId ORDER BY timestamp DESC")
    fun getMailsByTransaction(transactionId: Long): List<Mail>

    // Relation queries
    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :userId")
    fun getUserWithMails(userId: Long): UserWithMails

    @Transaction
    @Query("SELECT * FROM transactions WHERE transaction_id = :transactionId")
    fun getTransactionWithMails(transactionId: Long): TransactionWithMails

    @Transaction
    @Query("SELECT * FROM mails WHERE mail_id = :mailId")
    fun getMailWithTransaction(mailId: Long): MailWithTransaction
}