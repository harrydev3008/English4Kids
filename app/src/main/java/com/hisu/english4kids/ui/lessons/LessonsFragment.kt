package com.hisu.english4kids.ui.lessons

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hisu.english4kids.R
import com.hisu.english4kids.data.BUNDLE_COURSE_ID_DATA
import com.hisu.english4kids.data.BUNDLE_LESSON_DATA
import com.hisu.english4kids.data.BUNDLE_LESSON_ID_DATA
import com.hisu.english4kids.data.BUNDLE_ROUND_PLAYED_DATA
import com.hisu.english4kids.data.CONTENT_TYPE_JSON
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.model.course.Lesson
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.LessonResponseModel
import com.hisu.english4kids.data.network.response_model.SearchUserResponseModel
import com.hisu.english4kids.data.network.response_model.UpdateUserResponseModel
import com.hisu.english4kids.databinding.FragmentLessonsBinding
import com.hisu.english4kids.ui.play_screen.PlayFragment
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.widget.dialog.LoadingDialog
import com.hisu.english4kids.widget.dialog.PurchaseHeartDialog
import com.hisu.english4kids.widget.dialog.StartRoundDialog
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LessonsFragment : Fragment() {

    private var _binding: FragmentLessonsBinding? = null
    private val binding get() = _binding!!
    private val myNavArgs: LessonsFragmentArgs by navArgs()

    private var lessonsAdapter: LessonAdapter? = null

    private lateinit var localDataManager: LocalDataManager
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var heartDialog: PurchaseHeartDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)

        localDataManager = LocalDataManager()
        localDataManager.init(requireContext())

        binding.tvMode.text = myNavArgs.title

        initView()
        initDialog()
        backToHomePage()
        setUpLessonsRecyclerView()
        loadLessons()
        handleButtonHeart()
    }

    private fun initView() {
        if(MyUtils.isNetworkAvailable(requireContext())) {
            API.apiService.getUserInfo("Bearer ${localDataManager.getUserAccessToken()}").enqueue(handleGetUserInfo)
        }
    }

    private fun backToHomePage() = binding.tvMode.setOnClickListener {
        findNavController().popBackStack()
//        findNavController().navigate(R.id.action_classModeLevelFragment_to_courseFragment)
    }

    private fun setUpLessonsRecyclerView() = binding.rvLessons.apply {
        lessonsAdapter = LessonAdapter(requireContext(), ::handleLessonClick)
        adapter = lessonsAdapter
    }

    private fun handleButtonHeart() = binding.tvHeart.setOnClickListener {
        heartDialog.showDialog()
    }

    private fun handleLessonClick(lesson: Lesson, position: Int, status: Int) {
        if(MyUtils.isNetworkAvailable(requireContext())) {

            val startRoundDialog = StartRoundDialog(
                requireContext(),
                String.format(
                    requireContext().getString(R.string.round_pattern),
                    position + 1,
                    lesson.rounds.size
                ), status
            )

            startRoundDialog.setStartBtnEvent {
                startRoundDialog.dismissDialog()

                val bundle = Bundle()
                bundle.putString(
                    BUNDLE_LESSON_DATA,
                    Gson().toJson(lesson.rounds.slice(position until lesson.rounds.size))
                )

                bundle.putString(BUNDLE_LESSON_ID_DATA, lesson._id)
                bundle.putString(BUNDLE_COURSE_ID_DATA, myNavArgs.courseId)
                val isLessonCompleted = lesson.playedRounds == lesson.totalRounds
                bundle.putBoolean(BUNDLE_ROUND_PLAYED_DATA, isLessonCompleted)

                findNavController().navigate(
                    R.id.action_classModeLevelFragment_to_playFragment,
                    bundle
                )
            }

            startRoundDialog.showDialog()

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

    private fun initDialog() {
        heartDialog = PurchaseHeartDialog(requireContext())

        heartDialog.setPurchaseCallback { amount ->

            requireActivity().runOnUiThread {
                mLoadingDialog.showDialog()
            }

            val obj = JsonObject()
            obj.addProperty("hearts", amount)

            val requestBody = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), obj.toString())

            API.apiService.buyHeart("Bearer ${localDataManager.getUserAccessToken()}", requestBody).enqueue(handleBuyHearts)
        }
    }

    private fun loadLessons() {
        API.apiService.getLessonByCourseId(
            "Bearer ${localDataManager.getUserAccessToken()}",
            myNavArgs.courseId
        ).enqueue(handleGetLessonCallback)
    }

    private val handleGetLessonCallback = object : Callback<LessonResponseModel> {
        override fun onResponse(call: Call<LessonResponseModel>, response: Response<LessonResponseModel>) {
            if (response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {
                    this.data.apply {
                        lessonsAdapter?.lessons = this.lession
                        lessonsAdapter?.notifyDataSetChanged()
                        binding.rvLessons.adapter = lessonsAdapter
                        localDataManager.setLessonsInfo(Gson().toJsonTree(this).toString())
                    }
                }
            }
        }

        override fun onFailure(call: Call<LessonResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                iOSDialogBuilder(requireContext())
                    .setTitle(requireContext().getString(R.string.request_err))
                    .setSubtitle(requireContext().getString(R.string.err_network_not_connected))
                    .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                        it.dismiss()
                    }.build().show()
            }
            Log.e(LessonsFragment::class.java.name, t.message ?: "error message")
        }
    }

    private val handleGetUserInfo = object: Callback<SearchUserResponseModel> {
        override fun onResponse(call: Call<SearchUserResponseModel>, response: Response<SearchUserResponseModel>) {

            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }

            if(response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {
                    this.data.user.apply {
                        val playerInfoJson = Gson().toJson(this)
                        localDataManager.setUserInfo(playerInfoJson)

                            requireActivity().runOnUiThread {
                                binding.tvWeeklyScore.text = this.weeklyScore.toString()
                                binding.tvCoins.text = this.golds.toString()
                                binding.tvHeart.text = this.hearts.toString()
                            }
                    }
                }
            } else {
                requireActivity().runOnUiThread {
                    iOSDialogBuilder(requireContext())
                        .setTitle(requireContext().getString(R.string.request_err))
                        .setSubtitle(requireContext().getString(R.string.confirm_otp_err_occur_msg))
                        .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                            it.dismiss()
                        }.build().show()
                }
            }
        }

        override fun onFailure(call: Call<SearchUserResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }
            Log.e(LessonsFragment::class.java.name, t.message?: "error message")
        }
    }

    private val handleBuyHearts = object: Callback<UpdateUserResponseModel> {
        override fun onResponse(call: Call<UpdateUserResponseModel>, response: Response<UpdateUserResponseModel>) {

            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }

            if(response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {
                    this.data.apply {
                        val playerInfoJson = Gson().toJson(this.updatedUser)
                        localDataManager.setUserInfo(playerInfoJson)

                        requireActivity().runOnUiThread {
                            binding.tvHeart.text = this.updatedUser.hearts.toString()
                            iOSDialogBuilder(requireContext())
                                .setTitle(requireContext().getString(R.string.confirm_otp))
                                .setSubtitle(requireContext().getString(R.string.buy_more_heart_success))
                                .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                                    it.dismiss()
                                    heartDialog.dismissDialog()
                                }.build().show()
                        }
                    }
                }
            } else {
                requireActivity().runOnUiThread {
                    iOSDialogBuilder(requireContext())
                        .setTitle(requireContext().getString(R.string.request_err))
                        .setSubtitle(requireContext().getString(R.string.confirm_otp_err_occur_msg))
                        .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                            it.dismiss()
                        }.build().show()
                }
            }
        }

        override fun onFailure(call: Call<UpdateUserResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }
            Log.e(PlayFragment::class.java.name, t.message?: "error message")
        }
    }
}