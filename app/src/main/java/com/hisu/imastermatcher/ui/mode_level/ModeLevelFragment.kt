package com.hisu.imastermatcher.ui.mode_level

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentModeLevelBinding
import com.hisu.imastermatcher.model.Card
import com.hisu.imastermatcher.model.Level


class ClassModeLevelFragment : Fragment() {

    private var _binding: FragmentModeLevelBinding? = null
    private val binding get() = _binding!!
    private val myNavArgs: ClassModeLevelFragmentArgs by navArgs()
    private var levelAdapter: LevelAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModeLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mode = myNavArgs.mode

        if (mode == 0)
            binding.tvMode.text = getString(R.string.level_classic)
        else if (mode == 1)
            binding.tvMode.text = getString(R.string.level_timer)

        continueLevel()
        backToHomePage()
        setUpLevels()

        loadLevel()
    }

    private fun backToHomePage() = binding.btnHome.setOnClickListener {
        activity?.onBackPressed()
    }

    private fun continueLevel() = binding.btnContinue.setOnClickListener {
//        val action = ClassModeLevelFragmentDirections.modeToPlay(myNavArgs.mode)
//        findNavController().navigate(action)
        findNavController().navigate(R.id.mode_to_play)
    }

    private fun setUpLevels() = binding.rvLevels.apply {
        levelAdapter = LevelAdapter() {


            val cards = mutableListOf<Card>()

            val action = ClassModeLevelFragmentDirections.modeToPlay(mode = myNavArgs.mode, level = it.id)
            findNavController().navigate(action)
//            findNavController().navigate(R.id.mode_to_play)
        }

        adapter = levelAdapter
        setHasFixedSize(true)
    }

    private fun loadLevel() {
        val cards = listOf(
            Card(1, "1", R.drawable.img_test_1, false),
            Card(2, "3", R.drawable.img_test_3, false),
            Card(3, "2", R.drawable.img_test_2, false),
            Card(4, "4", R.drawable.img_test_4, false),
            Card(5, "-1", -1, true),
            Card(6, "4", R.drawable.img_test_4, false),
            Card(7, "3", R.drawable.img_test_3, false),
            Card(8, "2", R.drawable.img_test_2, false),
            Card(9, "1", R.drawable.img_test_1, false)
        )


        val levels = mutableListOf<Level>(
            Level(1, cards, 1, 3f),
            Level(2, cards, 1, 2.5f),
            Level(3, cards, 1, 2f),
            Level(4, cards, 0, 0f),
            Level(5, cards, -1, 0f)
        )

        levelAdapter?.items = levels
    }
}