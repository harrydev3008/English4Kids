package com.hisu.english4kids.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.FragmentRegisterBinding
import java.util.regex.Pattern

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

        handlePhoneNumberTextChange()
        handleLoginBtn()
    }

    private fun handlePhoneNumberTextChange() = binding.edtPhoneNumber.addTextChangedListener {
        it?.apply {
            binding.btnLogin.isEnabled = it.isNotEmpty()
        }
    }

    private fun handleLoginBtn() = binding.btnLogin.setOnClickListener {
        if (phoneNumberValidate()) {
            val phoneNumber = "+84${binding.edtPhoneNumber.text.toString().substring(1)}"
            val action = RegisterFragmentDirections.actionRegisterFragmentToCheckOTPFragment(phoneNumber)
            findNavController().navigate(action)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}