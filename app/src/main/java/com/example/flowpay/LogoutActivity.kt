package com.example.flowpay


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class LogoutActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.logout)  // Make sure this is the correct layout file name

        // Reference each button in your layout using their IDs
        val button_test: Button = findViewById<Button>(R.id.button_test)
        button_test.setOnClickListener {
            val intent = Intent(this, TestingActivity::class.java)
            startActivity(intent)  // Start the RegisterActivity
        }
    }
}
