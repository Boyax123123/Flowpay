package com.mine.flowpay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.mine.flowpay.utils.DialogUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mine.flowpay.app.FlowpayApp
import com.mine.flowpay.data.Mail
import com.mine.flowpay.viewmodel.MailViewModel
import com.mine.flowpay.viewmodel.ProductViewModel
import com.mine.flowpay.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

class MailDetailsActivity : AppCompatActivity() {
    private lateinit var mailViewModel: MailViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var backButton: ImageView
    private lateinit var deleteButton: ImageView
    private lateinit var subjectTextView: TextView
    private lateinit var messageTextView: TextView
    private lateinit var currentMail: Mail

    private var mailId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mail_details)

        // Get mail ID from intent
        mailId = intent.getLongExtra("MAIL_ID", -1)
        if (mailId == -1L) {
            finish()
            return
        }

        // Set header title
        val headerTitle = findViewById<TextView>(R.id.tv_title)
        headerTitle.text = "Mail Details"

        // Initialize ViewModels
        mailViewModel = ViewModelProvider(this).get(MailViewModel::class.java)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        // Initialize views
        backButton = findViewById(R.id.iv_back)
        deleteButton = findViewById(R.id.iv_delete)
        subjectTextView = findViewById(R.id.tv_subject)
        messageTextView = findViewById(R.id.tv_message)

        // Load mail details
        loadMailDetails()

        // Set up back button
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Set up delete button
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Set up delete button
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun loadMailDetails() {
        lifecycleScope.launch {
            val mail = mailViewModel.getMailById(mailId)
            if (mail != null) {
                currentMail = mail

                // Mark mail as read immediately when opened
                if (!mail.isRead) {
                    mail.isRead = true
                    mailViewModel.updateMail(mail)
                }
                // Always set result to refresh unread count in previous screen
                setResult(RESULT_OK)

                // Update UI with mail details
                runOnUiThread {
                    subjectTextView.text = mail.subject

                    // If this mail is associated with a transaction, get product and category info
                    if (mail.transaction_id != null) {
                        val transactionId = mail.transaction_id
                        val transaction = transactionViewModel.getTransactionById(transactionId)

                        if (transaction != null) {
                            // Get product name from transaction type
                            val productName = transaction.type

                            // Try to find the product in the database
                            val product = productViewModel.getProductByName(productName)

                            // If product found, get its category
                            var categoryName = ""
                            if (product != null) {
                                val category = productViewModel.getCategoryById(product.category_id)
                                if (category != null) {
                                    categoryName = category.category_name
                                }
                            }

                            // Inject product name and category into message
                            val message = mail.message.replace("[product name]", productName)
                                .replace("[category]", categoryName)
                            messageTextView.text = message
                        } else {
                            messageTextView.text = mail.message
                        }

                        // Add button to view transaction details
                        val transactionButton = findViewById<TextView>(R.id.btn_view_transaction)
                        transactionButton?.apply {
                            text = "Purchase Receipt"
                            setTextColor(resources.getColor(R.color.blue, null))
                            setOnClickListener {
                                val intent = Intent(
                                    this@MailDetailsActivity,
                                    TransactionDetails::class.java
                                ).apply {
                                    putExtra("TRANSACTION_ID", transactionId)
                                }
                                startActivity(intent)
                            }
                            visibility = android.view.View.VISIBLE
                        }
                    } else {
                        messageTextView.text = mail.message
                    }
                }
            } else {
                finish()
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        DialogUtils.showCustomConfirmationDialog(
            context = this,
            title = "Delete Mail",
            message = "Are you sure you want to delete this mail?",
            onConfirm = {
                deleteMail()
            }
        )
    }

    private fun deleteMail() {
        lifecycleScope.launch {
            mailViewModel.deleteMail(currentMail)
            runOnUiThread {
                Toast.makeText(this@MailDetailsActivity, "Mail deleted", Toast.LENGTH_SHORT).show()
            }
            setResult(RESULT_OK)
            finish()
        }
    }
}
