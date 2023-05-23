package com.hisu.english4kids.ui.play_screen.gameplay.multiple_choice

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.hisu.english4kids.MainActivity
import com.hisu.english4kids.R
import com.hisu.english4kids.data.ATTACHMENT_TYPE_AUDIO
import com.hisu.english4kids.data.ATTACHMENT_TYPE_IMAGE
import com.hisu.english4kids.data.ATTACHMENT_TYPE_NONE
import com.hisu.english4kids.data.model.exam.ExamQuestion
import com.hisu.english4kids.databinding.FragmentReviewQuestionBinding
import com.hisu.english4kids.utils.MyUtils

class ReviewQuestionFragment : Fragment() {

    private var _binding: FragmentReviewQuestionBinding?= null
    private val binding get() = _binding!!
    private val myNavArgs: ReviewQuestionFragmentArgs by navArgs()

    private lateinit var examQuestion: ExamQuestion
    private var answersTextView = mutableListOf<TextView>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReviewQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        examQuestion = Gson().fromJson(myNavArgs.question, ExamQuestion::class.java)

        initQuestion()
        initAnswerUI()
        handleBackButton()
        renderAnswer()
    }

    private fun initQuestion() {
        binding.tvQuestion.text = String.format(requireContext().getString(R.string.multiple_choice_pattern_review), examQuestion.question)

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

    private fun initAnswerUI() {
        answersTextView.add(binding.tvAnswerA)
        answersTextView.add(binding.tvAnswerB)
        answersTextView.add(binding.tvAnswerC)
        answersTextView.add(binding.tvAnswerD)
    }

    private fun handleBackButton() = binding.btnBack.setOnClickListener {
        findNavController().popBackStack()
    }

    private fun renderAnswer() = binding.apply {
        for(i in 0 until examQuestion.answers.size) {

            answersTextView[i].text = String.format(requireContext().getString(R.string.answer_multiple_choice_pattern), MyUtils.renderAnswerAlphabetIndex(i), examQuestion.answers[i].content)

            if(examQuestion.userPickAnswer != examQuestion.correctAnswer && examQuestion.answers[i].answerId == examQuestion.userPickAnswer) {
                answersTextView[i].setTextColor(ContextCompat.getColor(requireContext(), R.color.text_incorrect))
                answersTextView[i].background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_word_border_selected_red)
                answersTextView[i].setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_circle, 0)
            } else if((examQuestion.userPickAnswer == examQuestion.correctAnswer && examQuestion.answers[i].answerId == examQuestion.correctAnswer) || (examQuestion.userPickAnswer != examQuestion.correctAnswer && examQuestion.answers[i].answerId == examQuestion.correctAnswer)) {
                answersTextView[i].setTextColor(ContextCompat.getColor(requireContext(), R.color.text_correct))
                answersTextView[i].background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_word_border_selected_green)
                answersTextView[i].setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_round_check_circle_24, 0)
            }

            answersTextView[i].visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}