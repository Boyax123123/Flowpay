package com.mine.flowpay.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mine.flowpay.data.Mail
import com.mine.flowpay.data.*

//to get a transaction with its related mails)

data class MailWithTransaction(
    @Embedded
    val mail: Mail,

    @Relation(
        parentColumn = "transaction_id",
        entityColumn = "transaction_id"
    )
    val transaction: Transaction?
)