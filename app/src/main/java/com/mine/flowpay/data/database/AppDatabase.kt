package com.mine.flowpay.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mine.flowpay.data.dao.*
import com.mine.flowpay.data.*

@Database(
    entities = [
        Users::class,
        Product::class,
        ProductCategory::class,
        Transaction::class,
        Mail::class,
        Wishlist::class
    ],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): ProductCategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun mailDao(): MailDao
    abstract fun wishlistDao(): WishlistDao

    fun keepDatabaseAlive() {
        userDao().getAllUsers()
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flowpay_database"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}