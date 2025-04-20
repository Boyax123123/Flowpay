package com.mine.flowpay.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.Junction
import com.mine.flowpay.data.Product
import com.mine.flowpay.data.Users
import com.mine.flowpay.data.Wishlist

data class ProductWithWishlistStatus(
    @Embedded
    val product: Product,
    
    @Relation(
        parentColumn = "product_id",
        entityColumn = "user_id",
        associateBy = Junction(Wishlist::class)
    )
    val usersWhoWishlisted: List<Users>
) 