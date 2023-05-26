package com.hisu.english4kids.widget.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.daily_reward.Reward
import com.hisu.english4kids.databinding.LayoutDailyRewardBinding
import es.dmoral.toasty.Toasty

class DailyRewardAdapter(
    var context: Context,
    var claimReward: (reward: Reward) -> Unit
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
        holder.bindData(todayReward, position)
    }

    override fun getItemCount(): Int = rewards.size

    inner class DailyRewardViewHolder(val binding: LayoutDailyRewardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(todayReward: Reward, position: Int) = binding.apply {

            tvReward.text = String.format(context.getString(R.string.daily_reward_price_pattern), todayReward.reward)
            tvDate.text = String.format(context.getString(R.string.daily_reward_date_pattern), position + 1)

            when (todayReward.isClaimed) {
                1 -> {//claimed
                    changeContainerColor(R.color.reward_collected, true)
                    dailyParentContainer.setOnClickListener {
                        Toasty.error(context, context.getString(R.string.daily_reward_already_claimed), Toasty.LENGTH_SHORT, true).show()
                    }
                }
                0 -> {//not claimed
                    changeContainerColor(R.color.reward_not_collect)
                    dailyParentContainer.setOnClickListener {
                        claimReward.invoke(todayReward)
                    }
                }
                else -> {//locked
                    changeContainerColor(R.color.gray_af)
                    dailyParentContainer.setOnClickListener {
                        Toasty.error(context, context.getString(R.string.daily_reward_locked), Toasty.LENGTH_SHORT, true).show()
                    }
                }
            }
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
