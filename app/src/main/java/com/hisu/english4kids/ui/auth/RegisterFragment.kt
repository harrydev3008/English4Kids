package com.hisu.english4kids.ui.auth

import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.FragmentRegisterBinding
import com.hisu.english4kids.ui.dialog.LoadingDialog


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
            val dialog = LoadingDialog(requireContext(), Gravity.CENTER)
            dialog.showDialog()
            Handler(requireContext().mainLooper).postDelayed({
                dialog.dismissDialog()
                findNavController().navigate(R.id.action_registerFragment_to_checkOTPFragment)
            }, 10 * 1000)

        }
    }

    //todo: impl later
    private fun phoneNumberValidate() = true

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}