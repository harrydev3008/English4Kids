package com.hisu.imastermatcher.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hisu.imastermatcher.databinding.LayoutDailyRewardBinding

class DailyRewardAdapter : RecyclerView.Adapter<DailyRewardAdapter.DailyRewardViewHolder>() {

    var rewards = listOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyRewardViewHolder {
        return DailyRewardViewHolder(
            LayoutDailyRewardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: DailyRewardViewHolder, position: Int) {
        val todayReward = rewards[position]

        holder.binding.apply {
            tvReward.text = todayReward
            tvDay.text = "Day ${position + 1}"
        }
    }

    override fun getItemCount(): Int = rewards.size

    inner class DailyRewardViewHolder(val binding: LayoutDailyRewardBinding) :
        RecyclerView.ViewHolder(binding.root)
}
