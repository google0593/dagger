package com.nikola.jakshic.dagger.profile.heroes

import androidx.lifecycle.LiveData
import com.nikola.jakshic.dagger.common.network.OpenDotaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeroRepository @Inject constructor(
    private val dao: HeroDao,
    private val service: OpenDotaService
) {

    /**
     * Constructs the [LiveData] which emits every time
     * the requested data in the database has changed
     */
    fun getHeroesLiveDataByGames(id: Long) = dao.getHeroesByGames(id)

    /**
     * Constructs the [LiveData] which emits every time
     * the requested data in the database has changed
     */
    fun getHeroesLiveDataByWinrate(id: Long) = dao.getHeroesByWinrate(id)

    /**
     * Constructs the [LiveData] which emits every time
     * the requested data in the database has changed
     */
    fun getHeroesLiveDataByWins(id: Long) = dao.getHeroesByWins(id)

    /**
     * Constructs the [LiveData] which emits every time
     * the requested data in the database has changed
     */
    fun getHeroesLiveDataByLosses(id: Long) = dao.getHeroesByLosses(id)

    /**
     * Fetches the heroes from the network and inserts them into database.
     *
     * Whenever the database is updated, the observers of [LiveData]
     * returned by [getHeroesLiveDataByGames], [getHeroesLiveDataByWins],
     * [getHeroesLiveDataByLosses] and [getHeroesLiveDataByWinrate] are notified.
     */
    suspend fun fetchHeroes(id: Long, onSuccess: () -> Unit, onError: () -> Unit) {
        try {
            withContext(Dispatchers.IO) {
                val heroes = service.getHeroes(id)
                heroes.map {
                    it.accountId = id // response from the network doesn't contain any information
                    it // about who played this heroes, so we need to set this manually
                }
                dao.insertHeroes(heroes)
            }
            onSuccess()
        } catch (e: Exception) {
            onError()
        }
    }
}