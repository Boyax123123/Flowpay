package com.example.flowpay

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val button_register: Button = findViewById<Button>(R.id.button_register )

        button_register.setOnClickListener {
            Log.e("CSIT 284", "Button is clicked")
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }

        val button_test: Button = findViewById<Button>(R.id.button_test)
        button_test.setOnClickListener {
            val intent = Intent(this, TestingActivity::class.java)
            startActivity(intent)  // Start the RegisterActivity
        }

    }
}