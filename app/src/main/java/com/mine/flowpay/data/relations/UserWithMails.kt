package com.mine.flowpay.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mine.flowpay.data.Users
import com.mine.flowpay.data.Mail

//get a user with all their mails
data class UserWithMails(
    @Embedded
    val user: Users,

    @Relation(
        parentColumn = "user_id",
        entityColumn = "user_id"
    )
    val mails: List<Mail>
)