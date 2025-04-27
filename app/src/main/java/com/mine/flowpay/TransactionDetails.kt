package com.mine.flowpay

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mine.flowpay.app.FlowpayApp
import com.mine.flowpay.fragments.NavbarFragment
import com.mine.flowpay.viewmodel.TransactionViewModel
import com.mine.flowpay.viewmodel.ProductViewModel
import com.mine.flowpay.viewmodel.ProductCategoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionDetails : AppCompatActivity() {
    companion object {
        private const val MAIL_LIST_REQUEST_CODE = 100
    }
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var categoryViewModel: ProductCategoryViewModel

    // UI components
    private lateinit var backButton: ImageView
    private lateinit var productNameView: TextView
    private lateinit var priceView: TextView
    private lateinit var transactionIdView: TextView
    private lateinit var categoryView: TextView
    private lateinit var dateView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)

        // Set header title
        val headerTitle = findViewById<TextView>(R.id.tv_header_title)
        headerTitle.text = "Transaction Details"

        // Set up navbar
        supportFragmentManager.beginTransaction()
            .replace(R.id.navbar_container, NavbarFragment())
            .commit()

        // Initialize ViewModels
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        categoryViewModel = ViewModelProvider(this).get(ProductCategoryViewModel::class.java)

        // Initialize views
        backButton = findViewById(R.id.iv_back)
        productNameView = findViewById(R.id.tv_product_name_header)
        priceView = findViewById(R.id.tv_price_header)
        transactionIdView = findViewById(R.id.tv_transaction_id_value)
        categoryView = findViewById(R.id.tv_category_value)
        dateView = findViewById(R.id.tv_date_value)

        // Get transaction ID from intent
        val transactionId = intent.getLongExtra("TRANSACTION_ID", -1)
        if (transactionId != -1L) {
            // Get transaction details
            val transaction = transactionViewModel.getTransactionById(transactionId)
            if (transaction != null) {
                // Set initial transaction details
                productNameView.text = transaction.type
                priceView.text = "₱${String.format("%.2f", transaction.amount)}"
                transactionIdView.text = transaction.transaction_id.toString()

                // Find product by name and get its category
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val product = productViewModel.getProductByName(transaction.type)
                        if (product != null) {
                            // Get category name
                            val category = categoryViewModel.getCategoryById(product.category_id)
                            withContext(Dispatchers.Main) {
                                categoryView.text = category?.category_name ?: "Unknown Category"
                            }
                        } else {
                            // Try to parse category from transaction type
                            val parts = transaction.type.split(" - ")
                            if (parts.size > 1) {
                                withContext(Dispatchers.Main) {
                                    // If the type contains a separator, assume second part is the category
                                    productNameView.text = parts[0]
                                    categoryView.text = parts[1]
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    categoryView.text = "Unknown Category"
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            categoryView.text = "Unknown Category"
                        }
                    }
                }

                // Format date
                val dateFormat = SimpleDateFormat("MM/dd/yy HH:mm", Locale.getDefault())
                dateView.text = dateFormat.format(Date(transaction.timestamp))
            }
        }

        // Set header title and back button
        findViewById<ImageView>(R.id.iv_back).setOnClickListener { onBackPressed() }

        // Set up navbar fragment with FlowpayApp instance
        val app = application as FlowpayApp
        val navbarFragment = NavbarFragment().apply {
            arguments = Bundle().apply {
                putLong("USER_ID", app.loggedInuser?.user_id ?: -1)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.navbar_container, navbarFragment)
            .commit()
    }
}
