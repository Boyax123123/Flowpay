package com.mine.flowpay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mine.flowpay.data.Users
import com.mine.flowpay.data.database.AppDatabase
import com.mine.flowpay.data.repository.UserRepository
import com.mine.flowpay.app.FlowpayApp
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository
    val allUsers: List<Users>

    init {
        val database = (application as FlowpayApp).database
        repository = UserRepository(database.userDao())
        allUsers = repository.getAllUsers()
    }

    fun insertUser(user: Users) = viewModelScope.launch {
        repository.insertUser(user)
    }

    fun updateUser(user: Users) = viewModelScope.launch {
        repository.updateUser(user)
    }

    fun deleteUser(user: Users) = viewModelScope.launch {
        repository.deleteUser(user)
    }

    fun getUserById(userId: Long) = repository.getUserById(userId)

    fun getUserByEmail(email: String) = viewModelScope.launch {
        repository.getUserByEmail(email)
    }

    fun getUserByUsername(username: String) = viewModelScope.launch {
        repository.getUserByUsername(username)
    }

    // Update wallet balance for a user
    fun updateWalletBalance(userId: Long, newBalance: Double) = viewModelScope.launch {
        val user = repository.getUserById(userId)
        user?.let {
            it.walletBalance = newBalance
            repository.updateUser(it)
        }
    }
}
