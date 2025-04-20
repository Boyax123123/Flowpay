package com.mine.flowpay

import android.content.Intent
import android.os.Bundle
import android.view.View
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

    private var currentUserId: Long = -1
    private var selectedProduct: Product? = null
    private val wishlistedProducts = mutableSetOf<Long>()

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

        // Set category title
        categoryTitleView.text = categoryName

        // Initially hide confirmation panel and error message
        confirmationPanel.visibility = View.GONE
        errorMessageView.visibility = View.GONE

        // Set initial wallet balance
        val user = (application as FlowpayApp).loggedInuser
        if (user != null) {
            walletBalanceView.text = "₱${user.walletBalance}"
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

        // Load and observe products for the selected category
        productViewModel.getProductsByCategory(categoryId)
        productViewModel.allProducts.observe(this, Observer { products ->
            if (products != null) {
                updateAdapter(products)
            }
        })

        // Load wishlist items for current user
        wishlistViewModel.getUserWishlist(currentUserId)
        wishlistViewModel.userWishlist.observe(this, Observer { wishlist ->
            wishlistedProducts.clear()
            wishlist.forEach { item ->
                wishlistedProducts.add(item.product_id)
            }
            // Get current products and update adapter
            productViewModel.allProducts.value?.let { products ->
                updateAdapter(products)
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
                                walletBalanceView.text = "₱${user.walletBalance}"
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
            finish()
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

        recyclerView.adapter = ProductAdapter(products, 
            onProductSelected = { product -> onProductSelected(product) },
            onWishlistClicked = { product -> toggleWishlist(product) },
            isInWishlist = { product -> wishlistedProducts.contains(product.product_id) }
        )
    }

    private fun onProductSelected(product: Product) {
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
}
