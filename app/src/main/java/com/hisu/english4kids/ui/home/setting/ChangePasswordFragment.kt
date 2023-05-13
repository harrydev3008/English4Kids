package com.hisu.english4kids.ui.home.setting

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
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
import com.hisu.english4kids.databinding.FragmentChangePasswordBinding
import com.hisu.english4kids.ui.auth.LoginFragment
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.widget.dialog.LoadingDialog
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding?= null
    private val binding get() = _binding!!
    private var oldPassCheck = false
    private var newPassCheck = false
    private var confirmPassCheck = false

    private lateinit var localDataManager: LocalDataManager
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)
        localDataManager = LocalDataManager()
        localDataManager.init(requireContext())

        handleSaveButton()
        handleBackButton()
        handleNewPasswordTextChange()
        handleOldPasswordTextChange()
        handleConfirmPasswordTextChange()
    }

    private fun handleBackButton() = binding.btnBack.setOnClickListener {
        findNavController().navigate(R.id.change_password_done)
    }

    private fun handleSaveButton() = binding.btnSave.setOnClickListener {
        if (MyUtils.isNetworkAvailable(requireContext())) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("oldPassword", binding.edtOldPassword.text.toString())
            jsonObject.addProperty("newPassword", binding.edtNewPassword.text.toString())

            val changePasswordBodyRequest = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), jsonObject.toString())

            requireActivity().runOnUiThread {
                mLoadingDialog.showDialog()
            }

            API.apiService.changePassword("Bearer ${localDataManager.getUserAccessToken()}",changePasswordBodyRequest).enqueue(handleChangePasswordCallback)
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

    private fun handleNewPasswordTextChange() = binding.edtNewPassword.addTextChangedListener {
        if (it.toString().isEmpty()) {
            newPassCheck = false
            binding.tilNewPasswordContainer.helperText = getString(R.string.empty_password_err)
        } else if (it.toString().length < 8) {
            newPassCheck = false
            binding.tilNewPasswordContainer.helperText = getString(R.string.invalid_password_err)
        } else {
            newPassCheck = true
            binding.tilNewPasswordContainer.helperText = ""
        }

        checkButtonSaveState()
    }

    private fun handleOldPasswordTextChange() = binding.edtOldPassword.addTextChangedListener {
        if (it.toString().isEmpty()) {
            oldPassCheck = false
            binding.tilOldPasswordContainer.helperText = getString(R.string.empty_password_err)
        } else {
            oldPassCheck = true
            binding.tilOldPasswordContainer.helperText = ""
        }

        checkButtonSaveState()
    }

    private fun handleConfirmPasswordTextChange() = binding.edtConfirmPassword.addTextChangedListener {
        if (it.toString().isEmpty() || it.toString() != binding.edtNewPassword.text.toString()) {
            confirmPassCheck = false
            binding.tilConfirmPasswordContainer.helperText = getString(R.string.invalid_cf_password_err)
        } else {
            confirmPassCheck = true
            binding.tilConfirmPasswordContainer.helperText = ""
        }

        checkButtonSaveState()
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
                            findNavController().navigate(R.id.change_password_done)
                        }.build().show()
                }
            } else {
                requireActivity().runOnUiThread {
                    mLoadingDialog.dismissDialog()
                    iOSDialogBuilder(requireContext())
                        .setTitle(requireContext().getString(R.string.request_err))
                        .setSubtitle(requireContext().getString(R.string.wrong_old_password))
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

    private fun checkButtonSaveState() {
        binding.btnSave.isEnabled = oldPassCheck && newPassCheck && confirmPassCheck
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}