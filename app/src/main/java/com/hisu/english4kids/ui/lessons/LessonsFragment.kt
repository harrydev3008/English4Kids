package com.hisu.english4kids.ui.lessons

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hisu.english4kids.MyApplication
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
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.data.network.response_model.UpdateUserResponseModel
import com.hisu.english4kids.data.room_db.repository.PlayerRepository
import com.hisu.english4kids.data.room_db.view_model.PlayerViewModel
import com.hisu.english4kids.data.room_db.view_model.PlayerViewModelProviderFactory
import com.hisu.english4kids.databinding.FragmentLessonsBinding
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
    private lateinit var currentUser: Player

    private val playerViewModel: PlayerViewModel by activityViewModels {
        PlayerViewModelProviderFactory(
            PlayerRepository(
                requireActivity().applicationContext,
                (activity?.application as MyApplication).database.playerDAO()
            )
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLessonsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)

        localDataManager = LocalDataManager()
        localDataManager.init(requireContext())

        binding.tvMode.text = myNavArgs.title

        loadUserInfo()
        initDialog()
        backToHomePage()
        setUpLessonsRecyclerView()
        loadLessons()
        handleButtonHeart()
        setUpScrollEvent()
        backToTop()
        weeklyScoreHint()
    }

    private fun loadUserInfo() {
        val playJson = localDataManager.getUserInfo()
        if(playJson.isNullOrEmpty()) return

        currentUser = Gson().fromJson(playJson, Player::class.java)
        playerViewModel.getPlayerInfo(currentUser.id).observe(this.viewLifecycleOwner) {
            bindUserData(it)
        }
    }

    private fun bindUserData(player: Player) = binding.apply {
        tvWeeklyScore.text = player.weeklyScore.toString()
        tvCoins.text = player.golds.toString()
        tvHeart.text = player.hearts.toString()
    }

    private fun backToHomePage() = binding.tvMode.setOnClickListener {
        findNavController().popBackStack()
//        findNavController().navigate(R.id.action_classModeLevelFragment_to_courseFragment)
    }

    private fun setUpLessonsRecyclerView() = binding.rvLessons.apply {
        lessonsAdapter = LessonAdapter(requireContext(), ::handleLessonClick)
        adapter = lessonsAdapter
        binding.floatingButton.hide()
    }

    private fun setUpScrollEvent() {
        binding.scrollContainer.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            run {
                if (scrollY > 0) {
                    binding.floatingButton.show()
                } else {
                    binding.floatingButton.hide()
                }
            }
        }
    }

    private fun weeklyScoreHint() = binding.tvWeeklyScore.setOnClickListener {
        iOSDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.weekly_score))
            .setSubtitle(requireContext().getString(R.string.weekly_score_desc))
            .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                it.dismiss()
            }.build().show()
    }

    private fun backToTop() = binding.floatingButton.setOnClickListener {
        binding.rvLessons.post {
            val y = binding.rvLessons.y + binding.rvLessons.getChildAt(0).y
            binding.scrollContainer.smoothScrollTo(0, y.toInt())
        }
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
        API.apiService.getLessonByCourseId("Bearer ${localDataManager.getUserAccessToken()}", myNavArgs.courseId).enqueue(handleGetLessonCallback)
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
                        playerViewModel.updatePlayer(this.updatedUser)

                        requireActivity().runOnUiThread {
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
            Log.e(LessonsFragment::class.java.name, t.message?: "error message")
        }
    }
}