package com.hisu.english4kids.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.hisu.english4kids.R
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.model.course.Course
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.CourseResponseModel
import com.hisu.english4kids.databinding.FragmentCourseBinding
import com.hisu.english4kids.utils.local.LocalDataManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseFragment : Fragment() {

    private var _binding: FragmentCourseBinding? = null
    private lateinit var courseAdapter: CourseItemViewPagerAdapter
    private val binding get() = _binding!!

    private lateinit var coursesResponse: List<Course>
    private lateinit var localDataManager: LocalDataManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localDataManager = LocalDataManager()
        localDataManager.init(requireContext())

        initFeatureMovieList()
        backToHomePage()

        getCourses()
    }

    private fun getCourses() {
        API.apiService.getCourses("Bearer ${localDataManager.getUserAccessToken()}").enqueue(handleGetCourseCallback)
    }

    private fun initFeatureMovieList() = binding.vpCourses.apply {
        courseAdapter = CourseItemViewPagerAdapter(requireContext()) {
            val action = CourseFragmentDirections.courseToLevel(
                mode = it.title,
                courseId = it._id
            )
            findNavController().navigate(action)
        }

        adapter = courseAdapter
    }

    private fun backToHomePage() = binding.btnHomepage.setOnClickListener {
        findNavController().navigate(R.id.course_to_home)
    }

    private val handleGetCourseCallback = object : Callback<CourseResponseModel> {
        override fun onResponse(call: Call<CourseResponseModel>, response: Response<CourseResponseModel>) {
            if (response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {
                    this.data?.apply {
                        coursesResponse = this.courses
                        courseAdapter.courses = coursesResponse
                        courseAdapter.notifyDataSetChanged()
                        binding.vpCourses.adapter = courseAdapter
                    }
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
            Log.e(CourseFragment::class.java.name, t.message ?: "error message")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}