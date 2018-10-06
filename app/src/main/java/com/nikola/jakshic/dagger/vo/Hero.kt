package com.nikola.jakshic.dagger.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "heroes", primaryKeys = arrayOf("account_id", "hero_id"))
data class Hero(
        @ColumnInfo(name = "account_id") var accountId: Long,
        @ColumnInfo(name = "hero_id") @SerializedName("hero_id") @Expose var heroId: Int,
        @ColumnInfo(name = "games") @SerializedName("games") @Expose var gamesPlayed: Int,
        @ColumnInfo(name = "wins") @SerializedName("win") @Expose var gamesWon: Int)