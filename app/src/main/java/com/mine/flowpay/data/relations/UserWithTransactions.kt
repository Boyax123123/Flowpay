package com.mine.flowpay.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mine.flowpay.data.Users
import com.mine.flowpay.data.Transaction

data class UserWithTransactions(
    @Embedded
    val user: Users,
    
    @Relation(
        parentColumn = "user_id",
        entityColumn = "user_id"
    )
    val transactions: List<Transaction>
) 