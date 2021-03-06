package com.nikola.jakshic.dagger.leaderboard

import androidx.lifecycle.LiveData
import com.nikola.jakshic.dagger.common.network.OpenDotaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepository @Inject constructor(
    private val dao: LeaderboardDao,
    private val service: OpenDotaService
) {

    /**
     * Constructs the [LiveData] which emits every time
     * the requested data in the database has changed
     */
    fun getLeaderboardLiveData(region: String): LiveData<List<Leaderboard>> {
        return dao.getLeaderboard(region)
    }

    /**
     * Fetches the leaderboard from the network, takes the first 100 players
     * and inserts them into database.
     *
     * Whenever the database is updated, the observers of [LiveData]
     * returned by [getLeaderboardLiveData] are notified.
     */
    suspend fun fetchLeaderboard(region: String, onSuccess: () -> Unit, onError: () -> Unit) {
        try {
            withContext(Dispatchers.IO) {
                val leaderboard = service.getLeaderboard(region).leaderboard
                    ?: throw Exception()
                val list = leaderboard.take(100)
                list.map {
                    it.region = region // response from the network doesn't contain any information
                    it // about the region, so we need to set this manually
                }
                if (list.isNotEmpty()) {
                    // We don't have players ids, we only have their names,
                    // if the player has changed his name in the meantime,
                    // that would result into 2 rows in the database for a single player.
                    // So we need to remove all players from the database and then insert
                    // the fresh ones.
                    dao.deleteLeaderboards(region)
                    dao.insertLeaderboard(list)
                }
            }
            onSuccess()
        } catch (e: Exception) {
            onError()
        }
    }
}