package com.hisu.english4kids.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hisu.english4kids.MyApplication
import com.hisu.english4kids.R
import com.hisu.english4kids.data.CONTENT_TYPE_JSON
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.model.course.Course
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.CourseResponseModel
import com.hisu.english4kids.data.network.response_model.DataCourse
import com.hisu.english4kids.data.network.response_model.UpdateUserResponseModel
import com.hisu.english4kids.data.room_db.repository.CourseRepository
import com.hisu.english4kids.data.room_db.view_model.CourseViewModel
import com.hisu.english4kids.data.room_db.view_model.CourseViewModelProviderFactory
import com.hisu.english4kids.databinding.FragmentHomeBinding
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.widget.dialog.DailyRewardDialog
import com.hisu.english4kids.widget.dialog.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
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
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var dialog: DailyRewardDialog

    private val courseViewModel: CourseViewModel by activityViewModels() {
        CourseViewModelProviderFactory(
            CourseRepository(
                requireActivity().applicationContext,
                (activity?.application as MyApplication).database.courseDAO()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)

        setUpView()
    }

    private fun setUpView() {
        localDataManager = LocalDataManager()
        localDataManager.init(requireContext())

        initCoursesList()
        getCourses()
        leaderBoard()
        competitiveMode()
        dailyReward()
        handleSettingButton()
    }

    private fun handleSettingButton() = binding.btnSetting.setOnClickListener {
        findNavController().navigate(R.id.home_to_profile)
    }

    private fun leaderBoard() = binding.btnLeaderBoard.setOnClickListener {
        findNavController().navigate(R.id.action_homeFragment_to_leaderBoardFragment)
    }

    private fun competitiveMode() = binding.btnCompetitiveMode.setOnClickListener {
        findNavController().navigate(R.id.home_to_competitive)
    }

    private fun dailyReward() = binding.btnDailyReward.setOnClickListener {
         dialog = DailyRewardDialog(requireContext(), Gravity.CENTER) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("golds", it.reward)

            val bodyRequest = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), jsonObject.toString())

            requireActivity().runOnUiThread {
                mLoadingDialog.showDialog()
            }

            API.apiService.claimDailyReward("Bearer ${localDataManager.getUserAccessToken()}" ,bodyRequest).enqueue(handleClaimDaily)
        }
        dialog.showDialog()
    }

    private fun getCourses() {
        courseViewModel.getCourse().observe(this.viewLifecycleOwner) {
            it?.apply {
                courseAdapter.courses = this
                courseAdapter.notifyDataSetChanged()
                binding.circleIndicator.setViewPager(binding.vpCourses)
                if (this[0].currentLevel == this[0].totalLevel) {
                    binding.btnCompetitiveMode.isEnabled = true
                }
            }
        }
    }

    private fun initCoursesList() = binding.vpCourses.apply {
        courseAdapter = CourseItemViewPagerAdapter(requireContext()) {
            if(MyUtils.isNetworkAvailable(requireContext())) {
                val action = HomeFragmentDirections.homeToLesson(
                    courseId = it._id,
                    title = it.title
                )
                findNavController().navigate(action)
            } else {
                requireActivity().runOnUiThread {
                    iOSDialogBuilder(requireContext())
                        .setTitle(requireContext().getString(R.string.err_network_not_available))
                        .setSubtitle(requireContext().getString(R.string.err_connect_internet_to_learn))
                        .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                            it.dismiss()
                        }.build().show()
                }
            }
        }

        val transformer = CompositePageTransformer()

        transformer.addTransformer(MarginPageTransformer(15))
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

    private val handleClaimDaily = object : Callback<UpdateUserResponseModel> {
        override fun onResponse(call: Call<UpdateUserResponseModel>, response: Response<UpdateUserResponseModel>) {

            requireActivity().runOnUiThread {
                dialog.dismissDialog()
            }

            if (response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {
                    this.data.apply {
                        val playerInfoJson = Gson().toJson(this.updatedUser)
                        localDataManager.setUserLoinState(true)
                        localDataManager.setUserInfo(playerInfoJson)

                        requireActivity().runOnUiThread {
                            mLoadingDialog.dismissDialog()
                            iOSDialogBuilder(requireContext())
                                .setTitle(requireContext().getString(R.string.confirm_otp))
                                .setSubtitle(requireContext().getString(R.string.claimed_success))
                                .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                                    it.dismiss()
                                }.build().show()
                        }
                    }
                }
            } else {
                requireActivity().runOnUiThread {
                    iOSDialogBuilder(requireContext())
                        .setTitle(requireContext().getString(R.string.request_err))
                        .setSubtitle(requireContext().getString(R.string.claimed_err))
                        .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                            it.dismiss()
                        }.build().show()
                }
            }
        }

        override fun onFailure(call: Call<UpdateUserResponseModel>, t: Throwable) {
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