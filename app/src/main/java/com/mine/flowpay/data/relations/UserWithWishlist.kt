package com.mine.flowpay.data.relations
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.mine.flowpay.data.Product
import com.mine.flowpay.data.Users
import com.mine.flowpay.data.Wishlist

// User's wishlist items
data class UserWithWishlist(
    @Embedded
    val user: Users,

    @Relation(
        parentColumn = "user_id",
        entityColumn = "product_id",
        associateBy = Junction(Wishlist::class)
    )
    val wishlistItems: List<Product>
)

