package com.hisu.english4kids.ui.home

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.FragmentHomeBinding
import com.hisu.english4kids.model.leader_board.User
import com.hisu.english4kids.ui.dialog.DailyRewardDialog
import com.hisu.english4kids.utils.local.LocalDataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val localDataManager = LocalDataManager()
        localDataManager.init(requireContext())

//        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
//            val userJson =  withContext(Dispatchers.Default) { localDataManager.getUserInfo() }
//            val user = Gson().fromJson(userJson, User::class.java)
//
//            Log.e("test_user", "${user.phoneNumber} - ${user.username}")
//        }

        learningMode()
        leaderBoard()
        competitiveMode()
        dailyReward()

        binding.btnCompetitiveMode.isEnabled = true
    }

    private fun learningMode() = binding.btnLearningMode.setOnClickListener {
        findNavController().navigate(R.id.home_to_course)
    }

    private fun leaderBoard() = binding.btnLeaderBoard.setOnClickListener {
        findNavController().navigate(R.id.action_homeFragment_to_leaderBoardFragment)
    }

    private fun competitiveMode() = binding.btnCompetitiveMode.setOnClickListener {
        findNavController().navigate(R.id.home_to_competitive)
    }

    private fun dailyReward() = binding.btnDailyReward.setOnClickListener {
        val dialog = DailyRewardDialog(requireContext(), Gravity.CENTER)
        dialog.showDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}