package com.mine.flowpay.data.repository

import com.mine.flowpay.data.Wishlist
import com.mine.flowpay.data.dao.WishlistDao

class WishlistRepository(private val wishlistDao: WishlistDao) {
    // Insert operations
    suspend fun insertToWishlist(wishlist: Wishlist) = wishlistDao.insertToWishlist(wishlist)
    
    // Delete operations
    suspend fun removeFromWishlist(wishlist: Wishlist) = wishlistDao.removeFromWishlist(wishlist)
    
    // Query operations
    fun getUserWishlist(userId: Long) = wishlistDao.getUserWishlist(userId)
    fun getProductWishlistStatus(productId: Long) = wishlistDao.getProductWishlistStatus(productId)
    
    // Relation queries
    fun getUserWithWishlist(userId: Long) = wishlistDao.getUserWithWishlist(userId)
    fun getProductWithWishlistStatus(productId: Long) = wishlistDao.getProductWithWishlistStatus(productId)
}