package com.mine.flowpay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mine.flowpay.data.Users
import com.mine.flowpay.data.database.AppDatabase
import com.mine.flowpay.data.repository.UserRepository
import com.mine.flowpay.app.FlowpayApp
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository
    private val _currentUser = MutableLiveData<Users>()
    val currentUser: LiveData<Users> = _currentUser

    init {
        val database = (application as FlowpayApp).database
        repository = UserRepository(database.userDao())
    }

    fun loadUser(userId: Long) = viewModelScope.launch {
        val user = repository.getUserById(userId)
        user?.let {
            _currentUser.value = it
        }
    }

    fun insertUser(user: Users) = viewModelScope.launch {
        repository.insertUser(user)
    }

    fun updateUser(user: Users) = viewModelScope.launch {
        repository.updateUser(user)
        // Update LiveData if this is the current user
        if (_currentUser.value?.user_id == user.user_id) {
            _currentUser.value = user
        }
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
            // Update LiveData if this is the current user
            if (_currentUser.value?.user_id == userId) {
                _currentUser.value = it
            }
        }
    }
}
