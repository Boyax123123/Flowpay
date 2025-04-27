package com.mine.flowpay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mine.flowpay.data.Product
import com.mine.flowpay.data.ProductCategory
import com.mine.flowpay.data.database.AppDatabase
import com.mine.flowpay.data.repository.ProductRepository
import com.mine.flowpay.app.FlowpayApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepository
    private val _allProducts = MutableLiveData<List<Product>>()
    val allProducts: LiveData<List<Product>> = _allProducts
    private val _allCategories = MutableLiveData<List<ProductCategory>>()
    val allCategories: LiveData<List<ProductCategory>> = _allCategories

    init {
        val database = (application as FlowpayApp).database
        repository = ProductRepository(database.productDao(), database.categoryDao())
        _allProducts.value = repository.getAllProducts()
        _allCategories.value = repository.getAllCategories()
    }

    // Product operations
    suspend fun getProductsByCategory(categoryId: Long): List<Product> {
        return withContext(Dispatchers.IO) {
            repository.getProductsByCategory(categoryId)
        }
    }

    fun getProductByName(productName: String): Product? {
        return repository.getProductByName(productName)
    }

    // Category operations
    fun getCategoryWithProducts(categoryId: Long) = repository.getCategoryWithProducts(categoryId)

    fun getCategoryById(categoryId: Long) = repository.getAllCategories().find { it.category_id == categoryId }
}
