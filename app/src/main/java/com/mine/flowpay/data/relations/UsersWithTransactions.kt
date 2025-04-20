package com.mine.flowpay.data.relations

import com.mine.flowpay.data.*
import androidx.room.Embedded
import androidx.room.Relation


data class UsersWithTransactions(
    @Embedded
    val user: Users,

    @Relation(
        parentColumn = "id",
        entityColumn = "user_id"
    )

    val transactions: List<Transaction>
)
