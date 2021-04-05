package com.example.androidnavigatorleti.ui.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.history_item.view.*

class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(
        item: String,
        nextClick: () -> Unit,
        deleteClick: () -> Unit
    ) {
        with(itemView) {
            isClickable = true
            isFocusable = true

            next.setOnClickListener { nextClick.invoke() }
            del.setOnClickListener { deleteClick.invoke() }
            address.text = item
        }
    }
}