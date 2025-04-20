package com.mine.flowpay

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mine.flowpay.app.FlowpayApp
import com.mine.flowpay.fragments.NavbarFragment
import com.mine.flowpay.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionDetails : AppCompatActivity() {
    companion object {
        private const val MAIL_LIST_REQUEST_CODE = 100
    }
    private lateinit var transactionViewModel: TransactionViewModel

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

        // Set up navbar
        supportFragmentManager.beginTransaction()
            .replace(R.id.navbar_container, NavbarFragment())
            .commit()

        // Initialize ViewModel
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

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
                // Set transaction details
                productNameView.text = transaction.type
                priceView.text = "₱${transaction.amount}"
                transactionIdView.text = transaction.transaction_id.toString()
                // Use transaction type as category
                categoryView.text = transaction.type

                // Format date
                val dateFormat = SimpleDateFormat("MM/dd/yy HH:mm", Locale.getDefault())
                dateView.text = dateFormat.format(Date(transaction.timestamp))
            }
        }

        // Set up back button
        backButton.setOnClickListener {
            onBackPressed()
        }

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
