package com.hisu.english4kids.ui.auth

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hisu.english4kids.R
import com.hisu.english4kids.data.CONTENT_TYPE_JSON
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.AuthResponseModel
import com.hisu.english4kids.data.network.response_model.SearchUserResponseModel
import com.hisu.english4kids.databinding.FragmentCheckOtpBinding
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.widget.dialog.LoadingDialog
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

class CheckOTPFragment : Fragment() {

    private val binding get() = _binding!!
    private var timer: CountDownTimer? = null
    private val RESEND_DELAY_TIME = 30 * 1000L//todo: set to 60 or 90s later
    private val myArgs: CheckOTPFragmentArgs by navArgs()

    private var verificationID = ""
    private var forceResendToken: PhoneAuthProvider.ForceResendingToken? = null
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
        API.apiService.searchUserByPhone(myArgs.phoneNumber).enqueue(handleCheckPhoneNumberCallback)
    }

    private fun checkUserExist(isExist: Boolean) {
        if(isExist) {
            requireActivity().runOnUiThread {
                mLoadingDialog.showDialog()
            }

            val jsonObject = JsonObject()
            jsonObject.addProperty("phone", myArgs.phoneNumber)

            val loginBodyRequest = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), jsonObject.toString())
            API.apiService.authLogin(loginBodyRequest).enqueue(handleLoginCallback)
        } else {
            val action = CheckOTPFragmentDirections.actionCheckOTPFragmentToDisplayNameFragment(myArgs.phoneNumber)
            findNavController().navigate(action)
        }
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

    private inner class OTPKeyEvent(private var current: EditText, private var previous: EditText?) :
        View.OnKeyListener {
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
                Log.e(
                    this@CheckOTPFragment.javaClass.name,
                    "message: ${exeption.message}\nlocalizedMessage: ${exeption.localizedMessage}"
                )

                iOSDialogBuilder(requireContext())
                    .setTitle(requireContext().getString(R.string.confirm_otp_err))
                    .setSubtitle(requireContext().getString(R.string.confirm_otp_err_occur_msg))
                    .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                        it.dismiss()
                    }.build().show()
            }

            override fun onCodeSent(
                verificationId: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, forceResendingToken)
                verificationID = verificationId
                this@CheckOTPFragment.forceResendToken = forceResendingToken
                countDownResendOTP()
            }
        }

    private val otpResendHandlerCallback =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(exeption: FirebaseException) {
                binding.tvErrorMessage.visibility = View.VISIBLE
                Log.e(
                    this@CheckOTPFragment.javaClass.name,
                    "message: ${exeption.message}\nlocalizedMessage: ${exeption.localizedMessage}"
                )
            }

            override fun onCodeSent(
                verificationId: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, forceResendingToken)
                verificationID = verificationId
                this@CheckOTPFragment.forceResendToken = forceResendingToken
                countDownResendOTP()
            }
        }

    private val handleCheckPhoneNumberCallback = object: Callback<SearchUserResponseModel> {
        override fun onResponse(call: Call<SearchUserResponseModel>, response: Response<SearchUserResponseModel>) {
            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }

            if(response.isSuccessful && response.code() == STATUS_OK) { //user existed
                response.body()?.apply {
                    val searchUserResponseModel = this
                    Log.e("test", searchUserResponseModel.data?.user!!.phone)
                    checkUserExist(true)
                }
            } else {
                checkUserExist(false)
            }
        }

        override fun onFailure(call: Call<SearchUserResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }
            Log.e(CheckOTPFragment::class.java.name, t.message ?: "error message")
        }
    }

    private val handleLoginCallback = object: Callback<AuthResponseModel> {
        override fun onResponse(call: Call<AuthResponseModel>, response: Response<AuthResponseModel>) {

            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }

            if(response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {
                    this.data?.apply {
                        val localDataManager = LocalDataManager()
                        localDataManager.init(requireContext())

                        val playerInfoJson = Gson().toJson(this.player)

                        localDataManager.setUserLoinState(true)
                        localDataManager.setUserInfo(playerInfoJson)

                        findNavController().navigate(R.id.otp_to_home)
                    }
                }
            } else {
                response.errorBody()?.apply {
                    iOSDialogBuilder(requireContext())
                        .setTitle(requireContext().getString(R.string.confirm_otp_err))
                        .setSubtitle(this.string())
                        .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                            it.dismiss()
                        }.build().show()
                }
            }
        }

        override fun onFailure(call: Call<AuthResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }
            Log.e(CheckOTPFragment::class.java.name, t.message ?: "error message")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposeTimerInterval()
        _binding = null
    }
}