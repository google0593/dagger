package com.nikola.jakshic.dagger.profile.heroes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nikola.jakshic.dagger.DaggerApp
import com.nikola.jakshic.dagger.R
import com.nikola.jakshic.dagger.common.DaggerViewModelFactory
import com.nikola.jakshic.dagger.common.Status
import com.nikola.jakshic.dagger.common.hasNetworkConnection
import com.nikola.jakshic.dagger.common.inflate
import com.nikola.jakshic.dagger.common.toast
import com.nikola.jakshic.dagger.profile.matches.byhero.MatchesByHeroActivity
import kotlinx.android.synthetic.main.fragment_hero.*
import javax.inject.Inject

class HeroFragment : Fragment(), HeroSortDialog.OnSortListener {

    @Inject lateinit var factory: DaggerViewModelFactory
    private var id: Long = -1
    private lateinit var viewModel: HeroViewModel
    private lateinit var adapter: HeroAdapter

    override fun onAttach(context: Context?) {
        (activity?.application as DaggerApp).appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return container?.inflate(R.layout.fragment_hero)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this, factory)[HeroViewModel::class.java]

        id = activity?.intent?.getLongExtra("account_id", -1) ?: -1

        viewModel.initialFetch(id)

        adapter = HeroAdapter {
            val intent = Intent(context, MatchesByHeroActivity::class.java).apply {
                putExtra("account_id", id)
                putExtra("hero_id", it)
            }
            startActivity(intent)
        }

        recView.layoutManager = LinearLayoutManager(context)
        recView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recView.adapter = adapter
        recView.setHasFixedSize(true)

        viewModel.list.observe(this, Observer(adapter::addData))
        viewModel.status.observe(this, Observer {
            when (it) {
                Status.LOADING -> swipeRefresh.isRefreshing = true
                else -> swipeRefresh.isRefreshing = false
            }
        })

        val sortDialog = HeroSortDialog()
        sortDialog.setTargetFragment(this, 301)

        btnSort.setOnClickListener {
            if (!sortDialog.isAdded) sortDialog.show(fragmentManager, null)
        }

        swipeRefresh.setOnRefreshListener {
            if (hasNetworkConnection())
                viewModel.fetchHeroes(id)
            else {
                toast(getString(R.string.error_network_connection))
                swipeRefresh.isRefreshing = false
            }
        }
    }

    override fun onSort(sort: Int) {
        // Remove previous observers b/c we are attaching new LiveData
        viewModel.list.removeObservers(this)

        when (sort) {
            0 -> viewModel.sortByGames(id)
            1 -> viewModel.sortByWinRate(id)
            2 -> viewModel.sortByWins(id)
            3 -> viewModel.sortByLosses(id)
        }
        // Set to null first, to delete all the items otherwise the list wont be scrolled to the first item
        adapter.addData(null)
        // Attach the observer to the new LiveData
        viewModel.list.observe(this, Observer(adapter::addData))
    }
}