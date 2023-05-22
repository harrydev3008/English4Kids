package com.hisu.english4kids.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.game_play.Gameplay
import com.hisu.english4kids.databinding.LayoutGameplayProgressItemBinding
import com.hisu.english4kids.widget.dialog.MessageDialog

class GameplayProgressAdapter(
    var context: Context,
    private val gameplayClickListener: (position: Int, status: Int) -> Unit
) : RecyclerView.Adapter<GameplayProgressAdapter.GameplayProgressViewHolder>() {

    var gameplays = listOf<Gameplay>()
    private val zigZagPaddingArr = listOf(0.05f, 0.2f, 0.4f, 0.6f, 0.6f, 0.4f, 0.2f, 0.05f)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameplayProgressViewHolder {
        return GameplayProgressViewHolder(
            LayoutGameplayProgressItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GameplayProgressViewHolder, position: Int) {
        val gameplay = gameplays[position]
        holder.bindData(gameplay, position)
    }

    override fun getItemCount(): Int = gameplays.size

    inner class GameplayProgressViewHolder(var binding: LayoutGameplayProgressItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bindData(gameplay: Gameplay, position: Int) = binding.apply {

                when (gameplay.status) {
                    0 -> {
                        outerCircle.background = ContextCompat.getDrawable(context, R.drawable.bg_circle_blue)
                        innerImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_solid))
                    }
                    1 -> {
                        outerCircle.background = ContextCompat.getDrawable(context, R.drawable.bg_circle)
                        innerImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_done_rounded))
                    }
                    2 -> {
                        outerCircle.background = ContextCompat.getDrawable(context, R.drawable.bg_circle_red)
                        innerImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_alert))
                    }
                    else -> {
                        outerCircle.background = ContextCompat.getDrawable(context, R.drawable.bg_circle_black)
                        innerImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_round_lock))
                    }
                }

                guideline.setGuidelinePercent(zigZagPaddingArr[position % 8])

                outerCircle.setOnClickListener {
                    if(gameplay.status != -1) {
                        gameplayClickListener.invoke(position, gameplay.status)
                    } else {
                        MessageDialog(context, context.getString(R.string.round_locked_desc), context.getString(R.string.round_locked), true).showDialog()
                    }
                }
            }
        }
}