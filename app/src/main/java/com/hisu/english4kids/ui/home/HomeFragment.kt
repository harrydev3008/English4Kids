package com.hisu.english4kids.ui.home

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.gson.Gson
import com.hisu.english4kids.MainActivity
import com.hisu.english4kids.R
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.databinding.FragmentHomeBinding
import com.hisu.english4kids.widget.dialog.DailyRewardDialog
import com.hisu.english4kids.widget.dialog.SettingDialog
import com.hisu.english4kids.utils.local.LocalDataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var localDataManager: LocalDataManager
    private lateinit var currentUser: Player

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView()
        handleDailyReward()
        learningMode()
        leaderBoard()
        competitiveMode()
        dailyReward()
        handleSettingButton()
    }

    private fun setUpView() {
        localDataManager = LocalDataManager()
        localDataManager.init(requireContext())

        currentUser = Gson().fromJson(localDataManager.getUserInfo(), Player::class.java)

        binding.apply {
            btnCompetitiveMode.isEnabled = currentUser.level >= 20
            resource.tvLevel.text = String.format(
                requireContext().getString(R.string.level_pattern),
                currentUser.level
            )
        }
    }

    private fun handleSettingButton() = binding.btnSetting.setOnClickListener {
        findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun handleDailyReward() = viewLifecycleOwner.lifecycleScope.launchWhenCreated {
        val isDaily =  withContext(Dispatchers.Default) { localDataManager.getUserRemindDailyRewardState() }

        if(isDaily) {
            val badgeDrawable =  BadgeDrawable.create(requireContext())
            badgeDrawable.number = 1
            badgeDrawable.setContentDescriptionNumberless("!")
            badgeDrawable.backgroundColor = requireContext().getColor(R.color.text_incorrect)
            badgeDrawable.badgeTextColor = requireContext().getColor(R.color.white)
            badgeDrawable.isVisible = true
            badgeDrawable.horizontalOffset = 20
            badgeDrawable.verticalOffset = 20

            BadgeUtils.attachBadgeDrawable(badgeDrawable, binding.btnDailyReward);
        }
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