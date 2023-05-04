package com.hisu.english4kids.ui.play_screen.gameplay.type_answer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.game_play.GameStyleFour
import com.hisu.english4kids.databinding.FragmentTypeAnswerBinding

class TypeAnswerFragment(
    private val itemTapListener: () -> Unit,
    private val wrongAnswerListener: () -> Unit,
    private val correctAnswerListener: (score: Int) -> Unit,
    private val gameStyleFour: GameStyleFour
) : Fragment() {

    private var _binding: FragmentTypeAnswerBinding? = null
    private val binding get() = _binding!!
    private val _result = MutableLiveData<Boolean>()
    private val result: LiveData<Boolean> = _result

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTypeAnswerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvQuestion.text = gameStyleFour.question

        result.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.GONE
                binding.btnCheck.tvCorrect.visibility = View.VISIBLE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.correct))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_correct))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
                correctAnswerListener.invoke(gameStyleFour.score)
            } else {
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.VISIBLE
                binding.btnCheck.tvCorrect.visibility = View.GONE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.incorrect))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_incorrect))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
                wrongAnswerListener.invoke()
            }
        }

        checkAnswer()
        handleEditTextChange()
    }

    private fun checkAnswer() = binding.btnCheck.btnNextRound.setOnClickListener {
        if (binding.btnCheck.btnNextRound.text.equals(requireContext().getString(R.string.check))) {
            if (binding.edtAnswer.text.toString().trim().equals(gameStyleFour.correctAns, ignoreCase = true)) {
                _result.postValue(true)
            } else {
                _result.postValue(false)
                binding.btnCheck.tvCorrectAnswer.text = gameStyleFour.correctAns
            }
        } else if (binding.btnCheck.btnNextRound.text.equals(requireContext().getString(R.string.next))) {
            binding.edtAnswer.setText("")
            itemTapListener.invoke()
        }
    }

    private fun handleEditTextChange() = binding.edtAnswer.addTextChangedListener {
        if (it.toString().isNotEmpty()) {
            binding.btnCheck.btnNextRound.isEnabled = true
            binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.check)
            binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.classic))
            binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
        } else {
            binding.btnCheck.btnNextRound.isEnabled = false
            binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.check)
            binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.gray_e5))
            binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.gray_af))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}