package com.nikola.jakshic.dagger.viewModel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.nikola.jakshic.dagger.Status
import com.nikola.jakshic.dagger.data.local.LeaderboardDao
import com.nikola.jakshic.dagger.model.Leaderboard
import com.nikola.jakshic.dagger.repository.LeaderboardRepository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class RegionViewModel @Inject constructor(private val repository: LeaderboardRepository,
                                          private val dao: LeaderboardDao) : ViewModel() {

    lateinit var list: LiveData<MutableList<Leaderboard>>
        private set
    private var initialFetch = false
    private val compositeDisposable = CompositeDisposable()
    val status = MutableLiveData<Status>()

    fun initialFetch(region: String?) {
        if (!initialFetch) {
            initialFetch = true
            list = dao.getLeaderboard(region)
            fetchLeaderboard(region)
        }
    }

    fun fetchLeaderboard(region: String?) {
        compositeDisposable.add(repository.fetchData(region, status))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}