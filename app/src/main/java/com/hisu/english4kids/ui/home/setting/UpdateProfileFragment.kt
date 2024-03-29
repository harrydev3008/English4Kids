package com.hisu.english4kids.ui.home.setting

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gdacciaro.iOSDialog.iOSDialog
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hisu.english4kids.MyApplication
import com.hisu.english4kids.R
import com.hisu.english4kids.data.CONTENT_TYPE_JSON
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.AuthResponseModel
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.data.network.response_model.UpdateUserResponseModel
import com.hisu.english4kids.data.room_db.repository.PlayerRepository
import com.hisu.english4kids.data.room_db.view_model.PlayerViewModel
import com.hisu.english4kids.data.room_db.view_model.PlayerViewModelProviderFactory
import com.hisu.english4kids.databinding.FragmentUpdateProfileBinding
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.widget.dialog.LoadingDialog
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateProfileFragment : Fragment() {

    private var _binding: FragmentUpdateProfileBinding?= null
    private val binding get() = _binding!!

    private lateinit var currentUser: Player
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var localDataManager: LocalDataManager
    private var isDataChanged = false

    private val playerViewModel: PlayerViewModel by activityViewModels() {
        PlayerViewModelProviderFactory(
            PlayerRepository(
                requireActivity().applicationContext,
                (activity?.application as MyApplication).database.playerDAO()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        handleBackButton()
        handleSaveButton()
        handleLogoutButton()
        handleEditTextChange()
        handleChangePassword()
    }

    private fun initView() {
        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)

        localDataManager = LocalDataManager()
        localDataManager.init(requireContext())

        val playJson = localDataManager.getUserInfo()

        if(playJson.isNullOrEmpty()) return

        currentUser = Gson().fromJson(playJson, Player::class.java)
        playerViewModel.getPlayerInfo(currentUser.id).observe(this.viewLifecycleOwner) {
            bindUserData(it)
        }
    }

    private fun bindUserData(player: Player) = binding.apply {
        binding.edtUserName.setText(player.username)
        binding.edtUserName.hint = player.username

        binding.edtPhoneNumber.setText(player.phone)
        binding.edtPhoneNumber.hint = player.phone

        binding.cimvUserPfp.setImageBitmap(MyUtils.createImageFromText(requireContext(), player.username))

        val year = player.registerDate.substring(0, 4)
        val month = player.registerDate.substring(5, 7)

        binding.tvJoinDate.text = String.format(
            requireContext().getString(R.string.join_date_pattern),
            month, year
        )
    }

    private fun handleBackButton() = binding.btnBack.setOnClickListener {
        if(!isDataChanged) {
            findNavController().navigate(R.id.action_updateProfileFragment_to_homeFragment)
        } else {
            iOSDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.confirm_otp))
                .setSubtitle(requireContext().getString(R.string.err_unsaved_update))
                .setPositiveListener(requireContext().getString(R.string.confirm_yes)) {
                    it.dismiss()
                    findNavController().popBackStack()
                }
                .setNegativeListener(requireContext().getString(R.string.confirm_no), iOSDialog::dismiss)
                .build().show()
        }
    }

    private fun handleChangePassword() = binding.edtPassword.setOnClickListener {
        findNavController().navigate(R.id.change_password)
    }

    private fun handleSaveButton() = binding.btnSave.setOnClickListener {
        if(MyUtils.isNetworkAvailable(requireContext())) {
            binding.edtUserName.clearFocus()
            handleSaveUserInfoEvent()
        } else {
            iOSDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.confirm_otp))
                .setSubtitle(requireContext().getString(R.string.err_network_not_available))
                .setPositiveListener(requireContext().getString(R.string.confirm_otp), iOSDialog::dismiss).build().show()
        }
    }

    private fun handleEditTextChange() = binding.edtUserName.addTextChangedListener {
        it?.apply {
            isDataChanged = it.toString() != currentUser.username
        }
    }

    private fun handleSaveUserInfoEvent() {
        requireActivity().runOnUiThread {
            mLoadingDialog.showDialog()
        }

        val jsonObject = JsonObject()
        jsonObject.addProperty("username", binding.edtUserName.text.toString())

        val updateUserBodyRequest = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), jsonObject.toString())
        API.apiService.updateUserInfo("Bearer ${localDataManager.getUserAccessToken()}",updateUserBodyRequest).enqueue(handleUpdateCallback)
    }

    private fun handleLogoutButton() = binding.btnLogout.setOnClickListener {
        if(MyUtils.isNetworkAvailable(requireContext())) {
            handleLogoutEvent()
        } else {
            iOSDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.confirm_otp))
                .setSubtitle(requireContext().getString(R.string.err_network_not_available))
                .setPositiveListener(requireContext().getString(R.string.confirm_otp), iOSDialog::dismiss).build().show()
        }
    }

    private fun handleLogoutEvent() {
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
            if(response.isSuccessful && response.code() == STATUS_OK) {

                localDataManager.setUserLoinState(false)

                Handler(requireContext().mainLooper).postDelayed({
                    requireActivity().runOnUiThread {
                        mLoadingDialog.dismissDialog()
                    }
                    findNavController().navigate(R.id.profile_to_login)
                }, 3 * 1000)
            } else {
                requireActivity().runOnUiThread {
                    mLoadingDialog.dismissDialog()
                    iOSDialogBuilder(requireContext())
                        .setTitle(requireContext().getString(R.string.request_err))
                        .setSubtitle(requireContext().getString(R.string.confirm_otp_err_occur_msg))
                        .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                            it.dismiss()
                        }.build().show()
                }
            }
        }

        override fun onFailure(call: Call<AuthResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
                iOSDialogBuilder(requireContext())
                    .setTitle(requireContext().getString(R.string.request_err))
                    .setSubtitle(requireContext().getString(R.string.err_network_not_connected))
                    .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                        it.dismiss()
                    }.build().show()
            }
            Log.e(UpdateProfileFragment::class.java.name, t.message ?: "error message")
        }
    }

    private val handleUpdateCallback = object: Callback<UpdateUserResponseModel> {
        override fun onResponse(call: Call<UpdateUserResponseModel>, response: Response<UpdateUserResponseModel>) {
            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }

            if(response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {
                    this.data.apply {
                        val playerInfoJson = Gson().toJson(this.updatedUser)
                        localDataManager.setUserInfo(playerInfoJson)
                        playerViewModel.updatePlayer(this.updatedUser)

                        requireActivity().runOnUiThread {
                            iOSDialogBuilder(requireContext())
                                .setTitle(requireContext().getString(R.string.confirm_otp))
                                .setSubtitle(requireContext().getString(R.string.update_username_success))
                                .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                                    it.dismiss()
                                    isDataChanged = false
                                }.build().show()
                        }
                    }
                }
            } else {
                requireActivity().runOnUiThread {
                    iOSDialogBuilder(requireContext())
                        .setTitle(requireContext().getString(R.string.request_err))
                        .setSubtitle(requireContext().getString(R.string.confirm_otp_err_occur_msg))
                        .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                            it.dismiss()
                        }.build().show()
                }
            }
        }

        override fun onFailure(call: Call<UpdateUserResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()

                iOSDialogBuilder(requireContext())
                    .setTitle(requireContext().getString(R.string.request_err))
                    .setSubtitle(requireContext().getString(R.string.err_network_not_connected))
                    .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                        it.dismiss()
                    }.build().show()
            }
            Log.e(UpdateProfileFragment::class.java.name, t.message ?: "error message")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}