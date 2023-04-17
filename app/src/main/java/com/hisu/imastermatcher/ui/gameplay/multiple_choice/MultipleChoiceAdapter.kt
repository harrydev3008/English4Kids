package com.hisu.imastermatcher.ui.gameplay.multiple_choice

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.LayoutMultipleChoiceItemBinding
import com.hisu.imastermatcher.model.multiple_choice.MultipleChoiceModel

class MultipleChoiceAdapter(
    var context: Context,
    private val itemTapListener: (item: String, position: Int) -> Unit
) : RecyclerView.Adapter<MultipleChoiceAdapter.MultipleChoiceViewHolder>() {

    var choices = listOf<String>()
    var isLockView = false

    private var prevPick: TextView?= null
    private var curPick: TextView?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleChoiceViewHolder {
        return MultipleChoiceViewHolder(
            LayoutMultipleChoiceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MultipleChoiceViewHolder, position: Int) {
        val choice = choices[position]
        holder.bindData(choice, position)

        holder.apply {

            binding.tvAnswerChoice.setOnClickListener {

                if(!isLockView) {

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

                    itemTapListener.invoke(choice, position)
                }
            }
        }

    }

    override fun getItemCount(): Int = choices.size

    inner class MultipleChoiceViewHolder(var binding: LayoutMultipleChoiceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: String, position: Int) = binding.apply {
            tvAnswerChoice.text =
                String.format(
                    context.getString(R.string.answer_multiple_choice_pattern),
                    renderAnswerAlphabetIndex(position),
                    data
                )
        }

        private fun renderAnswerAlphabetIndex(position: Int) = when (position) {
            0 -> "A"
            1 -> "B"
            2 -> "C"
            else -> "D"
        }
    }
}