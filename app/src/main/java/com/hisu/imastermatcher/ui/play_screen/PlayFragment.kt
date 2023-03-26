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
import com.hisu.imastermatcher.ui.play_style.SentenceStyleFragment
import org.json.JSONObject

class PlayFragment : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!

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
        val levelPlaceHolder = "Dịch Câu này"

//        if (mode == 0)
//            levelPlaceHolder = "${getString(R.string.mode_classic)} ${lvl}"
//        else if (mode == 1)
//            levelPlaceHolder = "${getString(R.string.mode_timer)} ${lvl}"

        binding.tvModeLevel.text = levelPlaceHolder

        pauseGame()

        binding.pbStar.max = 4

        inflateRoundGamePlay(SentenceStyleFragment())
    }

    private fun inflateRoundGamePlay(fragment: Fragment) {
        val ft = childFragmentManager.beginTransaction()
        ft.add(binding.flRoundContainer.id, fragment, fragment.javaClass.name)
        ft.commitAllowingStateLoss()
    }

    private fun pauseGame() = binding.ibtnClose.setOnClickListener {
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}