package com.mine.flowpay.data.repository

import com.mine.flowpay.data.ProductCategory
import com.mine.flowpay.data.dao.ProductCategoryDao
import com.mine.flowpay.data.relations.CategoryWithProducts

class ProductCategoryRepository(private val productCategoryDao: ProductCategoryDao) {
    suspend fun insertCategory(category: ProductCategory) = productCategoryDao.insertCategory(category)
    fun getAllCategories() = productCategoryDao.getAllCategories()
    fun getCategoryById(categoryId: Long) = productCategoryDao.getCategoryById(categoryId)
    suspend fun updateCategory(category: ProductCategory) = productCategoryDao.updateCategory(category)
    suspend fun deleteCategory(category: ProductCategory) = productCategoryDao.deleteCategory(category)
    suspend fun deleteCategoryById(categoryId: Long) = productCategoryDao.deleteCategoryById(categoryId)
    fun getCategoryWithProducts(categoryId: Long) = productCategoryDao.getCategoryWithProducts(categoryId)
    fun getAllCategoriesWithProducts() = productCategoryDao.getAllCategoriesWithProducts()
} 