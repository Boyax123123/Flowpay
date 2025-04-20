package com.mine.flowpay.data.repository

import com.mine.flowpay.data.Product
import com.mine.flowpay.data.ProductCategory
import com.mine.flowpay.data.dao.ProductDao
import com.mine.flowpay.data.dao.ProductCategoryDao

class ProductRepository(
    private val productDao: ProductDao,
    private val categoryDao: ProductCategoryDao
) {
    // Product operations
    suspend fun insertProduct(product: Product) = productDao.insertProduct(product)
    fun getAllProducts() = productDao.getAllProducts()
    fun getProductsByCategory(categoryId: Long) = productDao.getProductsByCategory(categoryId)
    fun getProductByName(productName: String) = productDao.getProductByName(productName)

    // Category operations
    suspend fun insertCategory(category: ProductCategory) = categoryDao.insertCategory(category)
    fun getAllCategories() = categoryDao.getAllCategories()
    fun getCategoryWithProducts(categoryId: Long) = categoryDao.getCategoryWithProducts(categoryId)
} 
