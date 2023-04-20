package com.hisu.english4kids.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.hisu.english4kids.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

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

        handleUserNameTextChange()
        handlePhoneNumberTextChange()
        handleLoginBtn()
    }

    private fun handleUserNameTextChange() = binding.edtUsername.addTextChangedListener {
        it?.apply {
            binding.btnLogin.isEnabled =
                it.isNotEmpty() && binding.edtPhoneNumber.text.toString().isNotEmpty()

            if (it.length > 16) {
                binding.tilUsernameContainer.helperText =
                    "* Tên hiển thị không được vượt quá 16 kí tự!"
            } else {
                binding.tilUsernameContainer.helperText = ""
            }
        }
    }

    private fun handlePhoneNumberTextChange() = binding.edtPhoneNumber.addTextChangedListener {
        it?.apply {
            binding.btnLogin.isEnabled =
                it.isNotEmpty() && binding.edtUsername.text.toString().isNotEmpty()
        }
    }

    private fun handleLoginBtn() = binding.btnLogin.setOnClickListener {
        if (phoneNumberValidate()) {
            val phoneNumber = "+84${binding.edtPhoneNumber.text.toString().substring(1)}"
            val action = RegisterFragmentDirections.actionRegisterFragmentToCheckOTPFragment(phoneNumber)
            findNavController().navigate(action)
        }
    }

    //todo: impl later
    private fun phoneNumberValidate() = true

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}