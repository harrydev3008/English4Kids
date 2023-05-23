package com.hisu.english4kids.ui.auth

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gdacciaro.iOSDialog.iOSDialog
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.hisu.english4kids.R
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.SearchUserResponseModel
import com.hisu.english4kids.databinding.FragmentRegisterBinding
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.widget.dialog.LoadingDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var patternPhoneNumber: Pattern
    private var isLegit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        patternPhoneNumber = Pattern.compile("^(032|033|034|035|036|037|038|039|086|096|097|098|070|079|077|076|078|089|090|093|083|084|085|081|082|088|091|094|052|056|058|092|059|099|087)[0-9]{7}$")
        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)

        handlePhoneNumberFocusChange()
        handlePhoneNumberTextChange()
        handleDisplayNameTextChange()
        handlePasswordTextChange()
        handleConfirmPasswordTextChange()
        handleSwitchToLogin()
        handleLoginBtn()
    }

    private fun handlePhoneNumberFocusChange() = binding.edtPhoneNumber.setOnFocusChangeListener { view, isFocus ->
        run {
            if (!isFocus && validatePhoneNumber()) {
                if (MyUtils.isNetworkAvailable(requireContext())) {
                    requireActivity().runOnUiThread {
                        mLoadingDialog.showDialog()
                    }
                    API.apiService.searchUserByPhone(binding.edtPhoneNumber.text.toString()).enqueue(handleCheckPhoneNumberCallback)
                } else {
                    requireActivity().runOnUiThread {
                        isLegit = false
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
        }

    private fun handlePhoneNumberTextChange() = binding.edtPhoneNumber.addTextChangedListener {
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

    private fun handleDisplayNameTextChange() = binding.edtDisplayName.addTextChangedListener {
        it?.apply {
            if (it.toString().isEmpty()) {
                binding.tilDisplayNameContainer.helperText = getString(R.string.empty_display_err)
            } else if (it.toString().length < 4 || it.toString().length > 12) {
                binding.tilDisplayNameContainer.helperText = getString(R.string.invalid_display_err)
            } else {
                binding.tilDisplayNameContainer.helperText = ""
            }
        }
    }

    private fun handlePasswordTextChange() = binding.edtPassword.addTextChangedListener {
        if (it.toString().isEmpty()) {
            binding.tilPasswordContainer.helperText = getString(R.string.empty_password_err)
        } else if (it.toString().length < 8) {
            binding.tilPasswordContainer.helperText = getString(R.string.invalid_password_err)
        } else {
            binding.tilPasswordContainer.helperText = ""
        }
    }

    private fun handleConfirmPasswordTextChange() = binding.edtConfirmPassword.addTextChangedListener {
            if (it.toString().isEmpty() || it.toString() != binding.edtPassword.text.toString()) {
                binding.tilConfirmPasswordContainer.helperText =
                    getString(R.string.invalid_cf_password_err)
            } else {
                binding.tilConfirmPasswordContainer.helperText = ""
            }
        }

    private fun handleLoginBtn() = binding.btnLogin.setOnClickListener {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.btnLogin.windowToken, 0)

        if (validateData() && isLegit) {//todo: check isLegit
            val phoneNumber = binding.edtPhoneNumber.text.toString()
            val action = RegisterFragmentDirections.actionRegisterFragmentToCheckOTPFragment(
                phoneNumber = phoneNumber, authType = 1,
                displayName = binding.edtDisplayName.text.toString().trim(),
                password = binding.edtPassword.text.toString().trim()
            )
            findNavController().navigate(action)
        }
    }

    private fun handleSwitchToLogin() = binding.tvLoginNow.setOnClickListener {
        findNavController().navigate(R.id.regis_to_login)
    }

    private fun validatePhoneNumber(): Boolean {
        if (binding.edtPhoneNumber.text.toString().isEmpty())
            return false

        if (!patternPhoneNumber.matcher(binding.edtPhoneNumber.text.toString()).matches())
            return false

        return true;
    }

    private fun validateData(): Boolean {
        if (binding.edtPhoneNumber.text.toString().isEmpty()) {
            binding.tilPhoneContainer.helperText = getString(R.string.empty_phone_err)
            binding.edtPhoneNumber.requestFocus()
            return false
        }

        if (!patternPhoneNumber.matcher(binding.edtPhoneNumber.text.toString()).matches()) {
            binding.tilPhoneContainer.helperText = getString(R.string.invalid_phone_format_err)
            binding.edtPhoneNumber.requestFocus()
            return false
        }

        if (binding.edtDisplayName.text.toString().isEmpty()) {
            binding.tilDisplayNameContainer.helperText = getString(R.string.empty_display_err)
            binding.edtDisplayName.requestFocus()
            return false
        }

        if (binding.edtDisplayName.text.toString().length < 4 || binding.edtDisplayName.text.toString().length > 12) {
            binding.tilDisplayNameContainer.helperText = getString(R.string.invalid_display_err)
            binding.edtDisplayName.requestFocus()
            return false
        }

        if (binding.edtPassword.text.toString()
                .isEmpty() || binding.edtPassword.text.toString().length < 8
        ) {
            binding.tilPasswordContainer.helperText = getString(R.string.invalid_password_err)
            binding.edtPassword.requestFocus()
            return false
        }

        if (binding.edtConfirmPassword.text.toString().isEmpty() || binding.edtConfirmPassword.text.toString() != binding.edtPassword.text.toString()) {
            binding.tilConfirmPasswordContainer.helperText =
                getString(R.string.invalid_cf_password_err)
            binding.edtConfirmPassword.requestFocus()
            return false
        }

        return true
    }

    private val handleCheckPhoneNumberCallback = object: Callback<SearchUserResponseModel> {
        override fun onResponse(call: Call<SearchUserResponseModel>, response: Response<SearchUserResponseModel>) {

            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }

            if (response.isSuccessful && response.code() == STATUS_OK) { //user existed
                isLegit = false
                binding.tilPhoneContainer.helperText = getString(R.string.phone_existed)
            } else {
                isLegit = true
                binding.tilPhoneContainer.helperText = ""
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