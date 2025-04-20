package com.mine.flowpay.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "wishlists",
    primaryKeys = ["user_id", "product_id"],
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = Users::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = androidx.room.ForeignKey.CASCADE
        ),

        androidx.room.ForeignKey(
            entity = Product::class,
            parentColumns = ["product_id"],
            childColumns = ["product_id"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ]

)
data class Wishlist(
    @ColumnInfo(name = "user_id")
    val user_id: Long = 0,

    @ColumnInfo(name = "product_id")
    val product_id: Long = 0,

    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis()

)

