package com.example.androidnavigatorleti.presentation.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.common.base.BaseFragment
import com.example.androidnavigatorleti.presentation.adapters.HistoryRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : BaseFragment<HistoryViewModel, HistoryViewState>(R.layout.fragment_history) {

    override val viewModel: HistoryViewModel by viewModels()

    private val historyRecyclerAdapter by lazy {
        HistoryRecyclerAdapter(
            nextClick = ::onNextClick,
            deleteClick = ::onDeleteClick
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(recyclerView) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                context,
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                false
            )
            adapter = historyRecyclerAdapter.apply {
                setTitleData(it)
            }
        }
    }

    override fun renderState(state: HistoryViewState) {
        if (state.userInfo == null || state.userInfo.name.isEmpty()) {
            openFragment(R.id.unregistered)
        } else {
            with(recyclerView) {
                state.history?.let {
                    if (it.isNotEmpty()) {
                        no_items_text?.visibility = View.GONE

                    } else {
                        no_items_text?.visibility = View.VISIBLE
                    }
                }
            }
        }

        if (state.isDeleted) {
            with(historyRecyclerAdapter) {
                state.history?.forEach {
                    if (it.place == getItem(position) ?: "") deleteItem(it)
                }

                val list = getSearchHistory()
                list.forEach {
                    if (it.place == getItem(position) ?: "") deleteItem(it)
                }
                deleteItemFromList(position)
                if (itemCount == 0) no_items_text?.visibility = View.VISIBLE
            }
        }
    }

    private fun onNextClick(position: Int) {
        val direction = HistoryFragmentDirections.actionRefreshSearch(
            historyRecyclerAdapter.getItem(position)?.place.orEmpty()
        )
        openFragment(direction)
    }

    private fun onDeleteClick(position: Int) {
        viewModel.deleteItem(historyRecyclerAdapter.getItem(position))
    }
}