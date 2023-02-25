package com.hisu.imastermatcher.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.hisu.imastermatcher.databinding.LayoutDailyRewardsBinding

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
            val rewardAdapter = DailyRewardAdapter()
            rewardAdapter.rewards = listOf(
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "15"
            )

            adapter = rewardAdapter
            setHasFixedSize(true)
        }
    }

    fun showDialog() = dialog.show()
    fun dismissDialog() = dialog.dismiss()
}