package com.mine.flowpay

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mine.flowpay.adapters.ProductAdapter
import com.mine.flowpay.app.FlowpayApp
import com.mine.flowpay.data.Mail
import com.mine.flowpay.data.Product
import com.mine.flowpay.data.ProductCategory
import com.mine.flowpay.data.Transaction
import com.mine.flowpay.data.Wishlist
import com.mine.flowpay.utils.DialogUtils
import com.mine.flowpay.viewmodel.ProductCategoryViewModel
import com.mine.flowpay.viewmodel.ProductViewModel
import com.mine.flowpay.viewmodel.TransactionViewModel
import com.mine.flowpay.viewmodel.UserViewModel
import com.mine.flowpay.viewmodel.WishlistViewModel
import com.mine.flowpay.viewmodel.MailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val MAIL_LIST_REQUEST_CODE = 100
    }
    private var mailObserver: androidx.lifecycle.Observer<Int>? = null
    private lateinit var productViewModel: ProductViewModel
    private lateinit var wishlistViewModel: WishlistViewModel
    private lateinit var categoryViewModel: ProductCategoryViewModel
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var mailViewModel: MailViewModel

    private lateinit var searchInput: EditText
    private lateinit var searchIcon: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var contentContainer: View
    private lateinit var walletBalanceView: TextView
    private lateinit var walletIcon: ImageView
    private lateinit var mailIcon: ImageView
    private lateinit var confirmationPanel: CardView
    private lateinit var selectedProductView: TextView
    private lateinit var selectedPriceView: TextView
    private lateinit var buyButton: Button
    private lateinit var errorMessageView: TextView

    private var currentUserId: Long = -1
    private val wishlistedProducts = mutableSetOf<Long>()
    private val searchResults = mutableListOf<Product>()
    private val categoryResults = mutableListOf<ProductCategory>()
    private var selectedProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Get user ID from FlowpayApp
        val app = application as FlowpayApp
        currentUserId = app.loggedInuser?.user_id ?: -1

        // Initialize ViewModels
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        wishlistViewModel = ViewModelProvider(this).get(WishlistViewModel::class.java)
        categoryViewModel = ViewModelProvider(this).get(ProductCategoryViewModel::class.java)
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        mailViewModel = ViewModelProvider(this).get(MailViewModel::class.java)

        // Initialize views
        searchInput = findViewById(R.id.search_input)
        searchIcon = findViewById(R.id.search_icon)
        recyclerView = RecyclerView(this)
        contentContainer = findViewById(R.id.content_container)
        walletBalanceView = findViewById(R.id.tv_wallet_balance)
        walletIcon = findViewById(R.id.wallet_icon)
        mailIcon = findViewById(R.id.mail_icon)
        confirmationPanel = findViewById(R.id.confirmation_panel)
        selectedProductView = findViewById(R.id.tv_selected_product)
        selectedPriceView = findViewById(R.id.tv_selected_price)
        buyButton = findViewById(R.id.btn_buy)
        errorMessageView = findViewById(R.id.tv_error_message)

        // Add RecyclerView to content container
        (contentContainer as? android.widget.FrameLayout)?.addView(recyclerView)

        // Set up RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 columns

        // Initially hide confirmation panel and error message
        confirmationPanel.visibility = View.GONE
        errorMessageView.visibility = View.GONE

        // Set wallet balance
        val user = app.loggedInuser
        if (user != null) {
            walletBalanceView.text = "₱${String.format("%,.2f", user.walletBalance)}"
        }

        // Set up click listeners
        walletIcon.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        mailIcon.setOnClickListener {
            startActivityForResult(Intent(this, MailsActivity::class.java), MAIL_LIST_REQUEST_CODE)
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
                                transactionViewModel.createTransaction(transaction)
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
                                walletBalanceView.text = "₱${String.format("%,.2f", user.walletBalance)}"
                                confirmationPanel.visibility = View.GONE
                                Toast.makeText(this@SearchActivity, "Purchase successful!", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(this@SearchActivity, "Error processing purchase", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set up search functionality
        searchInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                performSearch(searchInput.text.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        searchIcon.setOnClickListener {
            performSearch(searchInput.text.toString())
        }

        // Load user's wishlist
        wishlistViewModel.getUserWishlist(currentUserId)

        // Observe wishlist changes
        wishlistViewModel.userWishlist.observe(this, Observer { wishlist ->
            // Clear previous wishlist
            wishlistedProducts.clear()

            // Add all product IDs from wishlist
            wishlist?.forEach { item ->
                wishlistedProducts.add(item.product_id)
            }

            // Update search results
            updateSearchResults()
        })

        // Observe products
        productViewModel.allProducts.observe(this, Observer { products ->
            // Store all products for searching
            updateSearchResults()
        })
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
            return
        }

        // Filter products based on search query
        val allProducts = productViewModel.allProducts.value ?: emptyList()
        searchResults.clear()
        searchResults.addAll(allProducts.filter { product ->
            product.productName.contains(query, ignoreCase = true)
        })

        // Filter categories based on search query
        val allCategories = categoryViewModel.allCategories
        categoryResults.clear()
        categoryResults.addAll(allCategories.filter { category ->
            category.category_name.contains(query, ignoreCase = true)
        })

        // If we found categories but no products, get products from those categories
        if (searchResults.isEmpty() && categoryResults.isNotEmpty()) {
            // Get products for the first matching category
            val category = categoryResults.first()
            productViewModel.getProductsByCategory(category.category_id)

            // Wait for the LiveData to update
            productViewModel.allProducts.observe(this, object : Observer<List<Product>> {
                override fun onChanged(products: List<Product>) {
                    searchResults.clear()
                    searchResults.addAll(products)
                    updateSearchResults()
                    // Remove the observer after one update
                    productViewModel.allProducts.removeObserver(this)
                }
            })
        } else {
            // Update adapter immediately if we have product results or no results
            updateSearchResults()
        }
    }

    private fun updateSearchResults() {
        // If we have product results, show them
        if (searchResults.isNotEmpty()) {
            recyclerView.adapter = ProductAdapter(
                searchResults,
                onProductSelected = { product -> onProductSelected(product) },
                onWishlistClicked = { product -> toggleWishlist(product) },
                isInWishlist = { product -> wishlistedProducts.contains(product.product_id) }
            )
        } else {
            // No results
            recyclerView.adapter = ProductAdapter(
                emptyList(),
                onProductSelected = { product -> onProductSelected(product) },
                onWishlistClicked = { product -> toggleWishlist(product) },
                isInWishlist = { product -> wishlistedProducts.contains(product.product_id) }
            )
        }
    }

    private fun onCategorySelected(category: ProductCategory) {
        // Navigate to ProductsListActivity with the selected category
        val intent = Intent(this, ProductsListActivity::class.java).apply {
            putExtra("CATEGORY_ID", category.category_id)
            putExtra("CATEGORY_NAME", category.category_name)
            putExtra("USER_ID", currentUserId)
        }
        startActivity(intent)
    }

    private fun onProductSelected(product: Product) {
        if (selectedProduct == product) {
            // If the same product is selected, hide the panel and deselect
            confirmationPanel.visibility = View.GONE
            selectedProduct = null
            (recyclerView.adapter as? ProductAdapter)?.setSelectedProduct(null)
        } else {
            // Show confirmation panel
            confirmationPanel.visibility = View.VISIBLE

            // Update selected product info
            selectedProduct = product
            selectedProductView.text = "${product.productName} • E-Wallet"
            selectedPriceView.text = "₱${product.price}"

            // Update buy button state based on current balance
            val user = (application as FlowpayApp).loggedInuser
            if (user != null) {
                updateBuyButtonState(user.walletBalance, product.price)
            }

            // Update product card background
            (recyclerView.adapter as? ProductAdapter)?.setSelectedProduct(product)
        }
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

    private fun updateBuyButtonState(userBalance: Double, productPrice: Double) {
        val canBuy = userBalance >= productPrice
        buyButton.isEnabled = canBuy
        errorMessageView.visibility = if (canBuy) View.GONE else View.VISIBLE
    }

    private fun toggleWishlist(product: Product) {
        if (wishlistedProducts.contains(product.product_id)) {
            // Show confirmation dialog for removing from wishlist
            DialogUtils.showCustomConfirmationDialog(
                context = this,
                title = "Remove from wishlist",
                message = "are you sure you want to remove this item?",
                onConfirm = {
                    wishlistViewModel.removeFromWishlist(currentUserId, product.product_id)
                    wishlistedProducts.remove(product.product_id)
                    (recyclerView.adapter as? ProductAdapter)?.updateWishlistState(product)
                    Toast.makeText(this, "Removed from wishlist", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            // Add to wishlist
            val wishlist = Wishlist(
                user_id = currentUserId,
                product_id = product.product_id
            )
            wishlistViewModel.addToWishlist(wishlist)
            wishlistedProducts.add(product.product_id)
            (recyclerView.adapter as? ProductAdapter)?.updateWishlistState(product)
            Toast.makeText(this, "Added to wishlist", Toast.LENGTH_SHORT).show()
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
}
