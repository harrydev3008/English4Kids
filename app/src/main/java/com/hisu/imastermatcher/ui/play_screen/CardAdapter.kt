package com.hisu.imastermatcher.ui.play_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hisu.imastermatcher.databinding.LayoutItemCardBinding
import com.hisu.imastermatcher.model.Card

class CardAdapter(val itemTapListener: (card: Card, img: View) -> Unit) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    var items = listOf<Card>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(
            LayoutItemCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = items[position]

        holder.binding.apply {

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

    override fun getItemCount(): Int = items.size

    inner class CardViewHolder(val binding: LayoutItemCardBinding) :
        RecyclerView.ViewHolder(binding.root)
}