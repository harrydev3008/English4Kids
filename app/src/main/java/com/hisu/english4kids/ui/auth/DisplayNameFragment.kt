package com.hisu.english4kids.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hisu.english4kids.R
import com.hisu.english4kids.data.CONTENT_TYPE_JSON
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.AuthResponseModel
import com.hisu.english4kids.databinding.FragmentDisplayNameBinding
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.widget.dialog.LoadingDialog
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DisplayNameFragment : Fragment() {

    private var _binding: FragmentDisplayNameBinding?= null
    private val binding get() = _binding!!

    private val myArgs: DisplayNameFragmentArgs by navArgs()
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDisplayNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLoadingDialog = LoadingDialog(requireContext(), Gravity.CENTER)

        handleUserNameTextChange()
        handleSaveButton()
    }

    private fun handleUserNameTextChange() = binding.edtUsername.addTextChangedListener {
        it?.apply {
            binding.btnSave.isEnabled = it.isNotEmpty()
        }
    }

    private fun handleSaveButton() = binding.btnSave.setOnClickListener {
        val jsonObject = JsonObject()
        jsonObject.addProperty("phone", myArgs.phoneNumber)
        jsonObject.addProperty("username", binding.edtUsername.text.toString().trim())

        val registerBodyRequest = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), jsonObject.toString())

        requireActivity().runOnUiThread {
            mLoadingDialog.showDialog()
        }

        API.apiService.authRegister(registerBodyRequest).enqueue(handleRegisterCallback)
    }

    private val handleRegisterCallback = object: Callback<AuthResponseModel> {
        override fun onResponse(call: Call<AuthResponseModel>, response: Response<AuthResponseModel>) {

            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }

            if(response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {
                        this.data?.apply {
                        val localDataManager = LocalDataManager()
                        localDataManager.init(requireContext())

                        val playerInfoJson = Gson().toJson(this.newPlayer)

                        localDataManager.setUserLoinState(true)
                        localDataManager.setUserInfo(playerInfoJson)

                        findNavController().navigate(R.id.action_displayNameFragment_to_homeFragment)
                    }
                }
            } else {
                //todo: impl later
            }
        }

        override fun onFailure(call: Call<AuthResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                mLoadingDialog.dismissDialog()
            }
            Log.e(DisplayNameFragment::class.java.name, t.message ?: "error message")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}