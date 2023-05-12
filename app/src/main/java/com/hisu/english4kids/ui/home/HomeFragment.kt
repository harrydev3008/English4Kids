package com.hisu.english4kids.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hisu.english4kids.R
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.model.course.Course
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.CourseResponseModel
import com.hisu.english4kids.data.network.response_model.DataCourse
import com.hisu.english4kids.databinding.FragmentHomeBinding
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.widget.dialog.DailyRewardDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var localDataManager: LocalDataManager
    private lateinit var coursesResponse: List<Course>
    private lateinit var courseAdapter: CourseItemViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView()
        handleDailyReward()
        initFeatureMovieList()
        getCourses()
        leaderBoard()
        competitiveMode()
        dailyReward()
        handleSettingButton()
    }

    private fun setUpView() {
        localDataManager = LocalDataManager()
        localDataManager.init(requireContext())
    }

    private fun handleSettingButton() = binding.btnSetting.setOnClickListener {
        findNavController().navigate(R.id.home_to_profile)
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

    private fun getCourses() {
        if(MyUtils.isNetworkAvailable(requireContext()))
            API.apiService.getCourses("Bearer ${localDataManager.getUserAccessToken()}").enqueue(handleGetCourseCallback)
        else {
            val localCourses = Gson().fromJson(
                localDataManager.getCourseInfo(), DataCourse::class.java
            )
            coursesResponse = localCourses.courses
            courseAdapter.courses = coursesResponse
            courseAdapter.notifyDataSetChanged()
            binding.circleIndicator.setViewPager(binding.vpCourses)
        }
    }

    private fun initFeatureMovieList() = binding.vpCourses.apply {
        courseAdapter = CourseItemViewPagerAdapter(requireContext()) {
            val action = HomeFragmentDirections.homeToLesson(
                courseId = it._id,
                title = it.title
            )
            findNavController().navigate(action)
        }

        val transformer = CompositePageTransformer()

        transformer.addTransformer(MarginPageTransformer(30))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }

        clipToPadding = false
        clipChildren = false
        offscreenPageLimit = 3

        adapter = courseAdapter

        setPageTransformer(transformer)
    }

    private val handleGetCourseCallback = object : Callback<CourseResponseModel> {
        override fun onResponse(call: Call<CourseResponseModel>, response: Response<CourseResponseModel>) {
            if (response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {
                    this.data?.apply {
                        coursesResponse = this.courses
                        courseAdapter.courses = coursesResponse
                        courseAdapter.notifyDataSetChanged()
                        binding.circleIndicator.setViewPager(binding.vpCourses)
                        localDataManager.setCourseInfo(Gson().toJsonTree(this).toString())
                    }
                }
            }
        }

        override fun onFailure(call: Call<CourseResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                iOSDialogBuilder(requireContext())
                    .setTitle(requireContext().getString(R.string.request_err))
                    .setSubtitle(requireContext().getString(R.string.err_network_not_connected))
                    .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                        it.dismiss()
                    }.build().show()
            }
            Log.e(HomeFragment::class.java.name, t.message ?: "error message")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}