package com.example.androidnavigatorleti.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.androidnavigatorleti.R
import com.example.androidnavigatorleti.base.BaseFragment
import com.example.androidnavigatorleti.ui.adapters.HistoryRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : BaseFragment() {

    private val historyRecyclerAdapter by lazy {
        HistoryRecyclerAdapter(
            nextClick = ::onNextClick,
            deleteClick = ::onDeleteClick
        )
    }

    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_history, container, false)

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