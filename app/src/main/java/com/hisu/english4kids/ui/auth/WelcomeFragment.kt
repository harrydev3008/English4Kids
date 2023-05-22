package com.hisu.english4kids.ui.auth

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gdacciaro.iOSDialog.iOSDialog
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hisu.english4kids.R
import com.hisu.english4kids.data.CONTENT_TYPE_JSON
import com.hisu.english4kids.data.LEVEL_NEWBIE
import com.hisu.english4kids.data.LEVEL_STARTER
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.AuthResponseModel
import com.hisu.english4kids.databinding.FragmentWelcomeBinding
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.widget.dialog.LoadingDialog
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding?= null
    private val binding get() = _binding!!
    private var level:String = LEVEL_NEWBIE

    private val myArgs: WelcomeFragmentArgs by navArgs()
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)

        handleNewbieModeClick()
        handleStarterModeClick()
        handleSaveButtonEvent()
    }

    private fun handleSaveButtonEvent() = binding.btnSave.setOnClickListener {
        register()
    }

    private fun handleNewbieModeClick() = binding.layoutStarter.setOnClickListener {
        level = LEVEL_NEWBIE
        binding.layoutStarter.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_word_border_selected_green)
        binding.layoutInter.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_word_border)
    }

    private fun handleStarterModeClick() = binding.layoutInter.setOnClickListener {
        level = LEVEL_STARTER
        binding.layoutInter.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_word_border_selected_green)
        binding.layoutStarter.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_word_border)
    }

    private fun register() {
        if (MyUtils.isNetworkAvailable(requireContext())) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("phone", myArgs.phoneNumber)
            jsonObject.addProperty("username", myArgs.displayName)
            jsonObject.addProperty("password", myArgs.password)
            jsonObject.addProperty("level", level)

            val registerBodyRequest = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), jsonObject.toString())

            requireActivity().runOnUiThread {
                mLoadingDialog.showDialog()
            }

            API.apiService.authRegister(registerBodyRequest).enqueue(handleRegisterCallback)
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

    private val handleRegisterCallback = object: Callback<AuthResponseModel> {
        override fun onResponse(call: Call<AuthResponseModel>, response: Response<AuthResponseModel>) {

            if(response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {
                    this.data.apply {
                        val localDataManager = LocalDataManager()
                        localDataManager.init(requireContext())

                        val playerInfoJson = Gson().toJson(this.player)

                        localDataManager.setUserLoinState(true)
                        localDataManager.setUserInfo(playerInfoJson)

                        localDataManager.setUserAccessToken(this.accessToken)
                        localDataManager.setUserRefreshToken(this.refreshToken)

                        Handler(requireContext().mainLooper).postDelayed({
                            requireActivity().runOnUiThread {
                                mLoadingDialog.dismissDialog()
                            }

                            findNavController().navigate(R.id.action_welcomeFragment_to_homeFragment)
                        }, 3 * 1000)
                    }
                }
            } else {
                requireActivity().runOnUiThread {
                    mLoadingDialog.dismissDialog()
                    iOSDialogBuilder(requireContext())
                        .setTitle(requireContext().getString(R.string.request_err))
                        .setSubtitle(requireContext().getString(R.string.confirm_otp_err_occur_msg))
                        .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                            it.dismiss()
                        }.build().show()
                }
            }
        }

        override fun onFailure(call: Call<AuthResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
                iOSDialogBuilder(requireContext()).setTitle(requireContext().getString(R.string.request_err))
                    .setSubtitle(t.message?: requireContext().getString(R.string.confirm_otp_err_occur_msg))
                    .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                        it.dismiss()
                    }.build().show()
            }
            Log.e(WelcomeFragment::class.java.name, t.message ?: "error message")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}