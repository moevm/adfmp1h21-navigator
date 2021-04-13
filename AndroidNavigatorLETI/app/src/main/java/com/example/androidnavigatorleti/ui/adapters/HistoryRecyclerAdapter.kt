package com.example.androidnavigatorleti.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.data.SearchHistoryItem
import com.example.androidnavigatorleti.ui.viewholders.HistoryViewHolder

/** Адаптер для recycler из фрагмента viewPager-а с новостями, акциями, фильмами каро и т.п. на главном экране */
class HistoryRecyclerAdapter(private val nextClick: () -> Unit, private val deleteClick: () -> Unit) :
    RecyclerView.Adapter<HistoryViewHolder>() {

    private val items: MutableList<SearchHistoryItem> = mutableListOf()

    override fun getItemCount(): Int = items.size

    fun setTitleData(newList: List<SearchHistoryItem>) {
        items.clear()

        if (newList.isNotEmpty()) {
            items.addAll(newList)
        }

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(items[position].place, nextClick, deleteClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
            HistoryViewHolder(LayoutInflater.from(parent.context).inflate(
                    R.layout.history_item,
                    parent,
                    false
            ))
}