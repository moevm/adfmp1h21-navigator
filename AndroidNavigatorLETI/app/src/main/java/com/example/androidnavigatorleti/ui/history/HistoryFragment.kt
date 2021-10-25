package com.example.androidnavigatorleti.ui.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.ui.base.BaseFragment
import com.example.androidnavigatorleti.ui.base.EmptyViewState
import com.example.androidnavigatorleti.ui.adapters.HistoryRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : BaseFragment<HistoryViewModel, EmptyViewState>(R.layout.fragment_history) {

    override val viewModel: HistoryViewModel by viewModels()

    private val historyRecyclerAdapter by lazy {
        HistoryRecyclerAdapter(
            nextClick = ::onNextClick,
            deleteClick = ::onDeleteClick
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.getUser() == null) {
            openFragment(R.id.unregistered)
        } else {
            with(recyclerView) {
                viewModel.getSearchHistory().let { data ->
                    if (data.isEmpty()) {
                        no_items_text?.visibility = View.VISIBLE
                    } else {
                        no_items_text?.visibility = View.GONE
                        hasFixedSize()
                        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                            context,
                            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                            false
                        )

                        adapter = historyRecyclerAdapter.apply {
                            setTitleData(data)
                        }
                    }
                }
            }
        }
    }

    override fun renderState(state: EmptyViewState) {
    }

    private fun onNextClick(position: Int) {
        val direction = HistoryFragmentDirections.actionRefreshSearch(
            historyRecyclerAdapter.getItemTitle(position) ?: ""
        )
        openFragment(direction)
    }

    private fun onDeleteClick(position: Int) {
        with(viewModel) {
            with(historyRecyclerAdapter) {
                val list = getSearchHistory()
                list.forEach {
                    if (it.place == getItemTitle(position) ?: "") deleteItem(it)
                }
                deleteItemFromList(position)
                if (itemCount == 0) no_items_text?.visibility = View.VISIBLE
            }
        }
    }
}