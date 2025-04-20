package com.mine.flowpay.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mine.flowpay.data.Transaction
import com.mine.flowpay.data.Mail

data class TransactionWithMails(
    @Embedded
    val transaction: Transaction,
    
    @Relation(
        parentColumn = "transaction_id",
        entityColumn = "transaction_id"
    )
    val mails: List<Mail>
) 