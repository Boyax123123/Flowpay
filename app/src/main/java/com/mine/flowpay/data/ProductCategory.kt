package com.mine.flowpay.data
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mine.flowpay.R

@Entity(tableName = "product_categories")
data class ProductCategory(
    @PrimaryKey(autoGenerate = true)
    val category_id: Long = 0,
    var category_name: String = "",
    var image: String = "img_notfound",
)
