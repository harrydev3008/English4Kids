package com.hisu.imastermatcher.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.hisu.imastermatcher.databinding.LayoutDailyRewardsBinding
import com.hisu.imastermatcher.model.daily_reward.Reward

class DailyRewardDialog() {
    private lateinit var context: Context
    private var gravity: Int = 0
    private lateinit var dialog: Dialog
    private lateinit var binding: LayoutDailyRewardsBinding

    constructor(context: Context, gravity: Int): this() {
        this.context = context
        this.gravity = gravity

        initDialog()
    }

    private fun initDialog() {
        binding = LayoutDailyRewardsBinding.inflate(LayoutInflater.from(context), null, false)

        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)

        val window = dialog.window ?: return

        val width = (context.getResources().getDisplayMetrics().widthPixels*0.90).toInt()
        val height = (context.getResources().getDisplayMetrics().heightPixels*0.90)

        window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val windowAttr = window.attributes
        windowAttr.gravity = gravity
        window.attributes = windowAttr

        binding.rvDailyRewards.apply {

            val gridLayoutManager = GridLayoutManager(context, 3)

            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if(position == 6) return 3
                    return 1
                }
            }

            layoutManager = gridLayoutManager

            val rewardAdapter = DailyRewardAdapter(context)
            rewardAdapter.rewards = listOf(
                Reward(1, 10, isClaimed = true, isClaimable = true),
                Reward(2, 20, isClaimable =  true),
                Reward(3, 30),
                Reward(4, 40),
                Reward(5, 50),
                Reward(6, 60),
                Reward(7, 100)
            )

            adapter = rewardAdapter
            setHasFixedSize(true)
        }
    }

    fun showDialog() = dialog.show()
    fun dismissDialog() = dialog.dismiss()
}