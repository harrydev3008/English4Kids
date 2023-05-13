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
import com.gdacciaro.iOSDialog.iOSDialog
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.hisu.english4kids.R
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.SearchUserResponseModel
import com.hisu.english4kids.databinding.FragmentChangePasswordBinding
import com.hisu.english4kids.databinding.FragmentForgotPasswordBinding
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.widget.dialog.LoadingDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding?= null
    private val binding get() = _binding!!

    private lateinit var patternPhoneNumber: Pattern
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        patternPhoneNumber = Pattern.compile("^(032|033|034|035|036|037|038|039|086|096|097|098|070|079|077|076|078|089|090|093|083|084|085|081|082|088|091|094|052|056|058|092|059|099|087)[0-9]{7}$")
        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)
        handleBackButton()
        handlePhoneNumberTextChange()
        handleNextButton()
    }

    private fun handleBackButton() = binding.btnBack.setOnClickListener {
        findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
    }

    private fun handlePhoneNumberTextChange() = binding.edtPhone.addTextChangedListener {
        it?.apply {
            if (it.toString().isEmpty()) {
                binding.tilPhoneContainer.helperText = getString(R.string.empty_phone_err)
            } else if (!patternPhoneNumber.matcher(it.toString().trim()).matches()) {
                binding.tilPhoneContainer.helperText = getString(R.string.invalid_phone_format_err)
            } else {
                binding.tilPhoneContainer.helperText = ""
            }
        }
    }

    private fun validatePhoneNumber(): Boolean {
        if (binding.edtPhone.text.toString().isEmpty()) {
            binding.tilPhoneContainer.helperText = getString(R.string.empty_phone_err)
            binding.edtPhone.requestFocus()
            return false
        }

        if (!patternPhoneNumber.matcher(binding.edtPhone.text.toString()).matches()) {
            binding.tilPhoneContainer.helperText = getString(R.string.invalid_phone_format_err)
            binding.edtPhone.requestFocus()
            return false
        }

        return true
    }

    private fun handleNextButton() = binding.btnSave.setOnClickListener {
        if(validatePhoneNumber()) {
            if (MyUtils.isNetworkAvailable(requireContext())) {
                requireActivity().runOnUiThread {
                    mLoadingDialog.showDialog()
                }
                API.apiService.searchUserByPhone(binding.edtPhone.text.toString()).enqueue(handleCheckPhoneNumberCallback)
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
    }

    private val handleCheckPhoneNumberCallback = object: Callback<SearchUserResponseModel> {
        override fun onResponse(call: Call<SearchUserResponseModel>, response: Response<SearchUserResponseModel>) {

            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }

            if (response.isSuccessful && response.code() == STATUS_OK) { //user existed
                val action = ForgotPasswordFragmentDirections.resetPassword(phoneNumber = binding.edtPhone.text.toString().trim(), authType = 2)
                findNavController().navigate(action)
            } else {
                binding.tilPhoneContainer.helperText = getString(R.string.phone_not_existed)
            }
        }

        override fun onFailure(call: Call<SearchUserResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
                iOSDialogBuilder(requireContext()).setTitle(requireContext().getString(R.string.request_err))
                    .setSubtitle(t.message?: requireContext().getString(R.string.err_network_not_connected))
                    .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                        it.dismiss()
                    }.build().show()
            }
            Log.e(CheckOTPFragment::class.java.name, t.localizedMessage ?: "error message")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}