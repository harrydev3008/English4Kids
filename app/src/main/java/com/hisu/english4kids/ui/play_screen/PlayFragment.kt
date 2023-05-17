package com.hisu.english4kids.ui.play_screen

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.hisu.english4kids.R
import com.hisu.english4kids.data.*
import com.hisu.english4kids.data.model.result.FinalResult
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.data.network.response_model.UpdateUserResponseModel
import com.hisu.english4kids.databinding.FragmentPlayBinding
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.widget.dialog.GameFinishDialog
import com.hisu.english4kids.widget.dialog.LoadingDialog
import com.hisu.english4kids.widget.dialog.MessageDialog
import com.hisu.english4kids.widget.dialog.PurchaseHeartDialog
import es.dmoral.toasty.Toasty
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class PlayFragment : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!

    private var startGamePlayTime: Long = 0
    private var wrongAnswerCount = 0
    private var correctAnswerCount = 0
    private var totalScore = 0
    private var unfinishedGameplays = mutableListOf<Object>()
    private var gameplays = mutableListOf<Object>()

    private lateinit var gameplayViewPagerAdapter: GameplayViewPagerAdapter
    private lateinit var heartDialog: PurchaseHeartDialog
    private lateinit var localDataManager: LocalDataManager
    private lateinit var player: Player
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)

        localDataManager = LocalDataManager()
        localDataManager.init(requireContext())

        player = Gson().fromJson(localDataManager.getUserInfo(), Player::class.java)

        initDialog()
        handleQuitGameButton()
        startGamePlayTime = System.nanoTime()

        val itemType = object : TypeToken<List<Object>>() {}.type
        val temp = Gson().fromJson<List<Object>>(arguments?.getString(BUNDLE_LESSON_DATA), itemType)
        gameplays.addAll(temp)

        binding.pbStar.max = gameplays.size
        binding.tvLife.text = player.hearts.toString()

        setUpViewpager()
    }

    private fun setUpViewpager() = binding.flRoundContainer.apply {
        isUserInputEnabled = false

        gameplayViewPagerAdapter = GameplayViewPagerAdapter(requireActivity(), ::handleNextQuestion, ::handleWrongAnswer, ::handleCorrectAnswer)
        gameplayViewPagerAdapter.setGamePlays(gameplays)

        adapter = gameplayViewPagerAdapter
    }

    private fun handleNextQuestion() {

        if (Integer.parseInt(binding.tvLife.text.toString()) < 1) {
            heartDialog.showDialog()
            return
        }

        if (binding.flRoundContainer.currentItem < gameplays.size - 1) {
            gameplayViewPagerAdapter.notifyItemChanged(binding.flRoundContainer.currentItem + 1)
            binding.pbStar.progress = binding.pbStar.progress + 1
            binding.flRoundContainer
                .setCurrentItem(binding.flRoundContainer.currentItem + 1, true)
        } else {

            if(unfinishedGameplays.size == 0) {
                val finishTime = System.nanoTime() - startGamePlayTime
                val gameRes = calculateFinalResult(finishTime)

                requireActivity().runOnUiThread {
                    mLoadingDialog.showDialog()
                }

                val obj = JsonObject()
                obj.addProperty("golds", gameRes.golds)

                val requestBody = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), obj.toString())

                API.apiService.updateGolds(
                    "Bearer ${localDataManager.getUserAccessToken()}", requestBody
                ).enqueue(handleUpdateGolds)

                val finishDialog =
                    GameFinishDialog(requireContext(), Gson().toJsonTree(gameRes))
                finishDialog.showDialog()

                finishDialog.setExitCallback {
                    finishDialog.dismissDialog()
                    findNavController().navigate(R.id.play_to_lesson)
                }

                finishDialog.setNextLessonCallback {
                    finishDialog.dismissDialog()
                    findNavController().popBackStack()
                }
            } else {
                MessageDialog(
                    requireContext(), requireContext().getString(R.string.oopss),
                    requireContext().getString(R.string.fix_wrong_exp)
                ).setStartBtnEvent {
                    gameplays.clear()
                    gameplays.addAll(unfinishedGameplays)
                    unfinishedGameplays.clear()

                    wrongAnswerCount = 0
                    binding.pbStar.max = gameplays.size
                    binding.pbStar.progress = 0
                    gameplayViewPagerAdapter.setGamePlays(gameplays)
                    gameplayViewPagerAdapter.notifyDataSetChanged()
                    binding.flRoundContainer.adapter = gameplayViewPagerAdapter
                }.showDialog()
            }
        }
    }

    private fun handleWrongAnswer(position: Int, isPlayed: Boolean) {
        unfinishedGameplays.add(gameplays[position])

        if(!isPlayed) {
            var currentLife = Integer.parseInt(binding.tvLife.text.toString())
            currentLife--
            wrongAnswerCount++
            binding.tvLife.text = "$currentLife"

            if (currentLife == 0)
                heartDialog.showDialog()
        }
    }

    private fun handleCorrectAnswer(score: Int, roundId: String, isPlayed: Boolean) {
        correctAnswerCount++

        if(!isPlayed) {
            Toasty.success(requireContext(), "+ ${(score / 2).toInt()} vàng!", Toasty.LENGTH_SHORT).show()
            totalScore += score
            val obj = JsonObject()
            obj.addProperty("userId", player.id)
            obj.addProperty("courseId", arguments?.getString(BUNDLE_COURSE_ID_DATA))
            obj.addProperty("lessionId", arguments?.getString(BUNDLE_LESSON_ID_DATA))
            obj.addProperty("roundId", roundId)
            obj.addProperty("score", score)
            obj.addProperty("hearts", Integer.parseInt(binding.tvLife.text.toString()))

            val requestBody = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), obj.toString())

            API.apiService.updateDiary(
                "Bearer ${localDataManager.getUserAccessToken()}", requestBody
            ).enqueue(handleUpdateDiaryCallback)
        } else {
            Log.e("teest", "???")
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

    private fun handleQuitGameButton() = binding.ibtnClose.setOnClickListener {
        iOSDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.confirm_dialog_msg))
            .setSubtitle(requireContext().getString(R.string.confirm_quit_game_play))
            .setBoldPositiveLabel(true)
            .setNegativeListener(requireContext().getString(R.string.confirm_msg_stay)) {
                it.dismiss()
            }.setPositiveListener(requireContext().getString(R.string.confirm_msg_quit)) {
                it.dismiss()

                requireActivity().runOnUiThread {
                    mLoadingDialog.showDialog()
                }

                val obj = JsonObject()
                obj.addProperty("hearts", Integer.parseInt(binding.tvLife.text.toString()))
                val requestBody = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), obj.toString())

                API.apiService.updateHeart("Bearer ${localDataManager.getUserAccessToken()}", requestBody).enqueue(handleUpdateHeart)

            }.build().show()
    }

    private fun calculateFinalResult(finishTime: Long): FinalResult {

        val minute = TimeUnit.NANOSECONDS.toMinutes(finishTime)
        val second = TimeUnit.NANOSECONDS.toSeconds(finishTime)
        totalScore = if (minute < 1) totalScore * 2 else totalScore

        val finalResult = FinalResult(
            "$minute:$second",
            ((correctAnswerCount.toFloat() / gameplays.size) * 100).toInt(),
            totalScore,
            golds = totalScore / 2
        )

        return finalResult
    }

    private val handleUpdateDiaryCallback = object: Callback<Any> {
        override fun onResponse(call: Call<Any>, response: Response<Any>) {
            if(response.isSuccessful && response.code() == 200) {
                Log.e(PlayFragment::class.java.name, "??? ok")
            } else {
                Log.e(PlayFragment::class.java.name, "??? loi")
            }
        }

        override fun onFailure(call: Call<Any>, t: Throwable) {
            Log.e(PlayFragment::class.java.name, t.message?: "error message")
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
                            binding.tvLife.text = this.updatedUser.hearts.toString()
                            iOSDialogBuilder(requireContext())
                                .setTitle(requireContext().getString(R.string.confirm_otp))
                                .setSubtitle(requireContext().getString(R.string.buy_heart_success))
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

    private val handleUpdateGolds = object: Callback<UpdateUserResponseModel> {
        override fun onResponse(call: Call<UpdateUserResponseModel>, response: Response<UpdateUserResponseModel>) {

            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }

            if(response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {
                    this.data.apply {
                        val playerInfoJson = Gson().toJson(this.updatedUser)
                        localDataManager.setUserInfo(playerInfoJson)
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

    private val handleUpdateHeart = object: Callback<UpdateUserResponseModel> {
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
                            findNavController().navigate(R.id.action_playFragment_to_homeFragment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}