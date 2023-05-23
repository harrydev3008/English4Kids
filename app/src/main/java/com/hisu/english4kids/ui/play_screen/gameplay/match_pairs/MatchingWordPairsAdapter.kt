package com.hisu.english4kids.ui.play_screen.gameplay.match_pairs

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hisu.english4kids.MainActivity
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.card.Card
import com.hisu.english4kids.databinding.LayoutAudioWordMatchingBinding
import com.hisu.english4kids.databinding.LayoutWordMatchingBinding
import java.lang.ref.WeakReference

class MatchingWordPairsAdapter(
    private var weakActivity: WeakReference<Activity>,
    var context: Context,
    private val itemTapListener: (item: Card, position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val WORD_PAIRS_TYPE = 1
    private val AUDIO_WORD_PAIRS_TYPE = 2

    var pairs = listOf<Card>()
    private var holders = mutableListOf<RecyclerView.ViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            WORD_PAIRS_TYPE -> WordPairViewHolder(
                LayoutWordMatchingBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            else -> AudioWordPairViewHolder(
                LayoutAudioWordMatchingBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

    override fun getItemViewType(position: Int): Int {
        return if (pairs[position].isAudio) AUDIO_WORD_PAIRS_TYPE else WORD_PAIRS_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holders.add(holder)
        val pair = pairs[position]

        when (holder) {
            is WordPairViewHolder -> {

                holder.apply {
                    bindData(pair)
                    binding.cardContainer.setOnClickListener {

                        binding.cardContainer.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.bg_word_border_selected_image_blue
                            )
                        binding.tvAnswer.setTextColor(context.getColor(R.color.classic))

                        itemTapListener.invoke(pair, position)
                    }
                }
            }

            is AudioWordPairViewHolder -> {
                holder.binding.apply {

                    levelParent.setOnClickListener {
                        (weakActivity.get() as MainActivity).playAudio(pair.answer)

                        levelParent.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.bg_word_border_selected_image_blue
                            )
                        itemTapListener.invoke(pair, position)
                    }
                }
            }
        }
    }

    fun correctPairsSelected(position: Int) {
        if (holders[position] is WordPairViewHolder) {
            (holders[position] as WordPairViewHolder).binding.apply {

                cardContainer.background = ContextCompat.getDrawable(context, R.drawable.bg_word_border_selected_green)
                tvAnswer.setTextColor(context.getColor(R.color.text_correct))

                Handler(context.mainLooper).postDelayed({
                    cardContainer.background = ContextCompat.getDrawable(context, R.drawable.bg_word_border_light_gray)
                    tvAnswer.setTextColor(context.getColor(R.color.gray_af))
                }, 500)

                cardContainer.isClickable = false
                cardContainer.setOnClickListener(null)
            }
        } else {
            (holders[position] as AudioWordPairViewHolder).binding.apply {

                levelParent.background = ContextCompat.getDrawable(context, R.drawable.bg_word_border_selected_green)

                Handler(context.mainLooper).postDelayed({
                    levelParent.background = ContextCompat.getDrawable(context, R.drawable.bg_word_border_light_gray)
                    imvPlayAudio.setImageResource(R.drawable.ic_round_volume_up_24_gray)
                }, 500)

                levelParent.isClickable = false
                levelParent.setOnClickListener(null)
            }
        }
    }

    fun resetPairsSelected(position: Int) {
        if (holders[position] is WordPairViewHolder) {
            (holders[position] as WordPairViewHolder).binding.apply {
                cardContainer.background = ContextCompat.getDrawable(context, R.drawable.bg_word_border)
                tvAnswer.setTextColor(context.getColor(R.color.gray))
            }
        } else {
            (holders[position] as AudioWordPairViewHolder).binding.apply {
                levelParent.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_word_border)
            }
        }
    }

    override fun getItemCount(): Int = pairs.size

    inner class WordPairViewHolder(var binding: LayoutWordMatchingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: Card) = binding.apply {
            tvAnswer.text = data.answer
        }
    }

    inner class AudioWordPairViewHolder(var binding: LayoutAudioWordMatchingBinding) :
        RecyclerView.ViewHolder(binding.root)
}