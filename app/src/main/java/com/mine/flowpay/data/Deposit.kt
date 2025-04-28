package com.mine.flowpay.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// Deposit.kt
@Entity(
    tableName = "deposits",
    foreignKeys = [
        ForeignKey(
            entity = Users::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Deposit(
    @PrimaryKey(autoGenerate = true)
    val deposit_id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "datetime")
    val datetime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "payment_option")
    val paymentOption: String  // "gcash", "maya", "visa", "mastercard"
)
