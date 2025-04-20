package com.mine.flowpay.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mine.flowpay.data.ProductCategory
import com.mine.flowpay.data.Product

data class CategoryWithProducts(
    @Embedded
    val category: ProductCategory,
    
    @Relation(
        parentColumn = "category_id",
        entityColumn = "category_id"
    )
    val products: List<Product>
)
