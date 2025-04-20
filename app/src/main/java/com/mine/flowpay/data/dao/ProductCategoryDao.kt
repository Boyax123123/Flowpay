package com.mine.flowpay.data.dao

import androidx.room.*
import com.mine.flowpay.data.ProductCategory
import com.mine.flowpay.data.relations.CategoryWithProducts

@Dao
interface ProductCategoryDao {
    // Create
    @Insert
    suspend fun insertCategory(category: ProductCategory)

    // Read
    @Query("SELECT * FROM product_categories")
    fun getAllCategories(): List<ProductCategory>

    @Query("SELECT * FROM product_categories WHERE category_id = :categoryId")
    fun getCategoryById(categoryId: Long): ProductCategory

    // Update
    @Update
    suspend fun updateCategory(category: ProductCategory)

    // Delete
    @Delete
    suspend fun deleteCategory(category: ProductCategory)

    @Query("DELETE FROM product_categories WHERE category_id = :categoryId")
    suspend fun deleteCategoryById(categoryId: Long)

    // Relations
    @Transaction
    @Query("SELECT * FROM product_categories WHERE category_id = :categoryId")
    fun getCategoryWithProducts(categoryId: Long): CategoryWithProducts

    @Transaction
    @Query("SELECT * FROM product_categories")
    fun getAllCategoriesWithProducts(): List<CategoryWithProducts>
} 