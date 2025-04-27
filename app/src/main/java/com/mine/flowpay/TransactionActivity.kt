package com.mine.flowpay

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mine.flowpay.adapters.TransactionAdapter
import com.mine.flowpay.app.FlowpayApp
import com.mine.flowpay.fragments.NavbarFragment
import com.mine.flowpay.viewmodel.TransactionViewModel

class TransactionActivity : AppCompatActivity() {
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: ImageView
    
    private var currentUserId: Long = -1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)
        
        // Get user ID from FlowpayApp
        val app = application as FlowpayApp
        currentUserId = app.loggedInuser?.user_id ?: -1
        
        // Initialize ViewModel
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        
        // Initialize views
        recyclerView = findViewById(R.id.rv_transactions)
        backButton = findViewById(R.id.iv_back)
        
        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Get transactions for the current user
        transactionViewModel.getUserTransactions(currentUserId)
        
        // Observe transactions
        transactionViewModel.allTransactions.observe(this, Observer { transactions ->
            recyclerView.adapter = TransactionAdapter(transactions) { transaction ->
                // Handle transaction click - navigate to TransactionDetails
                val intent = Intent(this, TransactionDetails::class.java).apply {
                    putExtra("TRANSACTION_ID", transaction.transaction_id)
                }
                startActivity(intent)
            }
        })
        
        // Set header title and back button
        val headerTitle = findViewById<TextView>(R.id.tv_header_title)
        headerTitle.text = "Transactions"
        findViewById<ImageView>(R.id.iv_back).setOnClickListener { onBackPressed() }

        // Set up navbar
        supportFragmentManager.beginTransaction()
            .replace(R.id.navbar_container, NavbarFragment())
            .commit()
    }
}