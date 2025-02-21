package com.example.flowpay


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)  // Make sure this is the correct layout file name

        val buttonLogout: Button = findViewById(R.id.button_logout)


        buttonLogout.setOnClickListener {
            val intent = Intent(this, LogoutActivity::class.java)
            startActivity(intent)  // Start the LogoutActivity
        }



//        testing area

        val button_test: Button = findViewById<Button>(R.id.button_test)
        button_test.setOnClickListener {
            val intent = Intent(this, TestingActivity::class.java)
            startActivity(intent)  // Start the RegisterActivity
        }

    }
}
