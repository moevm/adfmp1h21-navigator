package com.example.androidnavigatorleti.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.data.domain.SearchHistoryItem
import com.example.androidnavigatorleti.ui.viewholders.HistoryViewHolder

/** Адаптер для recycler из фрагмента viewPager-а с новостями, акциями, фильмами каро и т.п. на главном экране */
class HistoryRecyclerAdapter(private val nextClick: (position: Int) -> Unit, private val deleteClick: (position: Int) -> Unit) :
    RecyclerView.Adapter<HistoryViewHolder>() {

    private val itemDatabases: MutableList<SearchHistoryItem> = mutableListOf()

    override fun getItemCount(): Int = itemDatabases.size

    fun setTitleData(newList: List<SearchHistoryItem>) {
        itemDatabases.clear()

        if (newList.isNotEmpty()) {
            itemDatabases.addAll(newList)
        }

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(itemDatabases.getOrNull(position)?.place, position, nextClick, deleteClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
            HistoryViewHolder(LayoutInflater.from(parent.context).inflate(
                    R.layout.history_item,
                    parent,
                    false
            ))

    fun getItemTitle(position: Int) = itemDatabases.getOrNull(position)?.place

    fun deleteItemFromList(position: Int) {
        itemDatabases.removeAt(position)
        notifyDataSetChanged()
    }
}