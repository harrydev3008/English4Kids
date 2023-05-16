package com.hisu.english4kids.ui.auth

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.gdacciaro.iOSDialog.iOSDialog
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hisu.english4kids.R
import com.hisu.english4kids.data.CONTENT_TYPE_JSON
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.AuthResponseModel
import com.hisu.english4kids.databinding.FragmentLoginBinding
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.widget.dialog.LoadingDialog
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding?= null
    private val binding get() = _binding!!

    private lateinit var patternPhoneNumber: Pattern
    private lateinit var mLoadingDialog: LoadingDialog
    private var phoneCheck = false
    private var passCheck = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        patternPhoneNumber = Pattern.compile("^(032|033|034|035|036|037|038|039|086|096|097|098|070|079|077|076|078|089|090|093|083|084|085|081|082|088|091|094|052|056|058|092|059|099|087)[0-9]{7}$")

        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)

        handleSwitchToRegister()
        handleLoginEvent()
        handleForgetPassword()
        handlePhoneNumberTextChange()
        handlePasswordTextChange()
    }

    private fun handleSwitchToRegister() = binding.tvRegisterNow.setOnClickListener {
        findNavController().navigate(R.id.login_to_regis)
    }

    private fun handleForgetPassword() = binding.tvForgotPassword.setOnClickListener {
        findNavController().navigate(R.id.forgot_password)
    }

    private fun handleLoginEvent() = binding.btnLogin.setOnClickListener {
        if (MyUtils.isNetworkAvailable(requireContext())) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("phone", binding.edtPhoneNumber.text.toString())
            jsonObject.addProperty("password", binding.edtPassword.text.toString())

            val loginBodyRequest = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), jsonObject.toString())

            requireActivity().runOnUiThread {
                mLoadingDialog.showDialog()
            }

            API.apiService.authLogin(loginBodyRequest).enqueue(handleLoginCallback)
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

    private fun handlePhoneNumberTextChange() = binding.edtPhoneNumber.addTextChangedListener {
        it?.apply {
            if (it.toString().isEmpty()) {
                binding.tilPhoneContainer.helperText = getString(R.string.empty_phone_err)
                phoneCheck = false
            } else if (!patternPhoneNumber.matcher(it.toString().trim()).matches()) {
                binding.tilPhoneContainer.helperText = getString(R.string.invalid_phone_format_err)
                phoneCheck = false
            } else {
                phoneCheck = true
                binding.tilPhoneContainer.helperText = ""
            }

            checkButtonLoginState()
        }
    }
    private fun handlePasswordTextChange() = binding.edtPassword.addTextChangedListener {
        it?.apply {
            if (it.toString().isEmpty()) {
                passCheck = false
                binding.tilPasswordContainer.helperText = getString(R.string.empty_password_err)
            } else {
                passCheck = true
                binding.tilPhoneContainer.helperText = ""
            }

            checkButtonLoginState()
        }
    }

    private fun checkButtonLoginState() {
        binding.btnLogin.isEnabled = phoneCheck && passCheck
    }

    private val handleLoginCallback = object: Callback<AuthResponseModel> {
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

                            findNavController().navigate(R.id.login_to_home)
                        }, 3 * 1000)
                    }
                }
            } else {
                requireActivity().runOnUiThread {
                    mLoadingDialog.dismissDialog()
                    iOSDialogBuilder(requireContext())
                        .setTitle(requireContext().getString(R.string.request_err))
                        .setSubtitle(requireContext().getString(R.string.wrong_phone_or_password))
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
            Log.e(LoginFragment::class.java.name, t.message ?: "error message")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}