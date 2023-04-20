package com.hisu.english4kids.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.hisu.english4kids.R
import com.hisu.english4kids.databinding.FragmentCheckOtpBinding
import com.hisu.english4kids.ui.dialog.LoadingDialog
import es.dmoral.toasty.Toasty
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class CheckOTPFragment : Fragment() {

    private val binding get() = _binding!!
    private val timer = Timer()
    private val RESEND_DELAY_TIME = 2 * 10 //90 secs, but inorder to be ez to test, it will be 20 for now ;)
    private val myArgs: CheckOTPFragmentArgs by navArgs()

    private var verificationID = ""
    private var _binding: FragmentCheckOtpBinding? = null

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mAuth.setLanguageCode("vi")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)

        initOTPEditText()
        handleResendOTP()
        handleOTPVerification()
        handleButtonVerifyOTP()
    }

    private fun countDownResendOTP() {
        binding.tvResend.isEnabled = false

        val counter = AtomicInteger(RESEND_DELAY_TIME)
        val timerFormatStr = DecimalFormat("00")

        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                counter.decrementAndGet()

                requireActivity().runOnUiThread {
                    binding.tvResend.text = String.format(
                        requireContext().getString(R.string.resend_otp_timer),
                        timerFormatStr.format(counter.get())
                    )

                    if (counter.get() == 0) {
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

    private fun handleButtonVerifyOTP() = binding.btnVerifyOtp.setOnClickListener {
        mLoadingDialog.showDialog()
        val credential = PhoneAuthProvider.getCredential(verificationID, getUserInputOTPCode())
        signInWithPhoneAuthCredential(credential);
    }

    private fun navigateToNextPage() {
        timer.cancel()
        findNavController().navigate(R.id.otp_to_home)
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
        val options = generatePhoneAuthProviderOptions(myArgs.phoneNumber)
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun generatePhoneAuthProviderOptions(phoneNumber: String) =
        PhoneAuthOptions.newBuilder()
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(otpHandlerCallback).build()

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mLoadingDialog.dismissDialog()
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity(), OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    navigateToNextPage()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        iOSDialogBuilder(requireContext())
                            .setTitle(requireContext().getString(R.string.confirm_otp_err))
                            .setSubtitle(requireContext().getString(R.string.confirm_otp_err_occur_msg))
                            .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                                it.dismiss()
                            }.build().show()
                    }
                }
            })
    }

    inner class OTPKeyEvent(private var current: EditText, private var previous: EditText?) : View.OnKeyListener {
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

    inner class OTPTextWatcher(private var current: EditText, var next: EditText?) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(inputText: Editable) {
            val text = inputText.toString()

            if (text.length == 1 && next != null)
                next?.requestFocus()
            else
                current.requestFocus()

            binding.btnVerifyOtp.isEnabled = getUserInputOTPCode().length > 5
        }
    }

    private val otpHandlerCallback =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(exeption: FirebaseException) {
                binding.tvErrorMessage.visibility = View.VISIBLE
                Log.e(this@CheckOTPFragment.javaClass.name, "message: ${exeption.message}\nlocalizedMessage: ${exeption.localizedMessage}")
            }

            override fun onCodeSent(
                verificationId: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, forceResendingToken)
                verificationID = verificationId
                countDownResendOTP()
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
        _binding = null
    }
}