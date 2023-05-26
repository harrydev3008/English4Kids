package com.hisu.english4kids.ui.play_screen.gameplay.multiple_choice

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.exam.ExamQuestion
import com.hisu.english4kids.databinding.LayoutReviewQuestionHeaderItemBinding
import com.hisu.english4kids.utils.MyUtils

class ReviewAnswerAdapter(var context: Context, private var itemClick: (exam: ExamQuestion) -> Unit): RecyclerView.Adapter<ReviewAnswerAdapter.ReviewQuestionViewHolder>() {

    var questions = listOf<ExamQuestion>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewQuestionViewHolder {
        return ReviewQuestionViewHolder(
            LayoutReviewQuestionHeaderItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ReviewQuestionViewHolder, position: Int) {
        holder.bindData(questions[position], position)
    }

    override fun getItemCount(): Int = questions.size

    inner class ReviewQuestionViewHolder(var binding: LayoutReviewQuestionHeaderItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: ExamQuestion, position: Int) = binding.apply {
            tvQuestion.text = String.format(context.getString(R.string.review_answer_pattern), position + 1)

            llQuestionContainer.setOnClickListener {
                itemClick.invoke(data)
            }

            for(i in 0 until data.answers.size) {
                val textView = TextView(context)
                textView.setTextColor(context.getColor(R.color.white))
                textView.width = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._25sdp)
                textView.height = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._25sdp)
                textView.gravity = Gravity.CENTER
                textView.textSize = 14f
                textView.text = MyUtils.renderAnswerAlphabetIndex(i)

                if(data.userPickAnswer != data.correctAnswer && data.answers[i].answerId == data.userPickAnswer) {
                    textView.background = ContextCompat.getDrawable(context, R.drawable.container_question_wrong)
                }
                else if((data.userPickAnswer == data.correctAnswer && data.answers[i].answerId == data.correctAnswer) || (data.userPickAnswer != data.correctAnswer && data.answers[i].answerId == data.correctAnswer)) {
                    textView.background = ContextCompat.getDrawable(context, R.drawable.container_question_correct)
                }
                else {
                    textView.background = ContextCompat.getDrawable(context, R.drawable.container_question)
                }

                answerContainer.addView(textView)
            }
        }
    }
}