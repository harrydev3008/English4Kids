package com.hisu.imastermatcher.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentCheckOtpBinding
import es.dmoral.toasty.Toasty
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class CheckOTPFragment : Fragment() {

    private var _binding: FragmentCheckOtpBinding? = null
    private val binding get() = _binding!!

    private val timer = Timer()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initOTPEditText()
        handleResendOTP()
        handleVerifyOTP()
        countDownResendOTP()
    }

    private fun countDownResendOTP() {
        binding.tvResend.isEnabled = false

        val counter = AtomicInteger(29)
        val timerFormatStr = DecimalFormat("00")

        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                counter.decrementAndGet()

                requireActivity().runOnUiThread {
                    binding.tvResend.text = String.format(requireContext().getString(R.string.resend_otp_timer), timerFormatStr.format(counter.get()))

                    if(counter.get() == 0) {
                        timer.cancel()
                        binding.tvResend.text = requireContext().getString(R.string.resend_otp)
                        binding.tvResend.isEnabled = true
                    }
                }
            }
        }

        timer.schedule(timerTask, 0, 1000)
    }

    private fun handleResendOTP() = binding.tvResend.setOnClickListener {
        Toasty.error(requireContext(), "Resent OTP", Toasty.LENGTH_SHORT).show()
    }

    private fun handleVerifyOTP() = binding.btnVerifyOtp.setOnClickListener {
        //todo: impl later
        timer.cancel()

        findNavController().navigate(R.id.otp_to_home)
    }

    private fun initOTPEditText() {
        binding.edtInputOtp1.requestFocus()
        binding.edtInputOtp1.addTextChangedListener(OTPTextWatcher(binding.edtInputOtp1, binding.edtInputOtp2))
        binding.edtInputOtp2.addTextChangedListener(OTPTextWatcher(binding.edtInputOtp2, binding.edtInputOtp3))
        binding.edtInputOtp3.addTextChangedListener(OTPTextWatcher(binding.edtInputOtp3, binding.edtInputOtp4))
        binding.edtInputOtp4.addTextChangedListener(OTPTextWatcher(binding.edtInputOtp4, binding.edtInputOtp5))
        binding.edtInputOtp5.addTextChangedListener(OTPTextWatcher(binding.edtInputOtp5, binding.edtInputOtp6))
        binding.edtInputOtp6.addTextChangedListener(OTPTextWatcher(binding.edtInputOtp6,null))

        binding.edtInputOtp1.setOnKeyListener(OTPKeyEvent(binding.edtInputOtp1, null))
        binding.edtInputOtp2.setOnKeyListener(OTPKeyEvent(binding.edtInputOtp2, binding.edtInputOtp1))
        binding.edtInputOtp3.setOnKeyListener(OTPKeyEvent(binding.edtInputOtp3, binding.edtInputOtp2))
        binding.edtInputOtp4.setOnKeyListener(OTPKeyEvent(binding.edtInputOtp4, binding.edtInputOtp3))
        binding.edtInputOtp5.setOnKeyListener(OTPKeyEvent(binding.edtInputOtp5, binding.edtInputOtp4))
        binding.edtInputOtp6.setOnKeyListener(OTPKeyEvent(binding.edtInputOtp6, binding.edtInputOtp5))
    }

    inner class OTPKeyEvent(
        var current: EditText,
        var previous: EditText?
    ) : View.OnKeyListener {
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (current.id != R.id.edt_input_otp_1 && current.text.toString().isEmpty()) {
                    //If current is empty then previous EditText's number will also be deleted
                    previous?.setText("")
                    previous?.requestFocus()
                } else {
                    current.setText("")
                }

                binding.btnVerifyOtp.isEnabled = false
                return true
            }
            return false
        }
    }

    inner class OTPTextWatcher(
        var current: EditText,
        var next: EditText?
    ) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            val text = s.toString()

            if (text.length == 1 && next != null) {
                next?.requestFocus()
            } else {
                current.requestFocus()
            }

            val otpBuilder = StringBuilder("")
            otpBuilder
                .append(binding.edtInputOtp1.text)
                .append(binding.edtInputOtp2.text)
                .append(binding.edtInputOtp3.text)
                .append(binding.edtInputOtp4.text)
                .append(binding.edtInputOtp5.text)
                .append(binding.edtInputOtp6.text)

            binding.btnVerifyOtp.isEnabled = otpBuilder.length > 5
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
        _binding = null
    }
}