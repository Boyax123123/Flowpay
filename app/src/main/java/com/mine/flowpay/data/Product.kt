package com.mine.flowpay.data
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mine.flowpay.R

@Entity(
    tableName = "products",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = ProductCategory::class,
            parentColumns = ["category_id"],
            childColumns = ["category_id"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ]

)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val product_id: Long = 0,

    @ColumnInfo(name = "category_id")
    val category_id: Long = 0,

    @ColumnInfo(name = "product_name")
    var productName: String = "",
    var price:  Double = 0.0,
    var image: Int? = R.drawable.img_notfound,
)
