package com.hisu.english4kids.ui.gameplay.progress

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.LayoutGameplayProgressItemBinding

class GameplayProgressAdapter(
    var context: Context,
    private val gameplayClickListener: () -> Unit
) : RecyclerView.Adapter<GameplayProgressAdapter.GameplayProgressViewHolder>() {

    var gameplays = listOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameplayProgressViewHolder {
        return GameplayProgressViewHolder(
            LayoutGameplayProgressItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GameplayProgressViewHolder, position: Int) {
//        val gameplay = gameplays[position]
//        holder.binding.tvRound.text = "${position + 1}"
        holder.binding.parentContainer.setOnClickListener {
            gameplayClickListener.invoke()
        }
//        holder.bindData()
    }

    override fun getItemCount(): Int = 20

    inner class GameplayProgressViewHolder(var binding: LayoutGameplayProgressItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData() {

        }
    }
}