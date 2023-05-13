package com.hisu.english4kids.ui.leader_board

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hisu.english4kids.R
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.databinding.LayoutItemLeaderBoardBinding
import com.hisu.english4kids.utils.MyUtils

class LeaderBoardAdapter(var context: Context, var currentUser: Player) :
    RecyclerView.Adapter<LeaderBoardAdapter.LeaderBoardItemViewHolder>() {

    var leaderBoardUsers = listOf<Player>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderBoardItemViewHolder {
        return LeaderBoardItemViewHolder(
            LayoutItemLeaderBoardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: LeaderBoardItemViewHolder, position: Int) {
        val player = leaderBoardUsers[position]
        val isCurrentUser = currentUser.id == player.id
        holder.bindData(player,isCurrentUser, position + 1)
    }

    override fun getItemCount(): Int = leaderBoardUsers.size

    inner class LeaderBoardItemViewHolder(var binding: LayoutItemLeaderBoardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: Player, isCurrentUser: Boolean = false, index: Int) = binding.apply {

            cimvUserPfp.setImageBitmap(MyUtils.createImageFromText(context, data.username))
            tvIndex.text = "$index"
            tvUsername.text = data.username
            tvWeekExp.text = "${data.weeklyScore}"

            if(isCurrentUser) {
                parentView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue_gray))
            }
        }
    }
}