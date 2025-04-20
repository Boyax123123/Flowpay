package com.mine.flowpay.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.mine.flowpay.data.Wishlist
import com.mine.flowpay.data.relations.ProductWithWishlistStatus
import com.mine.flowpay.data.relations.UserWithWishlist

@Dao
interface WishlistDao {

    // Insert a new wishlist item
    @Insert
    suspend fun insertToWishlist(wishlist: Wishlist)

    //delete a wishlist item
    @Delete
    suspend fun removeFromWishlist(wishlist: Wishlist)

    // Get all wishlist items for a user
    @Query("SELECT * FROM wishlists WHERE user_id = :userId")
    fun getUserWishlist(userId: Long): List<Wishlist>

    // Get all users who have wishlisted a specific product
    @Query("SELECT * FROM wishlists WHERE product_id = :productId")
    fun getProductWishlistStatus(productId: Long): List<Wishlist>

    // Get all products in a user's wishlist
    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :userId")
    fun getUserWithWishlist(userId: Long): UserWithWishlist

    @Transaction
    @Query("SELECT * FROM products WHERE product_id = :productId")
    fun getProductWithWishlistStatus(productId: Long): ProductWithWishlistStatus
}