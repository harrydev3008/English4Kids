package com.hisu.imastermatcher.ui.play_style.match_pairs

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.LayoutWordMatchingBinding
import com.hisu.imastermatcher.model.PairMatchingModel

class MatchingWordPairsAdapter(
    var context: Context,
    private val itemTapListener: (item: PairMatchingModel, position: Int) -> Unit
) : RecyclerView.Adapter<MatchingWordPairsAdapter.WordPairViewHolder>() {

    var pairs = listOf<PairMatchingModel>()
    private var holders = mutableListOf<WordPairViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordPairViewHolder {
        return WordPairViewHolder(
            LayoutWordMatchingBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: WordPairViewHolder, position: Int) {
        val pair = pairs[position]

        holders.add(holder)

        holder.apply {
            bindData(pair)
            binding.cardContainer.setOnClickListener {

                binding.cardContainer.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_word_border_selected_image_blue)
                binding.tvAnswer.setTextColor(context.getColor(R.color.classic))

                itemTapListener.invoke(pair, position)//todo: impl later
            }
        }
    }

    fun correctPairsSelected(position: Int) {
        holders[position].binding.apply {
            cardContainer.background =
                ContextCompat.getDrawable(context, R.drawable.bg_word_border_light_gray)
            tvAnswer.setTextColor(context.getColor(R.color.gray_af))
        }
    }

    fun resetPairsSelected(position: Int) {
        holders[position].binding.apply {
            cardContainer.background =
                ContextCompat.getDrawable(context, R.drawable.bg_word_border)
            tvAnswer.setTextColor(context.getColor(R.color.gray))
        }
    }

    override fun getItemCount(): Int = pairs.size

    inner class WordPairViewHolder(var binding: LayoutWordMatchingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: PairMatchingModel) = binding.apply {
            tvAnswer.text = data.answer
        }
    }
}