package com.nikola.jakshic.dagger.data.local

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nikola.jakshic.dagger.vo.Competitive

@Dao
interface CompetitiveDao{

    @Query("SELECT * FROM competitive ORDER BY start_time+duration DESC")
    fun getMatches(): DataSource.Factory<Int, Competitive>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatches(list: List<Competitive>)

    @Query("SELECT COUNT(match_id) FROM competitive")
    fun getCount(): Int
}