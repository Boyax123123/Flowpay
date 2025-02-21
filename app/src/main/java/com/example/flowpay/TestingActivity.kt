package com.example.flowpay


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class TestingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_pages)  // Make sure this is the correct layout file name

        // Reference each button in your layout using their IDs
        val buttonLogin: Button = findViewById(R.id.button_login)
        val buttonRegister: Button = findViewById(R.id.button_register)
        val buttonLogout: Button = findViewById(R.id.button_logout)
        val buttonLanding: Button = findViewById(R.id.button_landing)
        val buttonProfile: Button = findViewById(R.id.button_profile)
        val buttonDeveloper: Button = findViewById(R.id.button_developer)

        // Set click listeners for each button to navigate to corresponding activity
        buttonLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)  // Start the LoginActivity
        }

        buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)  // Start the RegisterActivity
        }

        buttonLogout.setOnClickListener {
            val intent = Intent(this, LogoutActivity::class.java)
            startActivity(intent)  // Start the LogoutActivity
        }

        buttonLanding.setOnClickListener {
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)  // Start the LandingActivity
        }

        buttonProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)  // Start the ProfileActivity
        }

        buttonDeveloper.setOnClickListener {
            val intent = Intent(this, DeveloperActivity::class.java)
            startActivity(intent)  // Start the DeveloperActivity
        }
    }
}
