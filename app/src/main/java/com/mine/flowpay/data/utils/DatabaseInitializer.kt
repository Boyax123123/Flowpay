package com.mine.flowpay.data.utils

import com.mine.flowpay.R
import com.mine.flowpay.data.Product
import com.mine.flowpay.data.ProductCategory
import com.mine.flowpay.data.Users
import com.mine.flowpay.data.dao.ProductCategoryDao
import com.mine.flowpay.data.dao.ProductDao
import com.mine.flowpay.data.dao.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseInitializer {
    fun initializeCategories(productCategoryDao: ProductCategoryDao) {
        CoroutineScope(Dispatchers.IO).launch {
            // Check if categories already exist
            if (productCategoryDao.getAllCategories().isEmpty()) {
                val categories = listOf(
                    ProductCategory(category_name = "League of Legends", image = "img_lol"),
                    ProductCategory(category_name = "Mobile Legends", image = "img_ml"),
                    ProductCategory(category_name = "Valorant", image = "img_valo"),
                    ProductCategory(category_name = "COD: Mobile", image = "img_cod"),
                    ProductCategory(category_name = "Genshin Impact", image = "img_genshin"),
                    ProductCategory(category_name = "Wildrift", image = "img_wildrift"),
                    ProductCategory(category_name = "Rivals", image = "img_rivals"),
                    ProductCategory(category_name = "Steam", image = "logo_steam2")
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
                                Product(category_id = category.category_id, productName = "575 RP", price = 199.00, image = R.drawable.img_lol),
                                Product(category_id = category.category_id, productName = "1380 RP", price = 449.00, image = R.drawable.img_lol),
                                Product(category_id = category.category_id, productName = "2800 RP", price = 899.00, image = R.drawable.img_lol),
                                Product(category_id = category.category_id, productName = "4500 RP", price = 1399.00, image = R.drawable.img_lol),
                                Product(category_id = category.category_id, productName = "6500 RP", price = 1999.00, image = R.drawable.img_lol),
                                Product(category_id = category.category_id, productName = "13500 RP", price = 3999.00, image = R.drawable.img_lol),
                                Product(category_id = category.category_id, productName = "15000 RP", price = 4999.00, image = R.drawable.img_lol),
                                Product(category_id = category.category_id, productName = "18000 RP", price = 5999.00, image = R.drawable.img_lol),
                                Product(category_id = category.category_id, productName = "22000 RP", price = 7999.00, image = R.drawable.img_lol),
                                Product(category_id = category.category_id, productName = "25000 RP", price = 9999.00, image = R.drawable.img_lol),
                                Product(category_id = category.category_id, productName = "30000 RP", price = 11999.00, image = R.drawable.img_lol),
                                Product(category_id = category.category_id, productName = "35000 RP", price = 13999.00, image = R.drawable.img_lol),
                                Product(category_id = category.category_id, productName = "40000 RP", price = 15999.00, image = R.drawable.img_lol),
                                Product(category_id = category.category_id, productName = "45000 RP", price = 17999.00, image = R.drawable.img_lol)
                            )
                            products.forEach { productDao.insertProduct(it) }
                        }
                        "Mobile Legends" -> {
                            val products = listOf(
                                Product(category_id = category.category_id, productName = "5 Diamonds", price = 5.00, image = R.drawable.img_ml),
                                Product(category_id = category.category_id, productName = "11 Diamonds", price = 9.50, image = R.drawable.img_ml),
                                Product(category_id = category.category_id, productName = "22 Diamonds", price = 19.00, image = R.drawable.img_ml),
                                Product(category_id = category.category_id, productName = "56 Diamonds", price = 47.50, image = R.drawable.img_ml),
                                Product(category_id = category.category_id, productName = "112 Diamonds", price = 95.00, image = R.drawable.img_ml),
                                Product(category_id = category.category_id, productName = "223 Diamonds", price = 190.00, image = R.drawable.img_ml),
                                Product(category_id = category.category_id, productName = "336 Diamonds", price = 285.00, image = R.drawable.img_ml),
                                Product(category_id = category.category_id, productName = "570 Diamonds", price = 475.00, image = R.drawable.img_ml),
                                Product(category_id = category.category_id, productName = "1163 Diamonds", price = 950.00, image = R.drawable.img_ml),
                                Product(category_id = category.category_id, productName = "2398 Diamonds", price = 1900.00, image = R.drawable.img_ml)
                            )
                            products.forEach { productDao.insertProduct(it) }
                        }
                        "Valorant" -> {
                            val products = listOf(
                                Product(category_id = category.category_id, productName = "475 VP", price = 199.00, image = R.drawable.img_valo),
                                Product(category_id = category.category_id, productName = "1000 VP", price = 399.00, image = R.drawable.img_valo),
                                Product(category_id = category.category_id, productName = "2050 VP", price = 799.00, image = R.drawable.img_valo),
                                Product(category_id = category.category_id, productName = "3650 VP", price = 1399.00, image = R.drawable.img_valo),
                                Product(category_id = category.category_id, productName = "5350 VP", price = 1999.00, image = R.drawable.img_valo),
                                Product(category_id = category.category_id, productName = "11000 VP", price = 2999.00, image = R.drawable.img_valo)
                            )
                            products.forEach { productDao.insertProduct(it) }
                        }
                        "COD: Mobile" -> {
                            val products = listOf(
                                Product(category_id = category.category_id, productName = "10 Garena Shells", price = 10.00, image = R.drawable.img_cod),
                                Product(category_id = category.category_id, productName = "20 Garena Shells", price = 20.00, image = R.drawable.img_cod),
                                Product(category_id = category.category_id, productName = "50 Garena Shells", price = 50.00, image = R.drawable.img_cod),
                                Product(category_id = category.category_id, productName = "100 Garena Shells", price = 100.00, image = R.drawable.img_cod),
                                Product(category_id = category.category_id, productName = "200 Garena Shells", price = 200.00, image = R.drawable.img_cod),
                                Product(category_id = category.category_id, productName = "300 Garena Shells", price = 300.00, image = R.drawable.img_cod),
                                Product(category_id = category.category_id, productName = "500 Garena Shells", price = 500.00, image = R.drawable.img_cod),
                                Product(category_id = category.category_id, productName = "1000 Garena Shells", price = 1000.00, image = R.drawable.img_cod),
                                Product(category_id = category.category_id, productName = "1 Rewards", price = 1.00, image = R.drawable.img_cod)
                            )
                            products.forEach { productDao.insertProduct(it) }
                        }
                        "Genshin Impact" -> {
                            val products = listOf(
                                Product(category_id = category.category_id, productName = "60 Genesis Crystals", price = 55.00, image = R.drawable.img_genshin),
                                Product(category_id = category.category_id, productName = "330 Genesis Crystals (300 + 30 Bonus)", price = 280.00, image = R.drawable.img_genshin),
                                Product(category_id = category.category_id, productName = "1090 Genesis Crystals (980 + 110 Bonus)", price = 830.00, image = R.drawable.img_genshin),
                                Product(category_id = category.category_id, productName = "2240 Genesis Crystals (1980 + 260 Bonus)", price = 1670.00, image = R.drawable.img_genshin),
                                Product(category_id = category.category_id, productName = "3880 Genesis Crystals (3280 + 600 Bonus)", price = 2800.00, image = R.drawable.img_genshin),
                                Product(category_id = category.category_id, productName = "8080 Genesis Crystals (6480 + 1600 Bonus)", price = 5500.00, image = R.drawable.img_genshin),
                                Product(category_id = category.category_id, productName = "Blessing of the Welkin Moon", price = 280.00, image = R.drawable.img_genshin)
                            )
                            products.forEach { productDao.insertProduct(it) }
                        }
                        "Wildrift" -> {
                            val products = listOf(
                                Product(category_id = category.category_id, productName = "425 Wild Cores", price = 200.00, image = R.drawable.img_wildrift),
                                Product(category_id = category.category_id, productName = "1000 Wild Cores", price = 449.00, image = R.drawable.img_wildrift),
                                Product(category_id = category.category_id, productName = "1850 Wild Cores", price = 819.00, image = R.drawable.img_wildrift),
                                Product(category_id = category.category_id, productName = "3275 Wild Cores", price = 1430.00, image = R.drawable.img_wildrift),
                                Product(category_id = category.category_id, productName = "4800 Wild Cores", price = 2050.00, image = R.drawable.img_wildrift),
                                Product(category_id = category.category_id, productName = "10000 Wild Cores", price = 4090.00, image = R.drawable.img_wildrift)
                            )
                            products.forEach { productDao.insertProduct(it) }
                        }
                        "Rivals" -> {
                            val products = listOf(
                                Product(category_id = category.category_id, productName = "100 Rival Coins", price = 99.00, image = R.drawable.img_rivals),
                                Product(category_id = category.category_id, productName = "250 Rival Coins", price = 229.00, image = R.drawable.img_rivals),
                                Product(category_id = category.category_id, productName = "500 Rival Coins", price = 449.00, image = R.drawable.img_rivals),
                                Product(category_id = category.category_id, productName = "1000 Rival Coins (+50 Bonus)", price = 899.00, image = R.drawable.img_rivals),
                                Product(category_id = category.category_id, productName = "2500 Rival Coins (+250 Bonus)", price = 1999.00, image = R.drawable.img_rivals),
                                Product(category_id = category.category_id, productName = "5000 Rival Coins (+750 Bonus)", price = 3999.00, image = R.drawable.img_rivals),
                                Product(category_id = category.category_id, productName = "10000 Rival Coins (+2000 Bonus)", price = 7499.00, image = R.drawable.img_rivals),
                                Product(category_id = category.category_id, productName = "Starter Pack (200 Coins + Skin)", price = 199.00, image = R.drawable.img_rivals),
                                Product(category_id = category.category_id, productName = "Elite Pack (1000 Coins + 2 Skins)", price = 999.00, image = R.drawable.img_rivals),
                                Product(category_id = category.category_id, productName = "Champion Bundle (5000 Coins + 5 Skins)", price = 4999.00, image = R.drawable.img_rivals)
                            )
                            products.forEach { productDao.insertProduct(it) }
                        }
                        "Steam" -> {
                            val products = listOf(
                                Product(category_id = category.category_id, productName = "Steam Wallet PHP 50", price = 55.62, image = R.drawable.logo_steam2),
                                Product(category_id = category.category_id, productName = "Steam Wallet PHP 100", price = 111.24, image = R.drawable.logo_steam2),
                                Product(category_id = category.category_id, productName = "Steam Wallet PHP 250", price = 272.95, image = R.drawable.logo_steam2),
                                Product(category_id = category.category_id, productName = "Steam Wallet PHP 500", price = 535.60, image = R.drawable.logo_steam2),
                                Product(category_id = category.category_id, productName = "Steam Wallet PHP 1000", price = 1030.00, image = R.drawable.logo_steam2),
                                Product(category_id = category.category_id, productName = "Steam Wallet PHP 2200", price = 2244.00, image = R.drawable.logo_steam2),
                                Product(category_id = category.category_id, productName = "Steam Wallet PHP 300", price = 350.00, image = R.drawable.logo_steam2),
                                Product(category_id = category.category_id, productName = "Steam Wallet PHP 750", price = 750.00, image = R.drawable.logo_steam2)
                            )
                            products.forEach { productDao.insertProduct(it) }
                        }
                    }
                }
            }
        }
    }
    fun initializeAutoAccount(userDao: UserDao) {
        CoroutineScope(Dispatchers.IO).launch {
            val email = "yobs@g.com"
            val username = "yobs"
            val password = "pass.123"
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser == null) {
                val user = Users(
                    email = email,
                    username = username,
                    password = password
                )
                userDao.insertUser(user)
            }
        }
    }
}

