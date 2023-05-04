package com.hisu.english4kids.ui.lessons

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hisu.english4kids.R
import com.hisu.english4kids.data.BUNDLE_LESSON_DATA
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.model.course.Lesson
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.LessonResponseModel
import com.hisu.english4kids.databinding.FragmentLessonsBinding
import com.hisu.english4kids.utils.local.LocalDataManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LessonsFragment : Fragment() {

    private var _binding: FragmentLessonsBinding? = null
    private val binding get() = _binding!!
    private val myNavArgs: LessonsFragmentArgs by navArgs()
    private var levelAdapter: LessonAdapter? = null

    private lateinit var localDataManager: LocalDataManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localDataManager = LocalDataManager()
        localDataManager.init(requireContext())

        val mode = myNavArgs.mode
        binding.tvMode.text = mode

        backToHomePage()
        setUpLevels()
        loadLevel()
    }

    private fun backToHomePage() = binding.btnHome.setOnClickListener {
        findNavController().popBackStack()
//        findNavController().navigate(R.id.action_classModeLevelFragment_to_courseFragment)
    }

    private fun setUpLevels() = binding.rvLevels.apply {
        levelAdapter = LessonAdapter(requireContext(), ::handleLessonClick)
        adapter = levelAdapter
        setHasFixedSize(true)
    }

    private fun handleLessonClick(lesson: Lesson) {
        lesson.rounds.size
        val bundle = Bundle()
        bundle.putString(BUNDLE_LESSON_DATA, Gson().toJson(lesson.rounds))
        findNavController().navigate(R.id.action_classModeLevelFragment_to_gamePlayProgressFragment, bundle)
    }

    private fun loadLevel() {
        API.apiService.getLessonByCourseId(
            "Bearer ${localDataManager.getUserAccessToken()}",
            myNavArgs.courseId
        ).enqueue(handleGetLessonCallback)
    }

    private val handleGetLessonCallback = object : Callback<LessonResponseModel> {
        override fun onResponse(
            call: Call<LessonResponseModel>,
            response: Response<LessonResponseModel>
        ) {
            if (response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {
                    this.data?.apply {
                        levelAdapter?.lessons = this.lession
                        levelAdapter?.notifyDataSetChanged()
                        binding.rvLevels.adapter = levelAdapter
                    }
                }
            }
        }

        override fun onFailure(call: Call<LessonResponseModel>, t: Throwable) {
            requireActivity().runOnUiThread {
                iOSDialogBuilder(requireContext())
                    .setTitle(requireContext().getString(R.string.request_err))
                    .setSubtitle(requireContext().getString(R.string.err_network_not_connected))
                    .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                        it.dismiss()
                    }.build().show()
            }
            Log.e(LessonsFragment::class.java.name, t.message ?: "error message")
        }
    }
}