package com.mine.flowpay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mine.flowpay.data.Transaction
import com.mine.flowpay.data.Mail
import com.mine.flowpay.data.database.AppDatabase
import com.mine.flowpay.data.repository.TransactionRepository
import com.mine.flowpay.data.repository.MailRepository
import com.mine.flowpay.app.FlowpayApp
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository
    private val mailRepository: MailRepository
    private val _allTransactions = MutableLiveData<List<Transaction>>()
    val allTransactions: LiveData<List<Transaction>> = _allTransactions
    
    init {
        val database = (application as FlowpayApp).database
        repository = TransactionRepository(database.transactionDao())
        mailRepository = MailRepository(database.mailDao())
        _allTransactions.value = repository.getAllTransactions()
    }
    
    // Transaction operations
    fun getUserTransactions(userId: Long) {
        _allTransactions.value = repository.getUserTransactions(userId)
    }
    
    fun getTransactionsByType(type: String) {
        _allTransactions.value = repository.getTransactionsByType(type)
    }
    
    fun getTransactionById(transactionId: Long) = repository.getTransactionById(transactionId)

    fun createTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.createTransaction(transaction)
    }
    
    // Expose the repository for SortButtonsHandler
    fun getTransactionRepository(): TransactionRepository {
        return repository
    }
}