package com.mine.flowpay.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ColumnInfo

@Entity(
    tableName = "mails",
    foreignKeys = [
        ForeignKey(
            entity = Users::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Transaction::class,
            parentColumns = ["transaction_id"],
            childColumns = ["transaction_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["transaction_id"])
    ]
)


data class Mail(
    @PrimaryKey(autoGenerate = true)
    val mail_id: Long = 0,

    @ColumnInfo(name = "user_id")
    val user_id: Long = 0,

    @ColumnInfo(name = "transaction_id")
    val transaction_id: Long? = null,

    var subject: String = "",
    var message: String = "",

    val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_read")
    var isRead: Boolean = false,
)

