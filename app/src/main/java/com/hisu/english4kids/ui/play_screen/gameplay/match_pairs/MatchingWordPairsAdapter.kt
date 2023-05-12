package com.hisu.english4kids.ui.play_screen.gameplay.match_pairs

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.card.Card
import com.hisu.english4kids.databinding.LayoutAudioWordMatchingBinding
import com.hisu.english4kids.databinding.LayoutWordMatchingBinding


class MatchingWordPairsAdapter(
    var context: Context,
    private val itemTapListener: (item: Card, position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val WORD_PAIRS_TYPE = 1
    private val AUDIO_WORD_PAIRS_TYPE = 2

    private val mediaPlayer: MediaPlayer = MediaPlayer()

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
                        playAudio(pair.answer)

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

    private fun playAudio(audio: String) {
        /*
        * when the previous audio is playing, stop the prev one, reset the state then play current
        * audio file. Without .reset() wont work
        */
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.setDataSource(context, Uri.parse(audio))
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    fun correctPairsSelected(position: Int) {
        if (holders[position] is WordPairViewHolder) {
            (holders[position] as WordPairViewHolder).binding.apply {
                cardContainer.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_word_border_light_gray)
                tvAnswer.setTextColor(context.getColor(R.color.gray_af))

                cardContainer.isClickable = false
                cardContainer.setOnClickListener(null)
            }
        } else {
            (holders[position] as AudioWordPairViewHolder).binding.apply {
                levelParent.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_word_border_light_gray)

                imvPlayAudio.setImageResource(R.drawable.ic_round_volume_up_24_gray)

                levelParent.isClickable = false
                levelParent.setOnClickListener(null)
            }
        }
    }

    fun resetPairsSelected(position: Int) {
        if (holders[position] is WordPairViewHolder) {
            (holders[position] as WordPairViewHolder).binding.apply {
                cardContainer.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_word_border)
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