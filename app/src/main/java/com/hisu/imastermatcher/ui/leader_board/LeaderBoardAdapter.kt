package com.hisu.imastermatcher.ui.leader_board

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.LayoutItemLeaderBoardBinding
import com.hisu.imastermatcher.model.leader_board.LeaderBoardUser

class LeaderBoardAdapter(var context: Context) :
    RecyclerView.Adapter<LeaderBoardAdapter.LeaderBoardItemViewHolder>() {

    var leaderBoardUsers = mutableListOf<LeaderBoardUser>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderBoardItemViewHolder {
        return LeaderBoardItemViewHolder(
            LayoutItemLeaderBoardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: LeaderBoardItemViewHolder, position: Int) {
        val leaderBoardUser = leaderBoardUsers[position]
        holder.bindData(context, leaderBoardUser)
    }

    override fun getItemCount(): Int = leaderBoardUsers.size

    inner class LeaderBoardItemViewHolder(var binding: LayoutItemLeaderBoardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(context:Context, data: LeaderBoardUser) = binding.apply {
            tvIndex.text = "${data.rank}"
            tvUsername.text = data.username
            tvWeekExp.text = String.format(context.getString(R.string.weekly_exp_pattern), data.totalScore)
        }
    }
}