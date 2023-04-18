package com.hisu.english4kids.ui.leader_board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.FragmentLeaderBoardBinding
import com.hisu.english4kids.model.leader_board.LeaderBoardResponse
import com.hisu.english4kids.utils.MyUtils

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