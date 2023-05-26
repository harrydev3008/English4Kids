package com.hisu.english4kids.data.room_db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hisu.english4kids.data.network.response_model.Player

@Dao
interface PlayerDAO {
    @Query("SELECT * FROM player WHERE id = :id")
    fun getPlayerInfo(id: String): LiveData<Player>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(player: Player)

    @Update
    suspend fun update(player: Player)

    @Query("DELETE FROM player")
    suspend fun dropPlayerTable()

    @Delete
    suspend fun delete(player: Player)
}