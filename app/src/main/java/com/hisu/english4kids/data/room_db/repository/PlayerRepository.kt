package com.hisu.english4kids.data.room_db.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.data.room_db.dao.PlayerDAO

class PlayerRepository(context: Context, private val playerDAO: PlayerDAO) {
    fun getPlayerInfo(id: String): LiveData<Player> = playerDAO.getPlayerInfo(id)

    suspend fun insertPlayer(player: Player) = playerDAO.insert(player)

    suspend fun updatePlayer(player: Player) = playerDAO.update(player)

    suspend fun deletePlayer(player: Player) = playerDAO.delete(player)
}