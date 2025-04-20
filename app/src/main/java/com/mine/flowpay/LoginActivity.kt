package com.mine.flowpay

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mine.flowpay.app.FlowpayApp
import com.mine.flowpay.data.database.AppDatabase
import com.mine.flowpay.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var btnLogin: Button
    private lateinit var txtEmailError: TextView
    private lateinit var txtPasswordError: TextView
    private var emailIsGood = false
    private var passwordIsGood = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is already logged in
        val app = application as FlowpayApp
        if (app.sessionManager.isLoggedIn()) {
            // User is already logged in, redirect to home
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        // Initialize repository
        userRepository = UserRepository(AppDatabase.getInstance(this).userDao())

        // Set up views
        val txtEmail = findViewById<EditText>(R.id.edittext_email)
        val txtPassword = findViewById<EditText>(R.id.edittext_password)
        btnLogin = findViewById(R.id.btn_login)
        txtEmailError = findViewById(R.id.txt_email_error)
        txtPasswordError = findViewById(R.id.txt_password_error)
        val txtRegister = findViewById<TextView>(R.id.txt_register)

        // Hide error messages initially
        txtEmailError.text = ""
        txtPasswordError.text = ""
        txtEmailError.setTextColor(getColor(R.color.error_text))
        txtPasswordError.setTextColor(getColor(R.color.error_text))

        // Initially disable button
        updateButtonState()

        // Email validation
        txtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().lowercase()
                lifecycleScope.launch {
                    if (!isValidEmail(email)) {
                        txtEmailError.text = "Invalid Email"
                        emailIsGood = false
                    } else {
                        val user = userRepository.getUserByEmail(email)
                        if (user == null) {
                            txtEmailError.text = "Email not registered"
                            emailIsGood = false
                        } else {
                            txtEmailError.text = ""
                            emailIsGood = true
                        }
                    }
                    updateButtonState()
                }
            }
        })

        // Password validation
        txtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                if (password.isEmpty()) {
                    txtPasswordError.text = "Password is required"
                    passwordIsGood = false
                } else {
                    txtPasswordError.text = ""
                    passwordIsGood = true
                }
                updateButtonState()
            }
        })

        // Login button click
        btnLogin.setOnClickListener {
            val email = txtEmail.text.toString().lowercase()
            val password = txtPassword.text.toString()

            lifecycleScope.launch {
                try {
                    val user = userRepository.getUserByEmail(email)
                    if (user == null) {
                        txtEmailError.text = "Email not registered"
                    } else if (user.password != password) {
                        txtPasswordError.text = "Incorrect password"
                        passwordIsGood = false
                        updateButtonState()
                    } else {
                        // Set current user in FlowpayApp
                        (application as FlowpayApp).apply {
                            loggedInuser = user
                            isLoggedIn = true
                        }
                        
                        // Navigate to home screen
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    txtPasswordError.text = "Login error: ${e.message}"
                }
            }
        }

        // Redirect to register page
        txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun updateButtonState() {
        val isValid = emailIsGood && passwordIsGood
        btnLogin.isEnabled = isValid
        btnLogin.alpha = if (isValid) 1.0f else 0.5f
    }
}