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
import com.google.gson.reflect.TypeToken
import com.hisu.english4kids.R
import com.hisu.english4kids.data.BUNDLE_LESSON_DATA
import com.hisu.english4kids.data.STATUS_OK
import com.hisu.english4kids.data.model.course.Course
import com.hisu.english4kids.data.model.course.Lesson
import com.hisu.english4kids.data.network.API
import com.hisu.english4kids.data.network.response_model.DataLesson
import com.hisu.english4kids.data.network.response_model.LessonResponseModel
import com.hisu.english4kids.databinding.FragmentLessonsBinding
import com.hisu.english4kids.utils.MyUtils
import com.hisu.english4kids.utils.local.LocalDataManager
import com.hisu.english4kids.widget.dialog.StartRoundDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LessonsFragment : Fragment() {

    private var _binding: FragmentLessonsBinding? = null
    private val binding get() = _binding!!
    private val myNavArgs: LessonsFragmentArgs by navArgs()
    private var lessonsAdapter: LessonAdapter? = null
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

        binding.tvMode.text = myNavArgs.title

        backToHomePage()
        setUpLevels()
        loadLevel()
    }

    private fun backToHomePage() = binding.tvMode.setOnClickListener {
        findNavController().popBackStack()
//        findNavController().navigate(R.id.action_classModeLevelFragment_to_courseFragment)
    }

    private fun setUpLevels() = binding.rvLessons.apply {
        lessonsAdapter = LessonAdapter(requireContext(), ::handleLessonClick)
        adapter = lessonsAdapter
    }

    private fun handleLessonClick(lesson: Lesson, position: Int) {
        if(MyUtils.isNetworkAvailable(requireContext())) {

            val startRoundDialog = StartRoundDialog(
                requireContext(),
                String.format(
                    requireContext().getString(R.string.round_pattern), position + 1, lesson.rounds.size
                )
            )

            startRoundDialog.setStartBtnEvent {
                val bundle = Bundle()
                bundle.putString(
                    BUNDLE_LESSON_DATA,
                    Gson().toJson(lesson.rounds.slice(position until lesson.rounds.size))
                )
                findNavController().navigate(R.id.action_classModeLevelFragment_to_playFragment, bundle)
            }

            startRoundDialog.showDialog()

        } else {
            requireActivity().runOnUiThread {
                iOSDialogBuilder(requireContext())
                    .setTitle(requireContext().getString(R.string.err_network_not_available))
                    .setSubtitle(requireContext().getString(R.string.err_connect_internet_to_learn))
                    .setPositiveListener(requireContext().getString(R.string.confirm_otp)) {
                        it.dismiss()
                    }.build().show()
            }
        }
    }

    private fun loadLevel() {
        if (MyUtils.isNetworkAvailable(requireContext()))
            API.apiService.getLessonByCourseId(
                "Bearer ${localDataManager.getUserAccessToken()}",
                myNavArgs.courseId
            ).enqueue(handleGetLessonCallback)
        else {
            val localLessons = Gson().fromJson(localDataManager.getLessonsInfo(), DataLesson::class.java)
            lessonsAdapter?.lessons = localLessons.lession
            lessonsAdapter?.notifyDataSetChanged()
            binding.rvLessons.adapter = lessonsAdapter
        }
    }

    private val handleGetLessonCallback = object : Callback<LessonResponseModel> {
        override fun onResponse(
            call: Call<LessonResponseModel>,
            response: Response<LessonResponseModel>
        ) {
            if (response.isSuccessful && response.code() == STATUS_OK) {
                response.body()?.apply {
                    this.data?.apply {
                        lessonsAdapter?.lessons = this.lession
                        lessonsAdapter?.notifyDataSetChanged()
                        binding.rvLessons.adapter = lessonsAdapter
                        localDataManager.setLessonsInfo(Gson().toJsonTree(this).toString())
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