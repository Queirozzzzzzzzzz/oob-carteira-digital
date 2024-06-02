package com.oob.carteira_digital.models

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.oob.carteira_digital.BaseActivity
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

    @SuppressLint("ViewHolder", "SuspiciousIndentation")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val listItemView = inflater.inflate(R.layout.list_item, parent, false)
        val cardView = listItemView.findViewById<androidx.cardview.widget.CardView>(R.id.listItemCardView)

        val timeText = listItemView.findViewById<TextView>(R.id.timeText)
        val messageTitle = listItemView.findViewById<TextView>(R.id.messageTitle)

        val notification = notifications[position]

        timeText.text = notification["created_at"].toString()

        if(notification["read"] == "1") {
            cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.read_message_bg))
        }

        val oTitle = notification["title"].toString()
        val content = notification["content"].toString()

        var title = notification["title"].toString()
        val titleParts = title.split("\n")

            if(titleParts[0].length < 25 && titleParts.size > 1) {
                title = titleParts[0] + "..."
            }


        if(title.length >  25) {
            title = title.substring(0, 24) + "..."
        }

        messageTitle.text = title

        listItemView.setOnClickListener {
            val messageId = notification["id"].toString()
            (mContext as BaseActivity).openMessage(messageId, oTitle, content, listItemView)
        }

        return listItemView
    }

    fun notifyDataChanged() {
        notifyDataSetChanged()
    }
}