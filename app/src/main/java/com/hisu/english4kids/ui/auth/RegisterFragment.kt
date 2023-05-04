package com.hisu.english4kids.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.hisu.english4kids.R
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.SearchUserResponseModel
import com.hisu.english4kids.databinding.FragmentRegisterBinding
import com.hisu.english4kids.widget.dialog.LoadingDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var mLoadingDialog: LoadingDialog

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

        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)

        handlePhoneNumberTextChange()
        handleLoginBtn()
    }

    private fun handlePhoneNumberTextChange() = binding.edtPhoneNumber.addTextChangedListener {
        it?.apply {
            binding.btnLogin.isEnabled = it.isNotEmpty()
            binding.tilPhoneContainer.helperText = ""
        }
    }

    private fun handleLoginBtn() = binding.btnLogin.setOnClickListener {
        if (phoneNumberValidate()) {
            requireActivity().runOnUiThread {
                mLoadingDialog.showDialog()
            }

            API.apiService.searchUserByPhone(binding.edtPhoneNumber.text.toString()).enqueue(handleCheckPhoneNumberCallback)
        }
    }

    private fun phoneNumberValidate(): Boolean {

        val patternPhoneNumber = Pattern.compile("^(032|033|034|035|036|037|038|039|086|096|097|098|070|079|077|076|078|089|090|093|083|084|085|081|082|088|091|094|052|056|058|092|059|099|087)[0-9]{7}$")

        if (!patternPhoneNumber.matcher(binding.edtPhoneNumber.text.toString()).matches()) {
            binding.tilPhoneContainer.helperText = getString(R.string.invalid_phone_format_err);
            return false;
        }

        return true
    }

    private val handleCheckPhoneNumberCallback = object: Callback<SearchUserResponseModel> {
        override fun onResponse(call: Call<SearchUserResponseModel>, response: Response<SearchUserResponseModel>) {
            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }

            val phoneNumber = binding.edtPhoneNumber.text.toString()

            if(response.isSuccessful && response.code() == STATUS_OK) { //user existed
                //1 -> login, 0 -> register
                val action = RegisterFragmentDirections.actionRegisterFragmentToCheckOTPFragment(phoneNumber, authType = 1)
                findNavController().navigate(action)
            } else {
                val action = RegisterFragmentDirections.actionRegisterFragmentToCheckOTPFragment(phoneNumber, authType = 0)
                findNavController().navigate(action)
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