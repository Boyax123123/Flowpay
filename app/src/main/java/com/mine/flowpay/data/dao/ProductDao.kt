package com.mine.flowpay.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import com.mine.flowpay.data.relations.CategoryWithProducts
import com.mine.flowpay.data.Product

@Dao
interface ProductDao {
    // Create
    @Insert
    suspend fun insertProduct(product: Product)

    // Read
    @Query("SELECT * FROM products")
    fun getAllProducts(): List<Product>

    // Get product by ID
    @Query("SELECT * FROM products WHERE product_id = :productId")
    fun getProductById(productId: Long): Product

    // Get product by name
    @Query("SELECT * FROM products WHERE product_name = :productName LIMIT 1")
    fun getProductByName(productName: String): Product?

    //Update
    @Update
    suspend fun updateProduct(product: Product)

    //Delete
    @Delete
    suspend fun deleteProduct(product: Product)

    // Get all products by category
    @Query("SELECT * FROM products WHERE category_id = :categoryId")
    fun getProductsByCategory(categoryId: Long): List<Product>
}
