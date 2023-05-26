package com.hisu.english4kids.data.room_db.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hisu.english4kids.data.room_db.repository.PlayerRepository

class PlayerViewModelProviderFactory(private val playerRepository: PlayerRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PlayerViewModel::class.java))
            return PlayerViewModel(playerRepository) as T
        throw IllegalArgumentException("Unknown Class Name Exception")
    }
}