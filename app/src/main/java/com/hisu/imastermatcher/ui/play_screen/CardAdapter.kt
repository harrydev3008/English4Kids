package com.hisu.imastermatcher.ui.play_screen

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.LayoutItemCardBinding
import com.hisu.imastermatcher.model.card.Card

class CardAdapter(val itemTapListener: (card: Card, img: View) -> Unit) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    var items = listOf<Card>()
    private val holders = mutableListOf<CardViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(
            LayoutItemCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = items[position]
        holders.add(holder)

        holder.binding.apply {

//            rimvCoverImage.setImageResource(item.imagePath)

//            Glide.with(con)
//                .asBitmap().load(card.imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(object : SimpleTarget<Bitmap>() {
//                    override fun onResourceReady(
//                        resource: Bitmap,
//                        transition: Transition<in Bitmap>?
//                    ) {
//                        image.setImageBitmap(resource)
//                    }
//                })

            if (item.id.equals("-1")) {
                rimvCoverImage.visibility = View.INVISIBLE
            } else {
                rimvCoverImage.visibility = View.VISIBLE
                rimvCoverImage.setOnClickListener {
                    itemTapListener.invoke(item, rimvCoverImage)
                }
            }
        }
    }

    public fun hideCards() {
        holders.forEach {
            flipCard(it.binding.rimvCoverImage)
        }
    }

    private fun flipCard(image: ImageView) {
        val oa1 = ObjectAnimator.ofFloat(image, "scaleX", 1f, 0f)
        val oa2 = ObjectAnimator.ofFloat(image, "scaleX", 0f, 1f)

        oa1.interpolator = DecelerateInterpolator()
        oa2.interpolator = DecelerateInterpolator()

        oa1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)

                image.setImageResource(R.drawable.placeholder)

                oa2.start()
            }
        })

        oa1.start()
        oa1.duration = 200
        oa2.duration = 200
    }

    override fun getItemCount(): Int = items.size

    inner class CardViewHolder(val binding: LayoutItemCardBinding) :
        RecyclerView.ViewHolder(binding.root)
}