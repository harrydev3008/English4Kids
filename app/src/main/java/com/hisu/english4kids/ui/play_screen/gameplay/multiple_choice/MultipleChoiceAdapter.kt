package com.hisu.english4kids.ui.play_screen.gameplay.multiple_choice

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.exam.Answer
import com.hisu.english4kids.databinding.LayoutMultipleChoiceItemBinding
import com.hisu.english4kids.utils.MyUtils

class MultipleChoiceAdapter(
    var context: Context,
    private val itemTapListener: (item: Answer) -> Unit
) : RecyclerView.Adapter<MultipleChoiceAdapter.MultipleChoiceViewHolder>() {

    var answers = listOf<Answer>()

    private var prevPick: TextView? = null
    private var curPick: TextView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleChoiceViewHolder {
        return MultipleChoiceViewHolder(
            LayoutMultipleChoiceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MultipleChoiceViewHolder, position: Int) {
        val answer = answers[position]
        holder.bindData(answer, position)

        holder.apply {

            binding.tvAnswerChoice.setOnClickListener {

                curPick = binding.tvAnswerChoice

                prevPick?.apply {
                    background = ContextCompat.getDrawable(context, R.drawable.bg_word_border)
                    setTextColor(ContextCompat.getColor(context, R.color.gray))
                }

                curPick?.apply {
                    background = ContextCompat.getDrawable(context, R.drawable.bg_word_border_selected_competitive)
                    setTextColor(ContextCompat.getColor(context, R.color.competitive))
                }

                prevPick = binding.tvAnswerChoice

                itemTapListener.invoke(answer)
            }
        }

    }

    override fun getItemCount(): Int = answers.size

    inner class MultipleChoiceViewHolder(var binding: LayoutMultipleChoiceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: Answer, position: Int) = binding.apply {
            tvAnswerChoice.text = String.format(context.getString(R.string.answer_multiple_choice_pattern), MyUtils.renderAnswerAlphabetIndex(position), data.content)
        }
    }
}