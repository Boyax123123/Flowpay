package com.mine.flowpay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mine.flowpay.data.Wishlist
import com.mine.flowpay.data.database.AppDatabase
import com.mine.flowpay.data.repository.WishlistRepository
import com.mine.flowpay.data.relations.UserWithWishlist
import com.mine.flowpay.data.relations.ProductWithWishlistStatus
import com.mine.flowpay.app.FlowpayApp
import kotlinx.coroutines.launch

class WishlistViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WishlistRepository
    private val _userWishlist = MutableLiveData<List<Wishlist>>()
    val userWishlist: LiveData<List<Wishlist>> = _userWishlist
    private val _productWishlist = MutableLiveData<List<Wishlist>>()
    val productWishlist: LiveData<List<Wishlist>> = _productWishlist

    init {
        val database = (application as FlowpayApp).database
        repository = WishlistRepository(database.wishlistDao())
    }

    // Wishlist operations
    fun getUserWishlist(userId: Long) {
        _userWishlist.value = repository.getUserWishlist(userId)
    }

    fun getProductWishlistStatus(productId: Long) {
        _productWishlist.value = repository.getProductWishlistStatus(productId)
    }

    // Check if a product is in user's wishlist
    fun isInWishlist(userId: Long, productId: Long): Boolean {
        return repository.getUserWishlist(userId).any { it.product_id == productId }
    }

    // Add product to wishlist
    fun addToWishlist(wishlist: Wishlist) = viewModelScope.launch {
        repository.insertToWishlist(wishlist)
    }

    // Remove product from wishlist
    fun removeFromWishlist(userId: Long, productId: Long) = viewModelScope.launch {
        val wishlist = repository.getUserWishlist(userId).find { it.product_id == productId }
        wishlist?.let {
            repository.removeFromWishlist(it)
        }
    }

    // Relation operations
    fun getUserWithWishlist(userId: Long): UserWithWishlist = 
        repository.getUserWithWishlist(userId)

    fun getProductWithWishlistStatus(productId: Long): ProductWithWishlistStatus = 
        repository.getProductWithWishlistStatus(productId)
}
