package com.hisu.imastermatcher.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.LayoutDailyRewardBinding
import com.hisu.imastermatcher.model.daily_reward.Reward

class DailyRewardAdapter(
    var context: Context
) : RecyclerView.Adapter<DailyRewardAdapter.DailyRewardViewHolder>() {

    var rewards = listOf<Reward>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyRewardViewHolder {
        return DailyRewardViewHolder(
            LayoutDailyRewardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: DailyRewardViewHolder, position: Int) {
        val todayReward = rewards[position]

        holder.bindData(todayReward)
    }

    override fun getItemCount(): Int = rewards.size

    inner class DailyRewardViewHolder(val binding: LayoutDailyRewardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(todayReward: Reward) = binding.apply {

            tvReward.text = String.format(
                context.getString(R.string.daily_reward_price_pattern), todayReward.reward
            )

            tvDate.text =
                String.format(context.getString(R.string.daily_reward_date_pattern), todayReward.id)

            if (todayReward.isClaimed || todayReward.isClaimable) {

                if (todayReward.isClaimed)
                    changeContainerColor(R.color.reward_collected, true)
                else
                    changeContainerColor(R.color.reward_not_collect)

            } else
                changeContainerColor(R.color.gray_af)

        }

        private fun changeContainerColor(color: Int, isClaimed: Boolean = false) = binding.apply {
            tvDate.setBackgroundColor(ContextCompat.getColor(context, color))
            dailyParentContainer.strokeColor = ContextCompat.getColor(context, color)
            coinContainer.setBackgroundColor(ContextCompat.getColor(context, color))

            if (isClaimed)
                imvRewardCover.setImageResource(R.drawable.bg_chests_complete)
        }
    }
}
