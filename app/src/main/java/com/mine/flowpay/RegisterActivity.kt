package com.mine.flowpay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.mine.flowpay.data.Users
import com.mine.flowpay.data.database.AppDatabase
import com.mine.flowpay.data.repository.UserRepository
import com.mine.flowpay.app.FlowpayApp

class RegisterActivity : AppCompatActivity() {
    private lateinit var btnRegister: Button
    private var emailIsGood = false
    private var usernameIsGood = false
    private var passwordIsGood = false
    private var confirmPasswordIsGood = false
    private lateinit var userRepository: UserRepository

    // Password requirement checks
    private var hasMinLength = false
    private var hasLettersNumbersSpecial = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Initialize repository using application's database instance
        val database = (application as FlowpayApp).database
        userRepository = UserRepository(database.userDao())

        // Set up views
        val txtEmail = findViewById<EditText>(R.id.edittext_email)
        val txtUsername = findViewById<EditText>(R.id.edittext_username)
        val txtPassword = findViewById<EditText>(R.id.edittext_password)
        val txtConfirmPassword = findViewById<EditText>(R.id.edittext_confirm_password)
        btnRegister = findViewById(R.id.btn_register)
        val loginText = findViewById<TextView>(R.id.txt_login)

        // Error text views
        val emailError = findViewById<TextView>(R.id.txt_email_error)
        val usernameError = findViewById<TextView>(R.id.txt_username_error)
        val confirmPasswordError = findViewById<TextView>(R.id.txt_confirm_password_error)

        // Password requirement icons
        val iconMinLength = findViewById<ImageView>(R.id.icon_min_length)
        val iconLettersNumbersSpecial = findViewById<ImageView>(R.id.icon_letters_numbers_special)

        // Initially hide error messages and disable button
        emailError.text = ""
        usernameError.text = ""
        confirmPasswordError.text = ""
        updateButtonState()

        // Set error text colors
        emailError.setTextColor(getColor(R.color.error_text))
        usernameError.setTextColor(getColor(R.color.error_text))
        confirmPasswordError.setTextColor(getColor(R.color.error_text))

        // Email validation
        txtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().lowercase()
                // First check: Valid email format
                if (!isValidEmail(email)) {
                    emailError.text = "Invalid Email"
                    emailIsGood = false
                } else {
                    // Second check: Email already registered
                    val existingUser = userRepository.getUserByEmail(email)
                    if (existingUser != null) {
                        emailError.text = "Email already registered"
                        emailIsGood = false
                    } else {
                        emailError.text = ""
                        emailIsGood = true
                    }
                }
                updateButtonState()
            }
        })

        // Username validation
        txtUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val username = s.toString()
                if (username.isEmpty()) {
                    usernameError.text = "Username is required"
                    usernameIsGood = false
                } else {
                    val existingUser = userRepository.getUserByUsername(username)
                    if (existingUser != null) {
                        usernameError.text = "Username already taken"
                        usernameIsGood = false
                    } else {
                        usernameError.text = ""
                        usernameIsGood = true
                    }
                }
                updateButtonState()
            }
        })

        // Password validation
        txtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                validatePassword(password, iconMinLength, iconLettersNumbersSpecial)
                validateConfirmPassword(password, txtConfirmPassword.text.toString(), confirmPasswordError)
                updateButtonState()
            }
        })

        // Confirm Password validation
        txtConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateConfirmPassword(txtPassword.text.toString(), s.toString(), confirmPasswordError)
            }
        })

        // Register button click
        btnRegister.setOnClickListener {
            val newUser = Users(
                email = txtEmail.text.toString().lowercase(),
                username = txtUsername.text.toString(),
                password = txtPassword.text.toString()
            )
            userRepository.insertUser(newUser)
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }

        // Redirect to login page
        loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun validatePassword(password: String, iconMinLength: ImageView, iconLettersNumbersSpecial: ImageView) {
        // Check minimum length
        hasMinLength = password.length >= 7
        iconMinLength.setImageResource(if (hasMinLength) R.drawable.ic_check_green else R.drawable.ic_dot_grey)

        // Check for letters, numbers, and special characters
        hasLettersNumbersSpecial = password.any { it.isLetter() } &&
                password.any { it.isDigit() } &&
                password.any { "!@#$%^&*()_+[]{}|;:,.<>?".contains(it) }
        iconLettersNumbersSpecial.setImageResource(
            if (hasLettersNumbersSpecial) R.drawable.ic_check_green else R.drawable.ic_dot_grey
        )

        // Update password validity
        passwordIsGood = hasMinLength && hasLettersNumbersSpecial
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String, errorText: TextView) {
        if (confirmPassword.isEmpty()) {
            errorText.text = "Confirm password is required"
            confirmPasswordIsGood = false
        } else if (password != confirmPassword) {
            errorText.text = "Passwords do not match"
            confirmPasswordIsGood = false
        } else {
            errorText.text = ""
            confirmPasswordIsGood = true
        }
        updateButtonState()
    }

    private fun updateButtonState() {
        val isValid = emailIsGood && usernameIsGood && passwordIsGood && confirmPasswordIsGood
        btnRegister.isEnabled = isValid
        btnRegister.alpha = if (isValid) 1.0f else 0.5f
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}