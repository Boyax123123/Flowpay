package com.mine.flowpay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mails)

        val rv = findViewById<RecyclerView>(R.id.rvMails)
        rv.layoutManager = LinearLayoutManager(this)

        // Fetch data from the database
        val db = AppDatabase.getInstance(this)
        val dao = db.mailDao()

        CoroutineScope(Dispatchers.IO).launch {
            val mails = dao.getAllMails()
            withContext(Dispatchers.Main) {
                rv.adapter = MailAdapter(mails.map { it.subject })
            }
        }
    }
}
