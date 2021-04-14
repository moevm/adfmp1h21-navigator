package com.example.androidnavigatorleti.ui.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.history_item.view.*

class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(
        item: String?,
        position: Int,
        nextClick: (position: Int) -> Unit,
        deleteClick: (position: Int) -> Unit
    ) {
        with(itemView) {
            isClickable = true
            isFocusable = true

            next.setOnClickListener { nextClick.invoke(position) }
            del.setOnClickListener { deleteClick.invoke(position) }
            address.text = item ?: ""
        }
    }
}