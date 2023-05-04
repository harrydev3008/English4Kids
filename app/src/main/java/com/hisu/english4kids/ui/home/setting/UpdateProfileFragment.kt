package com.hisu.english4kids.ui.home.setting

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.gdacciaro.iOSDialog.iOSDialog
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.Gson
import com.hisu.english4kids.R
import com.hisu.english4kids.data.network.response_model.Player
import com.hisu.english4kids.databinding.FragmentUpdateProfileBinding
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.widget.dialog.LoadingDialog

class UpdateProfileFragment : Fragment() {

    private var _binding: FragmentUpdateProfileBinding?= null
    private val binding get() = _binding!!

    private lateinit var currentUser: Player
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var localDataManager: LocalDataManager

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
        handleExitButton()
    }

    private fun initView() {
        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)

        localDataManager = LocalDataManager()
        localDataManager.init(requireContext())

        val playJson = localDataManager.getUserInfo()

        if(playJson == null || playJson.isEmpty()) return

        currentUser = Gson().fromJson(playJson, Player::class.java)

        binding.edtUserName.setText(currentUser.username)
        binding.edtUserName.hint = currentUser.username

        binding.edtPhoneNumber.setText(currentUser.phone)
        binding.edtPhoneNumber.hint = currentUser.phone

        binding.cimvUserPfp.setImageBitmap(MyUtils.createImageFromText(requireContext(), currentUser.username))
    }

    private fun handleBackButton() = binding.btnBack.setOnClickListener {
        findNavController().popBackStack()
    }

    private fun handleSaveButton() = binding.btnSave.setOnClickListener {
        if(MyUtils.isNetworkAvailable(requireContext())) {
            handleSaveUserInfoEvent()
        } else {
            iOSDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.confirm_otp))
                .setSubtitle(requireContext().getString(R.string.err_network_not_available))
                .setPositiveListener(requireContext().getString(R.string.confirm_otp), iOSDialog::dismiss).build().show()
        }
    }

    private fun handleSaveUserInfoEvent() {
        //todo: call api
    }

    private fun handleExitButton() = binding.btnExit.setOnClickListener {
        //todo: if user made changes, confirm before exit
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}