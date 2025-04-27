package com.mine.flowpay.app

import android.app.Application
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.mine.flowpay.data.Users
import com.mine.flowpay.data.database.AppDatabase
import com.mine.flowpay.data.utils.DatabaseInitializer
import com.mine.flowpay.utils.SessionManager

class FlowpayApp : Application() {
    lateinit var sessionManager: SessionManager
    private lateinit var prefs: SharedPreferences
    lateinit var database: AppDatabase
        private set

    private val handler = Handler(Looper.getMainLooper())
    private val dbInspectorRunnable = object : Runnable {
        override fun run() {
            database.keepDatabaseAlive()
            handler.postDelayed(this, 2000) // Run every 2 seconds
        }
    }

    var loggedInuser: Users? = null
        set(value) {
            field = value
            if (value != null) {
                sessionManager.saveLoginState(value.user_id)
            } else {
                sessionManager.clearLoginState()
            }
        }

    var isLoggedIn: Boolean = false
        get() = loggedInuser != null

    override fun onCreate() {
        super.onCreate()
        sessionManager = SessionManager(this)
        database = AppDatabase.getInstance(this)
        prefs = getSharedPreferences("flowpay_prefs", MODE_PRIVATE)

        // Initialize categories and products
        DatabaseInitializer.initializeCategories(database.categoryDao())
        DatabaseInitializer.initializeProducts(database.productDao(), database.categoryDao())
        DatabaseInitializer.initializeAutoAccount(database.userDao())

        // Restore login state
        val userId = sessionManager.getUserId()
        if (userId != -1L && sessionManager.isLoggedIn()) {
            loggedInuser = database.userDao().getUserById(userId)
        }

        // Start periodic database check for Database Inspector
        handler.post(dbInspectorRunnable)
    }

    override fun onTerminate() {
        super.onTerminate()
        // Remove callbacks when app is terminated
        handler.removeCallbacks(dbInspectorRunnable)
    }
} 
