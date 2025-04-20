package com.mine.flowpay

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.mine.flowpay.app.FlowpayApp
import com.mine.flowpay.utils.DialogUtils
import com.mine.flowpay.data.Users
import com.mine.flowpay.viewmodel.UserViewModel

class ProfileActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var currentUser: Users

    // UI components
    private lateinit var usernameTextView: TextView
    private lateinit var editButton: TextView
    private lateinit var balanceTextView: TextView
    private lateinit var amountEditText: EditText
    private lateinit var cashInButton: Button

    // Menu items
    private lateinit var mailsMenu: LinearLayout
    private lateinit var transactionsMenu: LinearLayout
    private lateinit var wishlistMenu: LinearLayout
    private lateinit var searchMenu: LinearLayout
    private lateinit var settingsMenu: LinearLayout
    private lateinit var logoutMenu: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Get current user from application
        val app = application as FlowpayApp
        currentUser = app.loggedInuser ?: run {
            // If no user is logged in, redirect to login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Initialize UI components
        initializeViews()

        // Set up UI with user data
        updateUI()

        // Set up click listeners
        setupClickListeners()
    }

    private fun initializeViews() {
        // Find views by ID
        usernameTextView = findViewById(R.id.text_username)
        editButton = findViewById(R.id.btn_edit)
        balanceTextView = findViewById(R.id.text_balance)
        amountEditText = findViewById(R.id.edit_amount)
        cashInButton = findViewById(R.id.btn_cash_in)

        // Menu items
        mailsMenu = findViewById(R.id.menu_mails)
        transactionsMenu = findViewById(R.id.menu_transactions)
        wishlistMenu = findViewById(R.id.menu_wishlist)
        searchMenu = findViewById(R.id.menu_search)
        settingsMenu = findViewById(R.id.menu_settings)
        logoutMenu = findViewById(R.id.menu_logout)
    }

    private fun updateUI() {
        // Set username and balance
        usernameTextView.text = currentUser.username
        balanceTextView.text = "₱${currentUser.walletBalance}"
    }

    private fun setupClickListeners() {
        // Edit button click
        editButton.setOnClickListener {
            // TODO: Implement edit profile functionality
            Toast.makeText(this, "Edit profile functionality coming soon", Toast.LENGTH_SHORT).show()
        }

        // Cash in button click
        cashInButton.setOnClickListener {
            val amountText = amountEditText.text.toString()
            if (amountText.isNotEmpty()) {
                try {
                    val amount = amountText.toDouble()
                    if (amount > 0) {
                        // Update wallet balance
                        val newBalance = currentUser.walletBalance + amount
                        userViewModel.updateWalletBalance(currentUser.user_id, newBalance)

                        // Update current user in memory
                        currentUser.walletBalance = newBalance
                        (application as FlowpayApp).loggedInuser = currentUser

                        // Update UI
                        updateUI()

                        // Clear input field
                        amountEditText.text.clear()

                        Toast.makeText(this, "Successfully added ₱$amount to your wallet", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
            }
        }

        // Menu item clicks
        mailsMenu.setOnClickListener {
            // Navigate to mails screen
            startActivity(Intent(this, MailsActivity::class.java))
        }

        transactionsMenu.setOnClickListener {
            // Navigate to transactions screen
            startActivity(Intent(this, TransactionActivity::class.java))
        }

        wishlistMenu.setOnClickListener {
            // Navigate to likes/wishlist screen
            startActivity(Intent(this, LikesActivity::class.java))
        }

        searchMenu.setOnClickListener {
            // Navigate to search screen
            startActivity(Intent(this, SearchActivity::class.java))
        }

        settingsMenu.setOnClickListener {
            // Navigate to settings screen
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        logoutMenu.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        DialogUtils.showCustomConfirmationDialog(
            context = this,
            title = "Logout",
            message = "Are you sure you want to log out?",
            onConfirm = {
                // Clear logged in user
                (application as FlowpayApp).loggedInuser = null
                // Navigate to login screen
                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                finish()
            }
        )
    }
}
