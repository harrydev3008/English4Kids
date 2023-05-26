package com.hisu.english4kids.splash_screen

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.RenderMode
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hisu.english4kids.MyApplication
import com.hisu.english4kids.R
import com.hisu.english4kids.data.CONTENT_TYPE_JSON
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.AuthResponseModel
import com.hisu.english4kids.data.network.response_model.CourseResponseModel
import com.hisu.english4kids.data.room_db.repository.CourseRepository
import com.hisu.english4kids.data.room_db.repository.PlayerRepository
import com.hisu.english4kids.data.room_db.view_model.CourseViewModel
import com.hisu.english4kids.data.room_db.view_model.CourseViewModelProviderFactory
import com.hisu.english4kids.data.room_db.view_model.PlayerViewModel
import com.hisu.english4kids.data.room_db.view_model.PlayerViewModelProviderFactory
import com.hisu.english4kids.databinding.FragmentSplashScreenBinding
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.utils.local.LocalDataManager
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashScreenFragment : Fragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var localDataManager: LocalDataManager

    private val playerViewModel: PlayerViewModel by activityViewModels() {
        PlayerViewModelProviderFactory(
            PlayerRepository(
                requireActivity().applicationContext,
                (activity?.application as MyApplication).database.playerDAO()
            )
        )
    }

    private val courseViewModel: CourseViewModel by activityViewModels() {
        CourseViewModelProviderFactory(
            CourseRepository(
                requireActivity().applicationContext,
                (activity?.application as MyApplication).database.courseDAO()
            )
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localDataManager = LocalDataManager()
        localDataManager.init(requireContext())

        //make lottie less laggy
        binding.splashView.renderMode = RenderMode.HARDWARE

        val userLoginState = getLoginStatus()

        if(userLoginState) {
            Handler(requireContext().mainLooper).postDelayed({
                if(MyUtils.isNetworkAvailable(requireContext())) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("refreshToken", localDataManager.getUserRefreshToken())

                    val ssoBodyRequest = RequestBody.create(MediaType.parse(CONTENT_TYPE_JSON), jsonObject.toString())

                    API.apiService.checkSSO(ssoBodyRequest).enqueue(handleCheckSSOCallback)
                } else {
                    findNavController().navigate(R.id.splash_to_home)
                }
            }, 4 * 1000)
        } else {
            Handler(requireContext().mainLooper).postDelayed({
                findNavController().navigate(R.id.splash_to_login)
            }, 3 * 1000)
        }
    }

    private val handleCheckSSOCallback = object : Callback<AuthResponseModel> {
        override fun onResponse(call: Call<AuthResponseModel>, response: Response<AuthResponseModel>) {
            if (response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {
                    this.data.apply {

                        val playerInfoJson = Gson().toJson(this.player)
                        localDataManager.setUserLoinState(true)
                        localDataManager.setUserInfo(playerInfoJson)
                        localDataManager.setUserAccessToken(this.accessToken)

                        playerViewModel.updatePlayer(this.player)
                        API.apiService.getCourses("Bearer ${this.accessToken}").enqueue(handleGetCourseCallback)
                    }
                }
            } else {
                localDataManager.setUserLoinState(false)
                findNavController().navigate(R.id.splash_to_login)
            }
        }

        override fun onFailure(call: Call<AuthResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                iOSDialogBuilder(requireContext())
                    .setTitle(requireContext().getString(R.string.request_err))
                    .setSubtitle(requireContext().getString(R.string.err_network_not_connected))
                    .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                        it.dismiss()
                    }.build().show()
            }
            Log.e(SplashScreenFragment::class.java.name, t.message ?: "error message")
        }
    }

    private val handleGetCourseCallback = object : Callback<CourseResponseModel> {
        override fun onResponse(call: Call<CourseResponseModel>, response: Response<CourseResponseModel>) {
            if (response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {

                    courseViewModel.insertCourses(this.data.courses)

                    Handler(requireContext().mainLooper).postDelayed({
                        findNavController().navigate(R.id.splash_to_home)
                    }, 1000)
                }
            }
        }

        override fun onFailure(call: Call<CourseResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                iOSDialogBuilder(requireContext())
                    .setTitle(requireContext().getString(R.string.request_err))
                    .setSubtitle(requireContext().getString(R.string.err_network_not_connected))
                    .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                        it.dismiss()
                    }.build().show()
            }
            Log.e(SplashScreenFragment::class.java.name, t.message ?: "error message")
        }
    }

    private fun getLoginStatus(): Boolean {
        return localDataManager.getUserLoginState()
    }
}