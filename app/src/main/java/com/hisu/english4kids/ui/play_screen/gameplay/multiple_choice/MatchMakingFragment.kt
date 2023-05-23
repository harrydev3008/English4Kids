package com.hisu.english4kids.ui.play_screen.gameplay.multiple_choice

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gdacciaro.iOSDialog.iOSDialog
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.Gson
import com.hisu.english4kids.R
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.ExamResponseModel
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.databinding.FragmentMatchMakingBinding
import com.hisu.english4kids.ui.auth.CheckOTPFragment
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.widget.dialog.WalkingLoadingDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MatchMakingFragment : Fragment() {

    private var _binding: FragmentMatchMakingBinding? = null
    private val binding get() = _binding!!

    private lateinit var walkingLoadingDialog: WalkingLoadingDialog
    private lateinit var localDataManager: LocalDataManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMatchMakingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localDataManager = LocalDataManager()
        localDataManager.init(requireContext())
        walkingLoadingDialog = WalkingLoadingDialog(requireContext())

        handleFindMatchButton()
        handleBackButton()
    }

    private fun handleFindMatchButton() = binding.btnFindMatch.setOnClickListener {
        if (MyUtils.isNetworkAvailable(requireContext())) {
            requireActivity().runOnUiThread {
                walkingLoadingDialog.showDialog()
            }

            val user = Gson().fromJson(localDataManager.getUserInfo(), Player::class.java)
            API.apiService.getExam("Bearer ${localDataManager.getUserAccessToken()}").enqueue(handleGetExamCallback)
        } else {
            requireActivity().runOnUiThread {
                iOSDialogBuilder(requireContext())
                    .setTitle(requireContext().getString(R.string.confirm_otp))
                    .setSubtitle(requireContext().getString(R.string.err_network_not_available))
                    .setPositiveListener(
                        requireContext().getString(R.string.confirm_otp),
                        iOSDialog::dismiss
                    ).build().show()
            }
        }
    }

    private fun handleBackButton() = binding.btnBack.setOnClickListener {
        findNavController().navigate(R.id.action_matchMakingFragment_to_homeFragment)
    }

    private val handleGetExamCallback = object: Callback<ExamResponseModel> {
        override fun onResponse(call: Call<ExamResponseModel>, response: Response<ExamResponseModel>) {
            if(response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {

                    val action = MatchMakingFragmentDirections.actionMatchMakingFragmentToMultipleChoiceContainerFragment(examQuestions = Gson().toJson(this.data.exam))

                    Handler(requireContext().mainLooper).postDelayed({
                        requireActivity().runOnUiThread {
                            walkingLoadingDialog.dismissDialog()
                        }

                        findNavController().navigate(action)
                    }, 3 * 1000)
                }
            } else {
                requireActivity().runOnUiThread {
                    walkingLoadingDialog.dismissDialog()
                    iOSDialogBuilder(requireContext())
                        .setTitle(requireContext().getString(R.string.request_err))
                        .setSubtitle(requireContext().getString(R.string.get_exam_err))
                        .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                            it.dismiss()
                        }.build().show()
                }
            }
        }

        override fun onFailure(call: Call<ExamResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                walkingLoadingDialog.dismissDialog()
                iOSDialogBuilder(requireContext()).setTitle(requireContext().getString(R.string.request_err))
                    .setSubtitle(t.message?: requireContext().getString(R.string.err_network_not_connected))
                    .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                        it.dismiss()
                    }.build().show()
            }
            Log.e(MatchMakingFragment::class.java.name, t.localizedMessage ?: "error message")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}