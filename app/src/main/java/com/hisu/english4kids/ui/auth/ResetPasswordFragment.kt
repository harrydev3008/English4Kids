package com.hisu.english4kids.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gdacciaro.iOSDialog.iOSDialog
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.JsonObject
import com.hisu.english4kids.R
import com.hisu.english4kids.data.CONTENT_TYPE_JSON
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.AuthResponseModel
import com.hisu.english4kids.databinding.FragmentResetPasswordBinding
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.widget.dialog.LoadingDialog
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding?= null
    private val binding get() = _binding!!
    private val myArgs: ResetPasswordFragmentArgs by navArgs()
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)

        handlePasswordTextChange()
        handleButtonResetPassword()
    }

    private fun handlePasswordTextChange() = binding.edtPassword.addTextChangedListener {
        if (it.toString().isEmpty()) {
            binding.tilPasswordContainer.helperText = getString(R.string.empty_password_err)
        } else if (it.toString().length < 8) {
            binding.tilPasswordContainer.helperText = getString(R.string.invalid_password_err)
        } else {
            binding.tilPasswordContainer.helperText = ""
            binding.btnSave.isEnabled = true
        }
    }

    private fun handleButtonResetPassword() = binding.btnSave.setOnClickListener {
        if (MyUtils.isNetworkAvailable(requireContext())) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("phone", myArgs.phone)
            jsonObject.addProperty("newPassword", binding.edtPassword.text.toString())

            val changePasswordBodyRequest = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), jsonObject.toString())

            requireActivity().runOnUiThread {
                mLoadingDialog.showDialog()
            }

            API.apiService.recoverPassword(changePasswordBodyRequest).enqueue(handleChangePasswordCallback)
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

    private val handleChangePasswordCallback = object: Callback<AuthResponseModel> {
        override fun onResponse(call: Call<AuthResponseModel>, response: Response<AuthResponseModel>) {
            if(response.isSuccessful && response.code() == STATUS_OK) {
                requireActivity().runOnUiThread {
                    mLoadingDialog.dismissDialog()
                    iOSDialogBuilder(requireContext())
                        .setTitle(requireContext().getString(R.string.confirm_otp))
                        .setSubtitle(requireContext().getString(R.string.change_password_success))
                        .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                            it.dismiss()
                            findNavController().navigate(R.id.reset_pwd_to_login)
                        }.build().show()
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
            Log.e(LoginFragment::class.java.name, t.message ?: "error message")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}