package com.mine.flowpay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mine.flowpay.data.ProductCategory
import com.mine.flowpay.data.database.AppDatabase
import com.mine.flowpay.data.repository.ProductCategoryRepository
import com.mine.flowpay.app.FlowpayApp
import kotlinx.coroutines.launch

class ProductCategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductCategoryRepository
    val allCategories: List<ProductCategory>

    init {
        val database = (application as FlowpayApp).database
        repository = ProductCategoryRepository(database.categoryDao())
        allCategories = repository.getAllCategories()
    }

    fun insertCategory(category: ProductCategory) = viewModelScope.launch {
        repository.insertCategory(category)
    }

    fun updateCategory(category: ProductCategory) = viewModelScope.launch {
        repository.updateCategory(category)
    }

    fun deleteCategory(category: ProductCategory) = viewModelScope.launch {
        repository.deleteCategory(category)
    }

    fun getCategoryById(categoryId: Long) = repository.getCategoryById(categoryId)

    fun getCategoryWithProducts(categoryId: Long) = repository.getCategoryWithProducts(categoryId)

    fun getAllCategoriesWithProducts() = repository.getAllCategoriesWithProducts()
} 