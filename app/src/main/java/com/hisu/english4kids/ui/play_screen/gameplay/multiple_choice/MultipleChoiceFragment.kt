package com.hisu.english4kids.ui.play_screen.gameplay.multiple_choice

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.hisu.english4kids.MainActivity
import com.hisu.english4kids.R
import com.hisu.english4kids.data.ATTACHMENT_TYPE_AUDIO
import com.hisu.english4kids.data.ATTACHMENT_TYPE_IMAGE
import com.hisu.english4kids.data.ATTACHMENT_TYPE_NONE
import com.hisu.english4kids.data.model.exam.Answer
import com.hisu.english4kids.data.model.exam.ExamQuestion
import com.hisu.english4kids.databinding.FragmentMultipleChoiceBinding

class MultipleChoiceFragment(
    private val nextQuestionListener: (answer: Answer, position: Int, isCorrect: Boolean) -> Unit,
    var examQuestion: ExamQuestion,
    var questionPosition: Int
) : Fragment() {

    private var _binding: FragmentMultipleChoiceBinding? = null
    private val binding get() = _binding!!

    private val _result = MutableLiveData<Boolean>()
    private val result: LiveData<Boolean> = _result
    private lateinit var resultAnswer: Answer
    private var isCorrect: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMultipleChoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initQuestion()
        observeResult()
        setUpMultipleChoiceRecyclerView()
        handleNextQuestionButton()
    }

    private fun initQuestion() {
        binding.tvQuestion.text = String.format(requireContext().getString(R.string.multiple_choice_pattern), questionPosition + 1, examQuestion.question)

        when (examQuestion.attachmentType) {
            ATTACHMENT_TYPE_NONE -> {
                binding.imvQuestionImage.visibility = View.GONE
                binding.ibtnAudioQuestion.visibility = View.GONE
            }
            ATTACHMENT_TYPE_IMAGE -> {
                binding.imvQuestionImage.visibility = View.VISIBLE
                binding.ibtnAudioQuestion.visibility = View.GONE

                Glide.with(requireContext())
                    .asBitmap().load(examQuestion.attachment).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                            binding.imvQuestionImage.setImageBitmap(resource)
                        }
                    })
            }
            ATTACHMENT_TYPE_AUDIO -> {
                binding.imvQuestionImage.visibility = View.GONE
                binding.ibtnAudioQuestion.visibility = View.VISIBLE
                playAudio()
            }
        }
    }

    private fun playAudio() = binding.ibtnAudioQuestion.setOnClickListener {
        (requireActivity() as MainActivity).playAudio(examQuestion.attachment)
    }

    private fun setUpMultipleChoiceRecyclerView() = binding.rvChoices.apply {
        val questionAdapter = MultipleChoiceAdapter(requireContext(), ::handleItemClick)
        questionAdapter.answers = examQuestion.answers
        adapter = questionAdapter
    }

    private fun handleItemClick(answer: Answer) {
        _result.postValue(true)
        resultAnswer = answer
        isCorrect = answer.answerId == examQuestion.correctAnswer
    }

    private fun observeResult() {
        result.observe(viewLifecycleOwner) { binding.btnNextQuestion.isEnabled = it == true }
    }
    private fun handleNextQuestionButton() = binding.btnNextQuestion.setOnClickListener {
        nextQuestionListener.invoke(resultAnswer, questionPosition, isCorrect)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}