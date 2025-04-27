package com.mine.flowpay.data.repository

import com.mine.flowpay.data.database.AppDatabase

class BaseRepository(private val database: AppDatabase) {
    val userRepository by lazy { UserRepository(database.userDao()) }
    val productRepository by lazy { ProductRepository(database.productDao(), database.categoryDao()) }
    val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
    val mailRepository by lazy { MailRepository(database.mailDao()) }
    val wishlistRepository by lazy { WishlistRepository(database.wishlistDao()) }
    val depositRepository by lazy { DepositRepository(database.depositDao()) }
}
