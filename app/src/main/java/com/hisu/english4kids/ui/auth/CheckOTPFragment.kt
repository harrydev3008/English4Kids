package com.hisu.english4kids.ui.auth

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.FragmentCheckOtpBinding
import com.hisu.english4kids.widget.dialog.LoadingDialog
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

class CheckOTPFragment : Fragment() {

    private val binding get() = _binding!!
    private var timer: CountDownTimer? = null
    private val RESEND_DELAY_TIME = 99 * 1000L
    private val myArgs: CheckOTPFragmentArgs by navArgs()

    private var verificationID = ""
    private var forceResendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var _binding: FragmentCheckOtpBinding? = null

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        mAuth.setLanguageCode("vi")
        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)

        initOTPEditText()
        handleButtonVerifyOTP()
        handleButtonResendOTP()
        handleOTPVerification()
    }

    private fun countDownResendOTP() {
        binding.tvResend.isEnabled = false

        val timerFormatStr = DecimalFormat("00")

        timer = object : CountDownTimer(RESEND_DELAY_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                requireActivity().runOnUiThread {
                    binding.tvResend.text = String.format(
                        requireContext().getString(R.string.resend_otp_timer),
                        timerFormatStr.format(millisUntilFinished / 1000)
                    )
                }
            }

            override fun onFinish() {
                requireActivity().runOnUiThread {
                    binding.tvResend.text = requireContext().getString(R.string.resend_otp)
                    binding.tvResend.isEnabled = true
                }
            }
        }.start()
    }

    private fun disposeTimerInterval() {
        timer?.apply {
            this.cancel()
            timer = null
        }
    }

    private fun handleButtonResendOTP() = binding.tvResend.setOnClickListener {
        handleResendOTPVerification()
    }

    private fun handleButtonVerifyOTP() = binding.btnVerifyOtp.setOnClickListener {
        requireActivity().runOnUiThread {
            mLoadingDialog.showDialog()
        }
        val credential = PhoneAuthProvider.getCredential(verificationID, getUserInputOTPCode())
        signInWithPhoneAuthCredential(credential);
    }

    private fun navigateToNextPage() {
        //1 -> register, 2 -> forgot password
        when (myArgs.authType) {
            1 -> {
                register()
            }
            2 -> {
                forgotPassword()
            }
        }
    }

    private fun register() {
        val action = CheckOTPFragmentDirections.actionCheckOTPFragmentToWelcomeFragment(phoneNumber = myArgs.phoneNumber, displayName = myArgs.displayName, password = myArgs.password)
        findNavController().navigate(action)
    }

    private fun forgotPassword() {
        val action = CheckOTPFragmentDirections.otpToResetPwd(phone = myArgs.phoneNumber)
        findNavController().navigate(action)
    }

    private fun getUserInputOTPCode() = StringBuilder("")
        .append(binding.edtInputOtp1.text.toString())
        .append(binding.edtInputOtp2.text.toString())
        .append(binding.edtInputOtp3.text.toString())
        .append(binding.edtInputOtp4.text.toString())
        .append(binding.edtInputOtp5.text.toString())
        .append(binding.edtInputOtp6.text.toString()).toString()

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

    private fun handleOTPVerification() {
        val phoneNumber = "+84${myArgs.phoneNumber.substring(1)}"
        val options = generatePhoneAuthProviderOptions(phoneNumber)
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun handleResendOTPVerification() {
        binding.tvResend.isEnabled = false
        val phoneNumber = "+84${myArgs.phoneNumber.substring(1)}"
        val resendOTPOptions = generateResendPhoneAuthProviderOptions(phoneNumber)
        PhoneAuthProvider.verifyPhoneNumber(resendOTPOptions)
    }

    private fun generatePhoneAuthProviderOptions(phoneNumber: String) =
        PhoneAuthOptions.newBuilder()
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(otpHandlerCallback).build()

    private fun generateResendPhoneAuthProviderOptions(phoneNumber: String) =
        PhoneAuthOptions.newBuilder()
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setForceResendingToken(forceResendToken!!)
            .setCallbacks(otpResendHandlerCallback).build()

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity(), OnCompleteListener<AuthResult> { task ->
                requireActivity().runOnUiThread {
                    mLoadingDialog.dismissDialog()
                }

                if (task.isSuccessful) {
                    navigateToNextPage()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        binding.tvErrorMessage.visibility = View.VISIBLE
                    }
                }
            })
    }

    private inner class OTPKeyEvent(private var current: EditText, private var previous: EditText?) : View.OnKeyListener {
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (current.id != R.id.edt_input_otp_1 && current.text.toString().isEmpty()) {
                    //If current is empty then previous EditText's number will also be deleted
                    previous?.setText("")
                    previous?.requestFocus()
                } else
                    current.setText("")

                binding.btnVerifyOtp.isEnabled = false
                return true
            }
            return false
        }
    }

    private inner class OTPTextWatcher(private var current: EditText, var next: EditText?) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(inputText: Editable) {
            val text = inputText.toString()

            if (text.length == 1 && next != null)
                next?.requestFocus()
            else if(text.length == 1 && next == null) {
                current.clearFocus()
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view!!.windowToken, 0)
            }

            binding.btnVerifyOtp.isEnabled = getUserInputOTPCode().length > 5
        }
    }

    private val otpHandlerCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(exeption: FirebaseException) {
                Log.e(this@CheckOTPFragment.javaClass.name, "message: ${exeption.message}\nlocalizedMessage: ${exeption.localizedMessage}")

                iOSDialogBuilder(requireContext())
                    .setTitle(requireContext().getString(R.string.confirm_otp_err))
                    .setSubtitle(requireContext().getString(R.string.confirm_otp_err_occur_msg))
                    .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                        it.dismiss()
                    }.build().show()
            }

            override fun onCodeSent(verificationId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken)
                verificationID = verificationId
                this@CheckOTPFragment.forceResendToken = forceResendingToken
                countDownResendOTP()
            }
        }

    private val otpResendHandlerCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(exeption: FirebaseException) {
                binding.tvErrorMessage.visibility = View.VISIBLE
                Log.e(this@CheckOTPFragment.javaClass.name, "message: ${exeption.message}\nlocalizedMessage: ${exeption.localizedMessage}")
            }

            override fun onCodeSent(verificationId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken)
                verificationID = verificationId
                this@CheckOTPFragment.forceResendToken = forceResendingToken
                countDownResendOTP()
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        disposeTimerInterval()
        _binding = null
    }
}