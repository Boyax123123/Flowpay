package com.mine.flowpay

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mine.flowpay.adapters.ProductAdapter
import com.mine.flowpay.app.FlowpayApp
import com.mine.flowpay.data.Product
import com.mine.flowpay.data.Transaction
import com.mine.flowpay.data.Wishlist
import com.mine.flowpay.data.Mail
import com.mine.flowpay.utils.DialogUtils
import com.mine.flowpay.utils.SortButtonsHandler
import com.mine.flowpay.viewmodel.ProductViewModel
import com.mine.flowpay.viewmodel.TransactionViewModel
import com.mine.flowpay.viewmodel.UserViewModel
import com.mine.flowpay.viewmodel.WishlistViewModel
import com.mine.flowpay.viewmodel.MailViewModel

class ProductsListActivity : AppCompatActivity() {
    companion object {
        private const val MAIL_LIST_REQUEST_CODE = 100
    }
    private var mailObserver: androidx.lifecycle.Observer<Int>? = null
    private lateinit var productViewModel: ProductViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var wishlistViewModel: WishlistViewModel
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var mailViewModel: MailViewModel
    private lateinit var sortButtonsHandler: SortButtonsHandler

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryTitleView: TextView
    private lateinit var confirmationPanel: CardView
    private lateinit var selectedProductView: TextView
    private lateinit var selectedPriceView: TextView
    private lateinit var buyButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var walletBalanceView: TextView
    private lateinit var errorMessageView: TextView
    private lateinit var walletIcon: ImageView
    private lateinit var mailIcon: ImageView
    private lateinit var categoryBackgroundView: ImageView

    private var currentUserId: Long = -1
    private var selectedProduct: Product? = null
    private val wishlistedProducts = mutableSetOf<Long>()
    private var categoryProducts = listOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products_list)

        // Initialize ViewModels
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        wishlistViewModel = ViewModelProvider(this).get(WishlistViewModel::class.java)
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        mailViewModel = ViewModelProvider(this).get(MailViewModel::class.java)

        // Get user ID and category details from intent
        currentUserId = intent.getLongExtra("USER_ID", -1)
        val categoryId = intent.getLongExtra("CATEGORY_ID", -1)
        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "Products"

        // Initialize views
        categoryTitleView = findViewById(R.id.tv_category_title)
        recyclerView = findViewById(R.id.rv_products)
        confirmationPanel = findViewById(R.id.confirmation_panel)
        selectedProductView = findViewById(R.id.tv_selected_product)
        selectedPriceView = findViewById(R.id.tv_selected_price)
        buyButton = findViewById(R.id.btn_buy)
        backButton = findViewById(R.id.btn_back)
        walletBalanceView = findViewById(R.id.tv_wallet_balance)
        errorMessageView = findViewById(R.id.tv_error_message)
        walletIcon = findViewById(R.id.wallet_icon)
        mailIcon = findViewById(R.id.mail_icon)
        categoryBackgroundView = findViewById(R.id.category_background)

        // Set category title
        categoryTitleView.text = categoryName
        
        // Set category background image
        setCategoryBackgroundImage(categoryName)

        // Initially hide confirmation panel and error message
        confirmationPanel.visibility = View.GONE
        errorMessageView.visibility = View.GONE

        // Set initial wallet balance
        val user = (application as FlowpayApp).loggedInuser
        if (user != null) {
            // Update balance display with commas and two decimal points
            val formattedBalance = String.format("%,.2f", user.walletBalance)
            walletBalanceView.text = "₱$formattedBalance"
        }

        // Set up click listeners
        walletIcon.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        mailIcon.setOnClickListener {
            startActivity(Intent(this, MailsActivity::class.java))
        }

        // Set up mail observer
        mailObserver = androidx.lifecycle.Observer<Int> { count ->
            updateMailIcon(count, mailViewModel.hasPurchaseMail.value ?: false)
        }

        // Initial mail check
        mailViewModel.unreadMailCount.observe(this, mailObserver!!)

        // Observe purchase mail status
        mailViewModel.hasPurchaseMail.observe(this) { hasPurchaseMail ->
            updateMailIcon(mailViewModel.unreadMailCount.value ?: 0, hasPurchaseMail)
        }

        // Set up RecyclerView with 2 columns
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Set up the sort buttons
        setupSortButtons()

        // Load products for the selected category
        CoroutineScope(Dispatchers.IO).launch {
            val products = productViewModel.getProductsByCategory(categoryId)
            categoryProducts = products
            withContext(Dispatchers.Main) {
                // Update sort buttons with the loaded products
                if (::sortButtonsHandler.isInitialized) {
                    sortButtonsHandler.updateProducts(products)
                }
                updateAdapter(products)
            }
        }

        // Load wishlist items for current user
        wishlistViewModel.getUserWishlist(currentUserId)
        wishlistViewModel.userWishlist.observe(this, Observer { wishlist ->
            wishlistedProducts.clear()
            wishlist.forEach { item ->
                wishlistedProducts.add(item.product_id)
            }
            // Get products for the current category and update adapter
            CoroutineScope(Dispatchers.IO).launch {
                val products = productViewModel.getProductsByCategory(categoryId)
                withContext(Dispatchers.Main) {
                    updateAdapter(products)
                }
            }
        })

        // Set up buy button
        buyButton.setOnClickListener {
            selectedProduct?.let { product ->
                val user = (application as FlowpayApp).loggedInuser
                if (user != null && user.walletBalance >= product.price) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val database = (application as FlowpayApp).database

                            // Create transaction
                            val transaction = Transaction(
                                userId = currentUserId,
                                type = product.productName,
                                amount = product.price,
                                timestamp = System.currentTimeMillis()
                            )
                            // Insert transaction
                            withContext(Dispatchers.IO) {
                                database.transactionDao().createTransaction(transaction)
                            }

                            // Get transaction ID
                            val transactionId = withContext(Dispatchers.IO) {
                                database.transactionDao().getLastTransactionForUser(currentUserId).transaction_id
                            }

                            // Update user balance
                            user.walletBalance -= product.price
                            withContext(Dispatchers.IO) {
                                userViewModel.updateUser(user)
                            }

                            // Get category name
                            val category = withContext(Dispatchers.IO) {
                                productViewModel.getCategoryById(product.category_id)
                            }
                            val categoryName = category?.category_name ?: "Unknown"

                            // Generate random code
                            val code = generateRandomCode()

                            // Create mail
                            val mail = Mail(
                                user_id = currentUserId,
                                transaction_id = transactionId,
                                subject = "Purchase of ${categoryName}'s ${product.productName}",
                                message = """Hi there,
Thanks for your recent purchase of ${product.productName} from ${categoryName}. We hope you enjoy your experience!

Heres the code for your game:
Code: ${code}

Use this code to top up your account!

Best regards,
— The FlowPay Team""",
                                timestamp = System.currentTimeMillis(),
                                isRead = false
                            )
                            withContext(Dispatchers.IO) {
                                mailViewModel.createMail(mail)
                            }

                            runOnUiThread {
                                // Update balance display with commas and two decimal points
                                val formattedBalance = String.format("%,.2f", user.walletBalance)
                                walletBalanceView.text = "₱$formattedBalance"
                                confirmationPanel.visibility = View.GONE
                                Toast.makeText(this@ProductsListActivity, "Purchase successful!", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(this@ProductsListActivity, "Error processing purchase", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set up back button
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Set up wallet icon click listener
        walletIcon.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Set up mail icon click listener
        mailIcon.setOnClickListener {
            startActivityForResult(Intent(this, MailsActivity::class.java), MAIL_LIST_REQUEST_CODE)
        }
    }

    private fun updateAdapter(products: List<Product>) {
        recyclerView.adapter = ProductAdapter(
            products,
            onProductSelected = { product -> onProductSelected(product) },
            onWishlistClicked = { product -> toggleWishlist(product) },
            isInWishlist = { product -> wishlistedProducts.contains(product.product_id) }
        )
    }

    private fun onProductSelected(product: Product) {
        if (selectedProduct == product) {
            // Deselect: panel should disappear immediately
            selectedProduct = null
            hideConfirmationPanel()
            (recyclerView.adapter as? ProductAdapter)?.setSelectedProduct(null)
        } else {
            // Select: show panel and update info
            selectedProduct = product
            showConfirmationPanel(product)
            (recyclerView.adapter as? ProductAdapter)?.setSelectedProduct(product)
        }
        updateConfirmationPanelVisibility()
    }

    private fun updateBuyButtonState(userBalance: Double, productPrice: Double) {
        val canBuy = userBalance >= productPrice
        buyButton.isEnabled = canBuy
        errorMessageView.visibility = if (canBuy) View.GONE else View.VISIBLE
    }

    private fun generateRandomCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = java.util.Random()
        val firstPart = (1..3)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
        val secondPart = (1..3)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
        return "$firstPart-$secondPart"
    }

    private fun toggleWishlist(product: Product) {
        if (wishlistedProducts.contains(product.product_id)) {
            // Show custom confirmation dialog for removing from wishlist
            DialogUtils.showCustomConfirmationDialog(
                context = this,
                title = "Remove from wishlist",
                message = "are you sure you want to remove this item?",
                onConfirm = {
                    // Remove from wishlist
                    wishlistedProducts.remove(product.product_id)
                    wishlistViewModel.removeFromWishlist(currentUserId, product.product_id)
                    Toast.makeText(this, "Removed from wishlist", Toast.LENGTH_SHORT).show()
                    // Update the adapter to reflect changes
                    (recyclerView.adapter as? ProductAdapter)?.updateWishlistState(product)
                }
            )
        } else {
            // Add to wishlist
            wishlistedProducts.add(product.product_id)
            val wishlist = Wishlist(
                user_id = currentUserId,
                product_id = product.product_id
            )
            wishlistViewModel.addToWishlist(wishlist)
            Toast.makeText(this, "Added to wishlist", Toast.LENGTH_SHORT).show()

            // Update the adapter to reflect changes
            (recyclerView.adapter as? ProductAdapter)?.updateWishlistState(product)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MAIL_LIST_REQUEST_CODE && resultCode == RESULT_OK) {
            // Force refresh mail count
            mailViewModel.updateUnreadMailCount()
        }
    }

    private fun updateMailIcon(unreadCount: Int, hasPurchaseMail: Boolean) {
        if (unreadCount > 0) {
            // If there are unread mails, use the unread icon
            mailIcon.setImageResource(R.drawable.ic_mail_unread)
        } else {
            // If there are no unread mails, use the regular icon
            mailIcon.setImageResource(R.drawable.ic_mail)
        }
    }

    override fun onResume() {
        super.onResume()
        // Remove and re-add observer to force refresh
        mailObserver?.let { observer ->
            mailViewModel.unreadMailCount.removeObserver(observer)
            mailViewModel.unreadMailCount.observe(this, observer)
        }

        // Force refresh purchase mail status
        mailViewModel.hasPurchaseMail.removeObservers(this)
        mailViewModel.hasPurchaseMail.observe(this) { hasPurchaseMail ->
            updateMailIcon(mailViewModel.unreadMailCount.value ?: 0, hasPurchaseMail)
        }
    }

    private fun setupSortButtons() {
        // Initialize sort buttons if they exist in the layout
        val btnSortPopular = findViewById<Button>(R.id.btn_sort_popular)
        val btnSortHighLow = findViewById<Button>(R.id.btn_sort_high_low)
        val btnSortLowHigh = findViewById<Button>(R.id.btn_sort_low_high)
        val btnSortAZ = findViewById<Button>(R.id.btn_sort_a_z)
        
        // Only proceed if all buttons exist in the layout
        if (btnSortPopular != null && btnSortHighLow != null && btnSortLowHigh != null && btnSortAZ != null) {
            // Get transaction repository
            val transactionRepository = transactionViewModel.getTransactionRepository()
            
            // Create the sort buttons handler
            sortButtonsHandler = SortButtonsHandler(
                btnSortPopular,
                btnSortHighLow,
                btnSortLowHigh,
                btnSortAZ,
                transactionRepository
            ) { sortedProducts ->
                // Update the RecyclerView with sorted products
                updateAdapter(sortedProducts)
            }
        }
    }

    /**
     * Set the background image for the category
     * The image resource follows the pattern: img_[category]_bg
     * Category name is converted to lowercase and spaces are replaced with underscores
     */
    private fun setCategoryBackgroundImage(categoryName: String) {
        // Format the category name to match resource naming conventions
        val formattedCategoryName = categoryName.lowercase().replace(" ", "_")
        
        // Map of common game names to their abbreviations as they appear in file names
        val abbreviationMap = mapOf(
            "mobile_legends" to "ml",
            "league_of_legends" to "lol",
            "valorant" to "valo",
            "call_of_duty" to "cod",
            "wild_rift" to "wildrift",
            "pubg_mobile" to "pubg",
            "genshin_impact" to "genshin",
            "zenless_zone_zero" to "zenless",
            "honkai_star_rail" to "star_rail"
        )
        
        // Check if we have an abbreviation for this category
        val categoryCode = abbreviationMap[formattedCategoryName] ?: formattedCategoryName
        
        // Create the drawable resource name - using the existing pattern observed in drawable folder
        val backgroundResourceName = "img_${categoryCode}_bg"
        
        // Get the resource ID
        val resourceId = resources.getIdentifier(
            backgroundResourceName,
            "drawable",
            packageName
        )
        
        // Set the background image if resource exists, otherwise use a default background
        if (resourceId != 0) {
            categoryBackgroundView.setImageResource(resourceId)
        } else {
            // Try with the original formatted name as a fallback
            if (categoryCode != formattedCategoryName) {
                val fallbackResourceName = "img_${formattedCategoryName}_bg"
                val fallbackResourceId = resources.getIdentifier(
                    fallbackResourceName,
                    "drawable",
                    packageName
                )
                
                if (fallbackResourceId != 0) {
                    categoryBackgroundView.setImageResource(fallbackResourceId)
                    return
                }
            }
            
            // Use a default background image if the specific one doesn't exist
            // We'll use img_notfound.png as our fallback since it already exists
            categoryBackgroundView.setImageResource(R.drawable.img_notfound)
            
            // Log a message about the missing resource
            android.util.Log.d("ProductsListActivity", "Background image not found: $backgroundResourceName")
        }
    }

    private fun showConfirmationPanel(product: Product) {
        confirmationPanel.visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_selected_product).text = product.productName
        findViewById<TextView>(R.id.tv_selected_price).text = "₱${product.price}"
        findViewById<TextView>(R.id.tv_error_message).visibility = View.GONE
        val user = (application as FlowpayApp).loggedInuser
        val buyBtn = findViewById<Button>(R.id.btn_buy)
        if (user != null && user.walletBalance >= product.price) {
            buyBtn.isEnabled = true
            buyBtn.alpha = 1.0f
        } else {
            buyBtn.isEnabled = false
            buyBtn.alpha = 0.5f
            findViewById<TextView>(R.id.tv_error_message).visibility = View.VISIBLE
        }
        buyBtn.setOnClickListener {
            // Place purchase logic here
            purchaseSelectedProduct()
        }
    }

    private fun hideConfirmationPanel() {
        confirmationPanel.visibility = View.GONE
    }

    private fun updateConfirmationPanelVisibility() {
        if (selectedProduct == null) {
            hideConfirmationPanel()
        } else {
            showConfirmationPanel(selectedProduct!!)
        }
    }

    private fun purchaseSelectedProduct() {
        selectedProduct?.let { product ->
            val user = (application as FlowpayApp).loggedInuser
            if (user != null && user.walletBalance >= product.price) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val database = (application as FlowpayApp).database

                        // Create transaction
                        val transaction = Transaction(
                            userId = currentUserId,
                            type = product.productName,
                            amount = product.price,
                            timestamp = System.currentTimeMillis()
                        )
                        // Insert transaction
                        withContext(Dispatchers.IO) {
                            database.transactionDao().createTransaction(transaction)
                        }

                        // Get transaction ID
                        val transactionId = withContext(Dispatchers.IO) {
                            database.transactionDao().getLastTransactionForUser(currentUserId).transaction_id
                        }

                        // Update user balance
                        user.walletBalance -= product.price
                        withContext(Dispatchers.IO) {
                            userViewModel.updateUser(user)
                        }

                        // Get category name
                        val category = withContext(Dispatchers.IO) {
                            productViewModel.getCategoryById(product.category_id)
                        }
                        val categoryName = category?.category_name ?: "Unknown"

                        // Generate random code
                        val code = generateRandomCode()

                        // Create mail
                        val mail = Mail(
                            user_id = currentUserId,
                            transaction_id = transactionId,
                            subject = "Purchase of ${categoryName}'s ${product.productName}",
                            message = """Hi there,
Thanks for your recent purchase of ${product.productName} from ${categoryName}. We hope you enjoy your experience!

Heres the code for your game:
Code: ${code}

Use this code to top up your account!

Best regards,
— The FlowPay Team""",
                            timestamp = System.currentTimeMillis(),
                            isRead = false
                        )
                        withContext(Dispatchers.IO) {
                            mailViewModel.createMail(mail)
                        }

                        runOnUiThread {
                            // Update balance display with commas and two decimal points
                            val formattedBalance = String.format("%,.2f", user.walletBalance)
                            walletBalanceView.text = "₱$formattedBalance"
                            confirmationPanel.visibility = View.GONE
                            Toast.makeText(this@ProductsListActivity, "Purchase successful!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@ProductsListActivity, "Error processing purchase", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
