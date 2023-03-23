package com.hisu.imastermatcher.ui.play_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentPlayBinding
import org.json.JSONObject

class PlayFragment : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!

    private var isCorrect = false
    private val _result = MutableLiveData<Boolean>(true)
    val result: LiveData<Boolean> = _result

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        initRecyclerView()
//
//        val lvl = myNavArgs.level
//        val mode = myNavArgs.mode
        var levelPlaceHolder = ""

//        if (mode == 0)
//            levelPlaceHolder = "${getString(R.string.mode_classic)} ${lvl}"
//        else if (mode == 1)
//            levelPlaceHolder = "${getString(R.string.mode_timer)} ${lvl}"

        binding.tvModeLevel.text = levelPlaceHolder

        pauseGame()

        binding.pbStar.max = 4

        result.observe(viewLifecycleOwner) {
            if(it == true) {
                binding.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.correct))
                binding.containerWrong.visibility = View.GONE
                binding.tvCorrect.visibility = View.VISIBLE
                binding.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_correct))
                binding.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
            } else {
                binding.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.incorrect))
                binding.containerWrong.visibility = View.VISIBLE
                binding.tvCorrect.visibility = View.GONE
                binding.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_incorrect))
                binding.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
            }
        }

        nextRound()
    }

    private fun pauseGame() = binding.ibtnClose.setOnClickListener {
        findNavController().popBackStack()
//        if(result.value == true) {
//            _result.postValue(false)
//        } else
//            _result.postValue(true)
    }

    private fun nextRound() = binding.btnNextRound.setOnClickListener {
        var resultJson = JSONObject()
        resultJson.put("total_score", 9)
        resultJson.put("fast_score", "1:33")
        resultJson.put("perfect_score", 99)

        if(result.value == true) {
            val action = PlayFragmentDirections.gameFinish(resultJson.toString())
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}