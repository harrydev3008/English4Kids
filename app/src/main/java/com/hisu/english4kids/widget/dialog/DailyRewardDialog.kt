package com.hisu.english4kids.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.hisu.english4kids.data.model.daily_reward.Reward
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.databinding.LayoutDailyRewardsBinding
import com.hisu.english4kids.utils.local.LocalDataManager
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

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
        dialog.setCancelable(true)

        val window = dialog.window ?: return

        val width = (context.resources.displayMetrics.widthPixels * 0.90).toInt()

        window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val windowAttr = window.attributes
        windowAttr.gravity = gravity
        window.attributes = windowAttr

        dialog.setCancelable(false)

        setUpRewardRecyclerView()
        handleCloseButton()
    }

    private fun setUpRewardRecyclerView() = binding.rvDailyRewards.apply {

        val gridLayoutManager = GridLayoutManager(context, 3)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                /**
                 * 7 days, 7 rewards
                 * if position == the last day (day 7th, index 6) we will span the col
                 */
                if(position == 6) return 3
                return 1
            }
        }

        layoutManager = gridLayoutManager
        val localDataManager = LocalDataManager()
        localDataManager.init(context)

        val currentPlayer = Gson().fromJson(localDataManager.getUserInfo(), Player::class.java)
        val rewardList = mutableListOf<Reward>()

        if(currentPlayer.claimCount == 0 || currentPlayer.lastClaimdDate.isEmpty()) {
            rewardList.addAll(listOf(
                Reward(1, 10, isClaimed = false, isClaimable = true),
                Reward(2, 20),
                Reward(3, 30),
                Reward(4, 40),
                Reward(5, 50),
                Reward(6, 60),
                Reward(7, 100)
            ))
        } else {
            for(i in 0  until 7) {
                if(i < currentPlayer.claimCount) {
                    rewardList.add(Reward(i + 1, 10 * (i + 1), isClaimed = true))
                } else if(i == currentPlayer.claimCount) {

                    val day = currentPlayer.lastClaimdDate.substring(8, 10).toInt()
                    val mon = currentPlayer.lastClaimdDate.substring(5, 7).toInt()
                    val year = currentPlayer.lastClaimdDate.substring(0, 4).toInt()

                    val lastClaimedDate = LocalDate.of(year, mon, day)

                    if(lastClaimedDate.isBefore(LocalDate.now())) {
                        rewardList.add(Reward(i + 1, 10 * (i + 1), isClaimable = true, isClaimed = false))
                    } else if (LocalDate.now().isAfter(lastClaimedDate)) {
                        rewardList.add(Reward(i + 1, 10 * (i + 1), isClaimable = true, isClaimed = false))
                    } else {
                        rewardList.add(Reward(i + 1, 10 * (i + 1), isClaimable = false, isClaimed = false))
                    }
                } else {
                    rewardList.add(Reward(i + 1, 10 * (i + 1), isClaimed = false, isClaimable = false))
                }
            }
        }

        val rewardAdapter = DailyRewardAdapter(context) {
            claimReward.invoke(it)
        }
        rewardAdapter.rewards = rewardList

        adapter = rewardAdapter
        setHasFixedSize(true)
    }

    private fun handleCloseButton() = binding.btnClose.setOnClickListener {
        dismissDialog()
    }

    fun showDialog() = dialog.show()
    fun dismissDialog() = dialog.dismiss()
}