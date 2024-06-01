package com.oob.carteira_digital.models

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.oob.carteira_digital.R

class MessageListAdapter(context: Context, private val notifications: MutableList<Map<String, Any?>>): BaseAdapter() {

    private val mContext = context

    override fun getCount(): Int {
        return notifications.size
    }

    override fun getItem(p0: Int): Any {
        return p0.toLong()
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val listItemView = inflater.inflate(R.layout.list_item, parent, false)

        val timeText = listItemView.findViewById<TextView>(R.id.timeText)
        val messageText = listItemView.findViewById<TextView>(R.id.messageText)
        val notification = notifications[position]

        messageText.text = notification["content"].toString()
        timeText.text = notification["created_at"].toString()

        return listItemView
    }

    fun notifyDataChanged() {
        notifyDataSetChanged()
    }
}