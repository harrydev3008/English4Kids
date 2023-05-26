package com.hisu.english4kids.ui.play_screen.gameplay.multiple_choice

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
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
import com.google.gson.reflect.TypeToken
import com.hisu.english4kids.MyApplication
import com.hisu.english4kids.R
import com.hisu.english4kids.data.CONTENT_TYPE_JSON
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.model.exam.Answer
import com.hisu.english4kids.data.model.exam.ExamQuestion
import com.hisu.english4kids.data.model.result.FinalResult
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.UpdateUserResponseModel
import com.hisu.english4kids.data.room_db.repository.PlayerRepository
import com.hisu.english4kids.data.room_db.view_model.PlayerViewModel
import com.hisu.english4kids.data.room_db.view_model.PlayerViewModelProviderFactory
import com.hisu.english4kids.databinding.FragmentMultipleChoiceContainerBinding
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.widget.dialog.WalkingLoadingDialog
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MultipleChoiceContainerFragment : Fragment() {

    private var _binding: FragmentMultipleChoiceContainerBinding? = null
    private val binding get() = _binding!!
    private val myArgs: MultipleChoiceContainerFragmentArgs by navArgs()

    private var questions = listOf<ExamQuestion>()
    private var startGamePlayTime: Long = 0
    private var finishTime: Long = 0
    private var correctAnswer: Int = 0
    private var totalScore: Int = 0

    private lateinit var localDataManager: LocalDataManager
    private lateinit var mWalkingLoadingDialog: WalkingLoadingDialog

    private val playerViewModel: PlayerViewModel by activityViewModels() {
        PlayerViewModelProviderFactory(
            PlayerRepository(
                requireActivity().applicationContext,
                (activity?.application as MyApplication).database.playerDAO()
            )
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMultipleChoiceContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localDataManager = LocalDataManager()
        localDataManager.init(requireContext())

        mWalkingLoadingDialog = WalkingLoadingDialog(requireContext())

        val itemType = object : TypeToken<List<ExamQuestion>>() {}.type
        val tempQuestions = Gson().fromJson<List<ExamQuestion>>(myArgs.examQuestions, itemType)
        questions = tempQuestions

        setUpViewPager()
        back()
    }

    private fun back() = binding.cardCompetitiveHeader.tvBack.setOnClickListener {
        iOSDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.confirm_quit_game_play_title))
            .setSubtitle(requireContext().getString(R.string.confirm_quit_exam))
            .setBoldPositiveLabel(true)
            .setNegativeListener(requireContext().getString(R.string.confirm_msg_stay)) {
                it.dismiss()
            }.setPositiveListener(requireContext().getString(R.string.confirm_msg_quit)) {
                it.dismiss()
                findNavController().navigate(R.id.action_multipleChoiceContainerFragment_to_homeFragment)
            }.build().show()
    }

    private fun setUpViewPager() = binding.vpMultipleQuestion.apply {
        val questionAdapter = MultipleChoiceViewPagerAdapter(requireActivity(), ::handleNextQuestion)

        questionAdapter.questions = questions

        adapter = questionAdapter
        isUserInputEnabled = false
        startGamePlayTime = SystemClock.elapsedRealtime()
    }

    private fun handleNextQuestion(answer: Answer, position: Int, isCorrect: Boolean) {
        questions[position].userPickAnswer = answer.answerId

        if(isCorrect) {
            correctAnswer++
            totalScore += questions[position].score
        }

        if (binding.vpMultipleQuestion.currentItem < questions.size - 1)
            binding.vpMultipleQuestion.currentItem = binding.vpMultipleQuestion.currentItem + 1
        else {

            requireActivity().runOnUiThread {
                mWalkingLoadingDialog.setLoadingText("Đang chấm điểm!\nBạn chờ tý nha!")
                mWalkingLoadingDialog.showDialog()
            }

            finishTime = SystemClock.elapsedRealtime() - startGamePlayTime

            val obj = JsonObject()
            obj.addProperty("score", totalScore)
            val requestBody = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), obj.toString())
            API.apiService.updateScoreAndGold("Bearer ${localDataManager.getUserAccessToken()}", requestBody).enqueue(handleUpdateGoldAndScore)
        }
    }

    private val handleUpdateGoldAndScore = object: Callback<UpdateUserResponseModel> {
        override fun onResponse(call: Call<UpdateUserResponseModel>, response: Response<UpdateUserResponseModel>) {
            if(response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {
                    this.data.apply {
                        val playerInfoJson = Gson().toJson(this.updatedUser)
                        localDataManager.setUserInfo(playerInfoJson)
                        playerViewModel.updatePlayer(this.updatedUser)

                        val finalResult = FinalResult(MyUtils.convertMilliSecondsToMMSS(finishTime),0, totalScore, totalScore / 2, String.format("%02d/%02d", correctAnswer, questions.size))
                        val action = MultipleChoiceContainerFragmentDirections.actionMultipleChoiceContainerFragmentToCompleteCompetitiveFragment(
                            result = Gson().toJson(finalResult),
                            answers = Gson().toJson(questions)
                        )

                        Handler(requireActivity().mainLooper).postDelayed({
                            mWalkingLoadingDialog.dismissDialog()
                            findNavController().navigate(action)
                        }, 3000)
                    }
                }
            } else {
                requireActivity().runOnUiThread {
                    mWalkingLoadingDialog.dismissDialog()
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
                mWalkingLoadingDialog.dismissDialog()
                iOSDialogBuilder(requireContext())
                    .setTitle(requireContext().getString(R.string.request_err))
                    .setSubtitle(requireContext().getString(R.string.confirm_otp_err_occur_msg))
                    .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                        it.dismiss()
                    }.build().show()
            }
            Log.e(CompleteCompetitiveFragment::class.java.name, t.message?: "error message")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}