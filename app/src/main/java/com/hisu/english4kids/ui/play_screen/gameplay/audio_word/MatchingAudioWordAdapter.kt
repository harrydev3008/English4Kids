package com.hisu.english4kids.ui.play_screen.gameplay.audio_word

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.card.Card
import com.hisu.english4kids.databinding.LayoutItemCardWordBinding

class MatchingAudioWordAdapter(
    var context: Context,
    private val itemTapListener: (item: Card) -> Unit
) : RecyclerView.Adapter<MatchingAudioWordAdapter.CardWordViewHolder>() {

    var pairs = listOf<Card>()
    var isLockView = false

    private var prevPick: LinearLayout?= null
    private var curPick: LinearLayout?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardWordViewHolder {
        return CardWordViewHolder(
            LayoutItemCardWordBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CardWordViewHolder, position: Int) {
        val pair = pairs[position]

        holder.apply {
            bindData(pair)
            binding.cardContainer.setOnClickListener {

                if(!isLockView) {
                    curPick = binding.cardContainer

                    prevPick?.apply {
                        background = ContextCompat.getDrawable(context, R.drawable.bg_word_border)
                    }

                    curPick?.apply {
                        background = ContextCompat.getDrawable(context, R.drawable.bg_word_border_selected_image_blue)
                    }

                    prevPick = binding.cardContainer

                    itemTapListener.invoke(pair)
                }
            }
        }
    }

    override fun getItemCount(): Int = pairs.size

    inner class CardWordViewHolder(var binding: LayoutItemCardWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: Card) = binding.apply {
            tvAnswer.text = data.word
        }
    }
}