package com.mine.flowpay

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.mine.flowpay.utils.SortButtonsHandler
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.inputmethod.InputMethodManager
import android.text.TextWatcher
import android.text.Editable
import android.view.LayoutInflater
import android.view.Window

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
    private lateinit var clearSearchButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var contentContainer: View
    private lateinit var emptyStateContainer: View
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

    private lateinit var sortButtonsHandler: SortButtonsHandler

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
        clearSearchButton = findViewById(R.id.clear_search_button)
        recyclerView = RecyclerView(this)
        contentContainer = findViewById(R.id.content_container)
        emptyStateContainer = findViewById(R.id.empty_state_container)
        walletBalanceView = findViewById(R.id.tv_balance)
        walletIcon = findViewById(R.id.iv_wallet)
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
        emptyStateContainer.visibility = View.GONE

        // Set wallet balance
        val user = app.loggedInuser
        if (user != null) {
            walletBalanceView.text = "₱${String.format("%,.2f", user.walletBalance)}"
        }

        // Set up click listeners
        walletIcon.setOnClickListener {
            startActivity(Intent(this, WalletActivity::class.java))
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
            purchaseSelectedProduct()
        }
        
        // Set up search functionality
        searchInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                Log.d("SearchActivity", "Search action triggered from keyboard")
                performSearch(searchInput.text.toString())
                true
            } else {
                false
            }
        }

        searchIcon.setOnClickListener {
            Log.d("SearchActivity", "Search action triggered from search icon")
            performSearch(searchInput.text.toString())
        }
        
        // Set up clear search button
        clearSearchButton.setOnClickListener {
            clearSearch()
        }
        
        // Add text change listener to search input (toggle clear button only)
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearSearchButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

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
            Log.d("SearchActivity", "Products observed: ${products.size}")
            // Store all products for initial display
            if (searchInput.text.toString().isBlank()) {
                searchResults.clear()
                searchResults.addAll(products)
                updateSearchResults()
            }
            
            // Update sort buttons with all products
            if (::sortButtonsHandler.isInitialized) {
                sortButtonsHandler.updateProducts(products)
            }
        })
        
        // Set up sort buttons for product filtering
        setupSortButtons()
        
        // Initialize with empty state if needed
        updateEmptyState()

        // Observe categories
        categoryViewModel.allCategories.observe(this, Observer { categories ->
            // Store categories for searching
            if (!categories.isNullOrEmpty()) {
                categoryResults.clear()
                categoryResults.addAll(categories)
                Log.d("SearchActivity", "Categories loaded: ${categories.size}")
            } else {
                Log.d("SearchActivity", "No categories found")
            }
        })
    }

    private fun performSearch(query: String) {
        Log.d("SearchActivity", "Performing search with query: $query")
        
        if (query.isBlank()) {
            // If query is blank, show all products
            productViewModel.allProducts.value?.let {
                searchResults.clear()
                searchResults.addAll(it)
                updateSearchResults()
            }
            return
        }
        
        // Search in products
        productViewModel.allProducts.value?.let { allProducts ->
            val filteredProducts = allProducts.filter { product ->
                product.productName.contains(query, ignoreCase = true) ||
                categoryResults.any { category ->
                    category.category_name.contains(query, ignoreCase = true) &&
                    category.category_id == product.category_id
                }
            }
            Log.d("SearchActivity", "Found ${filteredProducts.size} products for search or category '$query'")
            
            if (filteredProducts.isEmpty()) {
                // No results found - show empty state
                searchResults.clear()
                updateSearchResults()
                updateEmptyState()
            } else {
                // Apply sort on filtered (search) results
                sortButtonsHandler.updateProducts(filteredProducts, true)
            }
        }
    }

    private fun clearSearch() {
        // Clear search input
        searchInput.setText("")
        
        // Hide clear button
        clearSearchButton.visibility = View.GONE
        
        // Load all products
        val allProducts = productViewModel.allProducts.value ?: emptyList()
        searchResults.clear()
        searchResults.addAll(allProducts)
        
        // Update sort buttons with all products
        if (::sortButtonsHandler.isInitialized) {
            sortButtonsHandler.updateProducts(allProducts, false)
        }
        
        // Update the UI
        updateSearchResults()
        updateEmptyState()
    }
    
    private fun updateEmptyState() {
        if (searchResults.isEmpty()) {
            // Show empty state
            emptyStateContainer.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            // Show results
            emptyStateContainer.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
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
        
        // Update empty state visibility
        updateEmptyState()
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
            hideConfirmationPanel()
            selectedProduct = null
            (recyclerView.adapter as? ProductAdapter)?.apply {
                setSelectedProduct(null)
                notifyDataSetChanged() // Force immediate UI update
            }
        } else {
            showConfirmationPanel(product)
            selectedProduct = product
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

    private fun setupSortButtons() {
        // Initialize sort buttons
        val btnSortPopular = findViewById<Button>(R.id.btn_sort_popular)
        val btnSortHighLow = findViewById<Button>(R.id.btn_sort_high_low)
        val btnSortLowHigh = findViewById<Button>(R.id.btn_sort_low_high)
        val btnSortAZ = findViewById<Button>(R.id.btn_sort_a_z)
        
        // Get transaction repository from TransactionViewModel
        val app = application as FlowpayApp
        val database = app.database
        val transactionRepository = transactionViewModel.getTransactionRepository() ?: database.transactionDao()?.let {
            com.mine.flowpay.data.repository.TransactionRepository(it)
        }
        
        // Create the sort buttons handler
        sortButtonsHandler = SortButtonsHandler(
            btnSortPopular,
            btnSortHighLow,
            btnSortLowHigh,
            btnSortAZ,
            transactionRepository,
        ) { sortedProducts ->
            // Only update if we received non-empty sorted products
            Log.d("SearchActivity", "Sort button handler returned ${sortedProducts.size} products")
            
            if (sortedProducts.isNotEmpty()) {
                Log.d("SearchActivity", "Updating search results with sorted products")
                searchResults.clear()
                searchResults.addAll(sortedProducts)
                updateSearchResults()
            } else if (searchResults.isEmpty() && productViewModel.allProducts.value != null) {
                // If search results are empty, use the products from the view model
                Log.d("SearchActivity", "Using products from view model as fallback")
                searchResults.clear()
                searchResults.addAll(productViewModel.allProducts.value!!)
                updateSearchResults()
            } else {
                Log.d("SearchActivity", "Keeping existing ${searchResults.size} search results")
            }
            // If sortedProducts is empty but searchResults already has items, we keep them
        }
        
        // No button selected by default (will use MOST_RECENT sort)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchInput.windowToken, 0)
    }

    private fun showConfirmationPanel(product: Product) {
        confirmationPanel.visibility = View.VISIBLE
        selectedProductView.text = product.productName
        selectedPriceView.text = "₱${product.price}"
        errorMessageView.visibility = View.GONE
        val user = (application as FlowpayApp).loggedInuser
        if (user != null && user.walletBalance >= product.price) {
            buyButton.isEnabled = true
            buyButton.alpha = 1.0f
        } else {
            buyButton.isEnabled = false
            buyButton.alpha = 0.5f
            errorMessageView.visibility = View.VISIBLE
        }
    }

    private fun hideConfirmationPanel() {
        confirmationPanel.visibility = View.GONE
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
                            hideConfirmationPanel()
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
}
