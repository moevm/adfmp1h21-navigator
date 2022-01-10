package com.example.androidnavigatorleti.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.domain.model.SearchHistoryItem
import com.example.androidnavigatorleti.presentation.history.viewholders.HistoryViewHolder

/** Адаптер для recycler из фрагмента viewPager-а с новостями, акциями, фильмами каро и т.п. на главном экране */
class HistoryRecyclerAdapter(
    private val nextClick: (position: Int) -> Unit,
    private val deleteClick: (position: Int) -> Unit
) : RecyclerView.Adapter<HistoryViewHolder>() {

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
        holder.bind(items.getOrNull(position)?.place, position, nextClick, deleteClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
            HistoryViewHolder(LayoutInflater.from(parent.context).inflate(
                    R.layout.history_item,
                    parent,
                    false
            ))

    fun getItem(position: Int): SearchHistoryItem? = items.getOrNull(position)

    fun deleteItemFromList(position: Int) {
        items.removeAt(position)
        notifyDataSetChanged()
    }
}