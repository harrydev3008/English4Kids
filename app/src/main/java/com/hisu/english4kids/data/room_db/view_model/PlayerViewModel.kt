package com.hisu.english4kids.data.room_db.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.data.room_db.repository.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayerViewModel(val playerRepository: PlayerRepository) : ViewModel()  {

    fun getPlayerInfo(id: String) = playerRepository.getPlayerInfo(id)

    fun insertPlayer(player: Player) = viewModelScope.launch(Dispatchers.IO) {
        if(player.lastClaimdDate.isNullOrEmpty()) {
            player.lastClaimdDate = ""
        }

        playerRepository.insertPlayer(player)
    }

    fun updatePlayer(player: Player) = viewModelScope.launch(Dispatchers.IO) {
        if(player.lastClaimdDate.isNullOrEmpty()) {
            player.lastClaimdDate = ""
        }

        playerRepository.updatePlayer(player)
    }
}