package com.hisu.imastermatcher.ui.leader_board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentLeaderBoardBinding
import com.hisu.imastermatcher.model.leader_board.LeaderBoardResponse
import com.hisu.imastermatcher.utils.MyUtils

class LeaderBoardFragment : Fragment() {

    private var _binding: FragmentLeaderBoardBinding? = null
    private val binding get() = _binding!!
    private lateinit var leaderBoardAdapter: LeaderBoardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeaderBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLeaderBoardAdapter()
        setUpLeaderBoardRecyclerView()
//        backToLevel()
    }

    private fun initLeaderBoardAdapter() {
        leaderBoardAdapter = LeaderBoardAdapter(requireContext())

        //todo: call api to get leader board data
        leaderBoardAdapter.leaderBoardUsers = Gson().fromJson(
            MyUtils.loadJsonFromAssets(requireActivity(), "leaderboard.json"),
            LeaderBoardResponse::class.java
        )
    }

    private fun setUpLeaderBoardRecyclerView() = binding.rvLeaderBoard.apply {
        adapter = leaderBoardAdapter
    }

    private fun backToLevel() = binding.btnNextRound.setOnClickListener {
        findNavController().navigate(R.id.board_to_level)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}