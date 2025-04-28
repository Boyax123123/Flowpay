package com.mine.flowpay

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MailAdapter(private val subjects: List<String>) : RecyclerView.Adapter<MailAdapter.MailViewHolder>() {

    class MailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val subjectView: TextView = view.findViewById(R.id.tv_mail_subject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mail, parent, false)
        return MailViewHolder(view)
    }

    override fun onBindViewHolder(holder: MailViewHolder, position: Int) {
        holder.subjectView.text = subjects[position]
    }

    override fun getItemCount() = subjects.size
}
