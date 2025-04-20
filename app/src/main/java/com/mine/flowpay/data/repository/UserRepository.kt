package com.mine.flowpay.data.repository

import com.mine.flowpay.data.Users
import com.mine.flowpay.data.dao.UserDao

class UserRepository(private val userDao: UserDao) {
    fun insertUser(user: Users) = userDao.insertUser(user)
    fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    fun getUserByUsername(username: String) = userDao.getUserByUsername(username)
    fun getUserById(userId: Long) = userDao.getUserById(userId)
    fun getAllUsers() = userDao.getAllUsers()
    fun updateUser(user: Users) = userDao.updateUser(user)
    fun deleteUser(user: Users) = userDao.deleteUser(user)
} 