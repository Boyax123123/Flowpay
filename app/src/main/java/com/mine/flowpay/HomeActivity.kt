package com.mine.flowpay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mine.flowpay.adapters.GameCategoryAdapter
import com.mine.flowpay.app.FlowpayApp
import com.mine.flowpay.MailsActivity
import com.mine.flowpay.viewmodel.ProductCategoryViewModel
import com.mine.flowpay.viewmodel.MailViewModel
import android.view.View

class HomeActivity : AppCompatActivity() {
    companion object {
        private const val MAIL_LIST_REQUEST_CODE = 100
    }
    private var mailObserver: androidx.lifecycle.Observer<Int>? = null
    private lateinit var viewModel: ProductCategoryViewModel
    private lateinit var mailViewModel: MailViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var walletBalanceView: TextView
    private lateinit var walletIcon: ImageView
    private lateinit var mailIcon: ImageView

    private var currentUserId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Get user ID from FlowpayApp
        val app = application as FlowpayApp
        currentUserId = app.loggedInuser?.user_id ?: -1

        // Initialize ViewModels
        viewModel = ViewModelProvider(this).get(ProductCategoryViewModel::class.java)
        mailViewModel = ViewModelProvider(this).get(MailViewModel::class.java)

        // Initialize views
        recyclerView = findViewById(R.id.rv_game_categories)
        walletBalanceView = findViewById(R.id.tv_wallet_balance)
        walletIcon = findViewById(R.id.wallet_icon)
        mailIcon = findViewById(R.id.mail_icon)

        // Set wallet balance
        walletBalanceView.text = "₱${app.loggedInuser?.walletBalance ?: 0.0}"

        // Set up wallet icon click listener
        walletIcon.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Set up mail icon click listener
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

        // Verify database content
        verifyDatabaseContent(app)

        // Set up RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 columns

        // Get categories and set up adapter
        val categories = viewModel.allCategories
        if (categories.isNotEmpty()) {
            Log.d("HomeActivity", "Categories loaded: ${categories.size}")
            categories.forEach { category ->
                Log.d("HomeActivity", "Category: ${category.category_name}, Image: ${category.image}")
                // Verify that the image resource exists
                try {
                    resources.getResourceName(category.image)
                    Log.d("HomeActivity", "Image resource exists: ${resources.getResourceName(category.image)}")
                } catch (e: Exception) {
                    Log.e("HomeActivity", "Image resource does not exist: ${category.image}", e)
                }
            }

            // Set up adapter
            recyclerView.adapter = GameCategoryAdapter(categories)
        } else {
            Log.e("HomeActivity", "No categories found")
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

    private fun verifyDatabaseContent(app: FlowpayApp) {
        // Get categories directly from the database
        val categories = app.database.categoryDao().getAllCategories()

        Log.d("HomeActivity", "Verifying database content - Categories count: ${categories.size}")

        // Check each category
        categories.forEach { category ->
            Log.d("HomeActivity", "DB Category: ${category.category_name}, Image ID: ${category.image}")

            // Verify image resource exists
            try {
                val resourceName = resources.getResourceName(category.image)
                Log.d("HomeActivity", "Resource exists: $resourceName")

                // Try to load the drawable
                try {
                    val drawable = resources.getDrawable(category.image, theme)
                    Log.d("HomeActivity", "Drawable loaded successfully for ${category.category_name}")
                } catch (e: Exception) {
                    Log.e("HomeActivity", "Failed to load drawable for ${category.category_name}: ${e.message}", e)
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Resource does not exist for ${category.category_name}: ${e.message}", e)
            }
        }
    }
}
