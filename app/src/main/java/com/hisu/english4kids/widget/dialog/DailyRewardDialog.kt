package com.hisu.english4kids.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.hisu.english4kids.data.model.daily_reward.Reward
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.databinding.LayoutDailyRewardsBinding
import com.hisu.english4kids.utils.local.LocalDataManager
import java.time.LocalDate

class DailyRewardDialog() {
    private lateinit var context: Context
    private var gravity: Int = 0
    private lateinit var dialog: Dialog
    private lateinit var binding: LayoutDailyRewardsBinding
    private lateinit var claimReward: (reward: Reward) -> Unit

    constructor(context: Context, gravity: Int, claimReward: (reward: Reward) -> Unit): this() {
        this.context = context
        this.gravity = gravity
        this.claimReward = claimReward
        initDialog()
    }

    private fun initDialog() {
        binding = LayoutDailyRewardsBinding.inflate(LayoutInflater.from(context), null, false)

        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)

        val window = dialog.window ?: return

        val width = (context.resources.displayMetrics.widthPixels * 0.90).toInt()

        window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttr = window.attributes
        windowAttr.gravity = gravity
        window.attributes = windowAttr

        setUpRewardRecyclerView()
        handleCloseButton()
    }

    private fun setUpRewardRecyclerView() = binding.rvDailyRewards.apply {
        layoutManager = initGridLayoutManager()

        val localDataManager = LocalDataManager()
        localDataManager.init(context)

        val currentPlayer = Gson().fromJson(localDataManager.getUserInfo(), Player::class.java)
        val rewardList = getRewardList(currentPlayer.claimCount)

        if (currentPlayer.claimCount == 0) {
            rewardList[0].isClaimed = 0
        } else if (currentPlayer.claimCount < 7)  {
            for (i in 0 until currentPlayer.claimCount) {
                rewardList[i].isClaimed = 1
            }

            val day = currentPlayer.lastClaimdDate.substring(8, 10).toInt()
            val mon = currentPlayer.lastClaimdDate.substring(5, 7).toInt()
            val year = currentPlayer.lastClaimdDate.substring(0, 4).toInt()

            val lastClaimedDate = LocalDate.of(year, mon, day)

            if(lastClaimedDate.isBefore(LocalDate.now())) {
                rewardList[currentPlayer.claimCount].isClaimed = 0
            } else if (LocalDate.now().isEqual(lastClaimedDate)) {
                rewardList[currentPlayer.claimCount].isClaimed = -1
            }
        }

        val rewardAdapter = DailyRewardAdapter(context) {
            claimReward.invoke(it)
        }

        rewardAdapter.rewards = rewardList

        adapter = rewardAdapter
        setHasFixedSize(true)
    }

    private fun initGridLayoutManager(): GridLayoutManager {
        val gridLayoutManager = GridLayoutManager(context, 3)
        gridLayoutManager.spanSizeLookup = spanSizeCount

        return gridLayoutManager
    }

    private val spanSizeCount = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            /**
             * 7 rewards, span the last col by 3
             */
            if (position == 6)
                return 3
            return 1
        }
    }

    private fun getRewardList(claimCount: Int) = if (claimCount < 7)
        MutableList(7) { Reward((it + 1) * 10, -1) }
    else
        MutableList(7) { Reward((it + 1) * 10, 1) }

    private fun handleCloseButton() = binding.btnClose.setOnClickListener {
        dismissDialog()
    }

    fun showDialog() = dialog.show()
    fun dismissDialog() = dialog.dismiss()
}