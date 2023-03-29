package com.hisu.imastermatcher.ui.play_style

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.LayoutItemCardImageBinding
import com.hisu.imastermatcher.model.AudioImageMatchingModel

class MatchingAudioImageAdapter(
    var context: Context,
    val itemTapListener: (item: AudioImageMatchingModel) -> Unit
) : RecyclerView.Adapter<MatchingAudioImageAdapter.CardImageViewHolder>() {

    var images = listOf<AudioImageMatchingModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardImageViewHolder {
        return CardImageViewHolder(
            LayoutItemCardImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CardImageViewHolder, position: Int) {
        val image = images[position]

        holder.binding.apply {

            imvAnswerImage.setImageResource(image.imageUrl)
            tvAnswer.text = image.answer
            cardContainer.setOnClickListener {
                cardContainer.background = ContextCompat.getDrawable(context, R.drawable.bg_word_border_selected_image_blue)
                itemTapListener.invoke(image)
            }
        }
    }

    override fun getItemCount(): Int = images.size

    inner class CardImageViewHolder(var binding: LayoutItemCardImageBinding) :
        RecyclerView.ViewHolder(binding.root)
}