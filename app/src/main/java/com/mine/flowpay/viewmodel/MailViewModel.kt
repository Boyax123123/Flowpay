package com.mine.flowpay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mine.flowpay.data.Mail
import com.mine.flowpay.data.database.AppDatabase
import com.mine.flowpay.data.repository.MailRepository
import com.mine.flowpay.data.relations.UserWithMails
import com.mine.flowpay.data.relations.TransactionWithMails
import com.mine.flowpay.data.relations.MailWithTransaction
import com.mine.flowpay.app.FlowpayApp
import kotlinx.coroutines.launch

class MailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MailRepository
    private val _userMails = MutableLiveData<List<Mail>>()
    val userMails: LiveData<List<Mail>> = _userMails
    private val _unreadMailCount = MutableLiveData<Int>()
    val unreadMailCount: LiveData<Int> = _unreadMailCount
    private val _hasPurchaseMail = MutableLiveData<Boolean>()
    val hasPurchaseMail: LiveData<Boolean> = _hasPurchaseMail

    init {
        val database = (application as FlowpayApp).database
        repository = MailRepository(database.mailDao())
        updateUnreadMailCount()
    }

    // Mail operations
    fun getUserMails(userId: Long) {
        _userMails.value = repository.getUserMails(userId)
    }

    fun getMailById(mailId: Long) = repository.getMailById(mailId)

    fun getMailsByReadStatus(userId: Long, isRead: Boolean) {
        _userMails.value = repository.getMailsByReadStatus(userId, isRead)
    }

    fun updateUnreadMailCount() = viewModelScope.launch {
        val userId = (getApplication() as FlowpayApp).loggedInuser?.user_id
        if (userId != null) {
            val unreadMails = repository.getMailsByReadStatus(userId, false)
            _unreadMailCount.value = unreadMails.size

            // Check if any unread mail is related to a purchase (has transaction_id)
            _hasPurchaseMail.value = unreadMails.any { it.transaction_id != null }
        } else {
            _unreadMailCount.value = 0
            _hasPurchaseMail.value = false
        }
    }

    fun getMailsByTransaction(transactionId: Long) {
        _userMails.value = repository.getMailsByTransaction(transactionId)
    }

    // Create mail
    fun createMail(mail: Mail) = viewModelScope.launch {
        repository.insertMail(mail)
        updateUnreadMailCount()
    }

    // Update mail
    suspend fun updateMail(mail: Mail) {
        repository.updateMail(mail)
        updateUnreadMailCount()
    }

    // Delete mail
    suspend fun deleteMail(mail: Mail) {
        repository.deleteMail(mail)
    }

    suspend fun deleteMailById(mailId: Long) {
        repository.deleteMailById(mailId)
    }

    // Relation operations
    fun getUserWithMails(userId: Long): UserWithMails = repository.getUserWithMails(userId)

    fun getTransactionWithMails(transactionId: Long): TransactionWithMails = 
        repository.getTransactionWithMails(transactionId)

    fun getMailWithTransaction(mailId: Long): MailWithTransaction = 
        repository.getMailWithTransaction(mailId)
}
