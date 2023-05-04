package com.hisu.english4kids.ui.home

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hisu.english4kids.R
import com.hisu.english4kids.data.CONTENT_TYPE_JSON
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.AuthResponseModel
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.databinding.FragmentSettingBinding
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.widget.dialog.LoadingDialog
import com.hisu.english4kids.widget.dialog.SettingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding?= null
    private val binding get() = _binding!!

    private lateinit var currentUser: Player
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var localDataManager: LocalDataManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        handleSettingButton()
        handleEditProfileEvent()
        handleLogoutEvent()
        handleBackButton()
        //todo: thong ke
    }

    private fun initView() {
        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)

        localDataManager = LocalDataManager()
        localDataManager.init(requireContext())

        currentUser = Gson().fromJson(localDataManager.getUserInfo(), Player::class.java)

        binding.tvUsername.text = currentUser.username
        binding.tvPhoneNumber.text = currentUser.phone
        binding.cimvUserPfp.setImageBitmap(MyUtils.createImageFromText(requireContext(), currentUser.username))
    }

    private fun handleBackButton() = binding.btnHomepage.setOnClickListener {
        findNavController().popBackStack()
    }

    private fun handleSettingButton() = binding.btnSettingNotification.setOnClickListener {
        SettingDialog(requireContext(), Gravity.CENTER)
            .setSaveCallback(::handleSaveSetting)
            .showDialog()
    }

    private fun handleSaveSetting(isRemindLearning: Boolean, isRemindDaily: Boolean) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                localDataManager.setUserRemindLearningState(isRemindLearning)
                localDataManager.setUserRemindDailyRewardState(isRemindDaily)
            }
        }
    }

    private fun handleEditProfileEvent() = binding.btnEditProfile.setOnClickListener {
        Toast.makeText(requireContext(), "impl later", Toast.LENGTH_SHORT).show()
    }

    private fun handleLogoutEvent() = binding.tvLogout.setOnClickListener {
        iOSDialogBuilder(requireContext())
            .setTitle(requireContext().getString(R.string.confirm_otp))
            .setSubtitle(requireContext().getString(R.string.confirm_logout))
            .setNegativeListener(requireContext().getString(R.string.confirm_no)) {
                it.dismiss()
            }
            .setPositiveListener(requireContext().getString(R.string.confirm_yes)) {
                it.dismiss()

                requireActivity().runOnUiThread {
                    mLoadingDialog.showDialog()
                }

                val jsonObject = JsonObject()
                jsonObject.addProperty("id", currentUser.id)

                val logoutBodyRequest = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), jsonObject.toString())
                API.apiService.authLogout(logoutBodyRequest).enqueue(handleLogoutCallback)
            }.build().show()
    }

    private val handleLogoutCallback = object: Callback<AuthResponseModel> {
        override fun onResponse(call: Call<AuthResponseModel>, response: Response<AuthResponseModel>) {
            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }

            if(response.isSuccessful && response.code() == STATUS_OK) {

                localDataManager.setUserLoinState(false)
                localDataManager.setUserInfo("")//clear user info

                Handler(requireContext().mainLooper).postDelayed({
                    findNavController().navigate(R.id.action_settingFragment_to_registerFragment)
                }, 3 * 1000)
            }
            //todo: handle else case
        }

        override fun onFailure(call: Call<AuthResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }
            Log.e(SettingFragment::class.java.name, t.message ?: "error message")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}