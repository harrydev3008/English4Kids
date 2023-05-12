package com.hisu.english4kids.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.game_play.Gameplay
import com.hisu.english4kids.databinding.LayoutGameplayProgressItemBinding

class GameplayProgressAdapter(
    var context: Context,
    private val gameplayClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<GameplayProgressAdapter.GameplayProgressViewHolder>() {

    var gameplays = listOf<Gameplay>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameplayProgressViewHolder {
        return GameplayProgressViewHolder(
            LayoutGameplayProgressItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GameplayProgressViewHolder, position: Int) {
        val gameplay = gameplays[position]
//        holder.binding.tvRound.text = "${position + 1}"

        if(gameplay.status == 1) {
            holder.binding.outerCircle.background = ContextCompat.getDrawable(context, R.drawable.bg_circle)
            holder.binding.innerImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_done_rounded))
        } else if(gameplay.status == 0) {
            holder.binding.outerCircle.background = ContextCompat.getDrawable(context, R.drawable.bg_circle_blue)
            holder.binding.innerImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_solid))
        } else {
            holder.binding.outerCircle.background = ContextCompat.getDrawable(context, R.drawable.bg_circle_black)
            holder.binding.innerImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_round_lock))
        }

        holder.binding.parentContainer.setOnClickListener {
            gameplayClickListener.invoke(position)
        }
//        holder.bindData()
    }

    override fun getItemCount(): Int = gameplays.size

    inner class GameplayProgressViewHolder(var binding: LayoutGameplayProgressItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData() {

        }
    }
}