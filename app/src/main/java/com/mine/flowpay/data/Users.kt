package com.mine.flowpay.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mine.flowpay.R

@Entity(tableName = "users")
data class Users(
    @PrimaryKey(autoGenerate = true)
    var user_id: Long = 0,

    var email: String,
    var username: String,
    var password: String,
    
    @ColumnInfo(name = "wallet_balance")
    var walletBalance: Double = 0.0,
    var image: Int = R.drawable.img_profile_default,
    var creation_date: Long = System.currentTimeMillis(),
)