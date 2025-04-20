package com.mine.flowpay.data.utils

import com.mine.flowpay.R
import com.mine.flowpay.data.Product
import com.mine.flowpay.data.ProductCategory
import com.mine.flowpay.data.dao.ProductCategoryDao
import com.mine.flowpay.data.dao.ProductDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseInitializer {
    fun initializeCategories(productCategoryDao: ProductCategoryDao) {
        CoroutineScope(Dispatchers.IO).launch {
            // Check if categories already exist
            if (productCategoryDao.getAllCategories().isEmpty()) {
                val categories = listOf(
                    ProductCategory(category_name = "League of Legends", image = R.drawable.img_lol),
                    ProductCategory(category_name = "Mobile Legends", image = R.drawable.img_ml),
                    ProductCategory(category_name = "Valorant", image = R.drawable.img_valo),
                    ProductCategory(category_name = "COD: Mobile", image = R.drawable.img_cod),
                    ProductCategory(category_name = "Genshin Impact", image = R.drawable.img_genshin),
                    ProductCategory(category_name = "Wildrift", image = R.drawable.img_wildrift),
                    ProductCategory(category_name = "Rivals", image = R.drawable.img_rivals),
                    ProductCategory(category_name = "Steam", image = R.drawable.logo_steam2)
                )

                categories.forEach {
                    productCategoryDao.insertCategory(it)
                }
            }
        }
    }

    fun initializeProducts(productDao: ProductDao, productCategoryDao: ProductCategoryDao) {
        CoroutineScope(Dispatchers.IO).launch {
            // Check if products already exist
            if (productDao.getAllProducts().isEmpty()) {
                // Get all categories
                val categories = productCategoryDao.getAllCategories()

                // Create products for each category
                categories.forEach { category ->
                    when (category.category_name) {
                        "League of Legends" -> {
                            val products = listOf(
                                Product(category_id = category.category_id, productName = "500 RP", price = 250.0, image = R.drawable.img_lol),
                                Product(category_id = category.category_id, productName = "1000 RP", price = 500.0, image = R.drawable.img_lol),
                                Product(category_id = category.category_id, productName = "2000 RP", price = 1000.0, image = R.drawable.img_lol)
                            )
                            products.forEach { productDao.insertProduct(it) }
                        }
                        "Mobile Legends" -> {
                            val products = listOf(
                                Product(category_id = category.category_id, productName = "100 Diamonds", price = 100.0, image = R.drawable.img_ml),
                                Product(category_id = category.category_id, productName = "300 Diamonds", price = 300.0, image = R.drawable.img_ml),
                                Product(category_id = category.category_id, productName = "500 Diamonds", price = 500.0, image = R.drawable.img_ml)
                            )
                            products.forEach { productDao.insertProduct(it) }
                        }
                        "Valorant" -> {
                            val products = listOf(
                                Product(category_id = category.category_id, productName = "500 VP", price = 250.0, image = R.drawable.img_valo),
                                Product(category_id = category.category_id, productName = "1000 VP", price = 500.0, image = R.drawable.img_valo),
                                Product(category_id = category.category_id, productName = "2000 VP", price = 1000.0, image = R.drawable.img_valo)
                            )
                            products.forEach { productDao.insertProduct(it) }
                        }
                        "COD: Mobile" -> {
                            val products = listOf(
                                Product(category_id = category.category_id, productName = "100 CP", price = 100.0, image = R.drawable.img_cod),
                                Product(category_id = category.category_id, productName = "300 CP", price = 300.0, image = R.drawable.img_cod),
                                Product(category_id = category.category_id, productName = "500 CP", price = 500.0, image = R.drawable.img_cod)
                            )
                            products.forEach { productDao.insertProduct(it) }
                        }
                        "Genshin Impact" -> {
                            val products = listOf(
                                Product(category_id = category.category_id, productName = "300 Genesis Crystals", price = 150.0, image = R.drawable.img_genshin),
                                Product(category_id = category.category_id, productName = "980 Genesis Crystals", price = 490.0, image = R.drawable.img_genshin),
                                Product(category_id = category.category_id, productName = "1980 Genesis Crystals", price = 990.0, image = R.drawable.img_genshin)
                            )
                            products.forEach { productDao.insertProduct(it) }
                        }
                        "Wildrift" -> {
                            val products = listOf(
                                Product(category_id = category.category_id, productName = "500 Wild Cores", price = 250.0, image = R.drawable.img_wildrift),
                                Product(category_id = category.category_id, productName = "1000 Wild Cores", price = 500.0, image = R.drawable.img_wildrift),
                                Product(category_id = category.category_id, productName = "2000 Wild Cores", price = 1000.0, image = R.drawable.img_wildrift)
                            )
                            products.forEach { productDao.insertProduct(it) }
                        }
                        "Rivals" -> {
                            val products = listOf(
                                Product(category_id = category.category_id, productName = "100 Rivals Coins", price = 100.0, image = R.drawable.img_rivals),
                                Product(category_id = category.category_id, productName = "300 Rivals Coins", price = 300.0, image = R.drawable.img_rivals),
                                Product(category_id = category.category_id, productName = "500 Rivals Coins", price = 500.0, image = R.drawable.img_rivals)
                            )
                            products.forEach { productDao.insertProduct(it) }
                        }
                        "Steam" -> {
                            val products = listOf(
                                Product(category_id = category.category_id, productName = "Steam Wallet ₱500", price = 500.0, image = R.drawable.logo_steam2),
                                Product(category_id = category.category_id, productName = "Steam Wallet ₱1000", price = 1000.0, image = R.drawable.logo_steam2),
                                Product(category_id = category.category_id, productName = "Steam Wallet ₱2000", price = 2000.0, image = R.drawable.logo_steam2)
                            )
                            products.forEach { productDao.insertProduct(it) }
                        }
                    }
                }
            }
        }
    }
} 
