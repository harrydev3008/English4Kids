package com.hisu.english4kids.ui.play_screen.gameplay.class_pairs_matching

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.hisu.english4kids.data.model.card.Card
import com.hisu.english4kids.databinding.LayoutItemCardBinding

class CardAdapter(
    var context: Context,
    val itemTapListener: (card: Card, frontCard: LinearLayout, backCard: TextView, parent: RelativeLayout) -> Unit
) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    var cards = listOf<Card>()
    private val holders = mutableListOf<CardViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(
            LayoutItemCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        if (card.isVisible)
            holders.add(holder)

        holder.bindData(card)
    }

    fun hideCards() {
        holders.forEach {
            it.binding.cardParent.isClickable = true
            it.binding.cardBack.visibility = View.VISIBLE
            it.binding.cardFront.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int = cards.size

    inner class CardViewHolder(val binding: LayoutItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(card: Card) = binding. apply{
            val size =
                if (itemCount > 4)
                    (context.resources.displayMetrics.widthPixels * 0.30).toInt()
                else
                    (context.resources.displayMetrics.widthPixels * 0.40).toInt()

            val params = RelativeLayout.LayoutParams(size, size)

            params.setMargins(
                0,
                0,
                context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._4sdp),
                context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._4sdp)
            )

            cardParent.layoutParams = params

            Glide.with(context)
                .asBitmap().load(card.imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        rimvCoverImage.setImageBitmap(resource)
                    }
                })

            tvWord.text = card.word

            if (card.cardId == "-1" || !card.isVisible) {
                cardBack.visibility = View.INVISIBLE
                cardFront.visibility = View.INVISIBLE
            } else {
                rimvCoverImage.visibility = View.VISIBLE
                cardParent.setOnClickListener { itemTapListener.invoke(card, cardFront, cardBack, cardParent) }
                cardParent.isClickable = false
            }
        }
    }
}